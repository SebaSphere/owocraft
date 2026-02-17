package dev.sebastianb.owocraft.client.owo_api.impl.bindings.python;

import dev.sebastianb.owocraft.CommonOwocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;

import dev.sebastianb.owocraft.config.OwocraftConfig;
import dev.sebastianb.owocraft.services.Services;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public enum PythonRunnerManagerImpl implements PythonRunnerManager {
    INSTANCE;

    // TODO: make this just seperated into this class only
    public HashMap<String, PythonScriptInformation> allLoadedScripts = new HashMap<>();

    private static final PythonInterpreter INTERPRETER = new PythonInterpreter();

    private final String pythonScriptPath = Services.PLATFORM.getConfigDir().resolve("owocraft/python/").toString();

    @Override
    public HashMap<String, PythonScriptInformation> getPythonScripts() {
        return allLoadedScripts;
    }

    @Override
    public void reloadWithScript(HashMap<String, PythonScriptInformation> pythonScriptInformationHashMap) {

        // so I want to take the python script and replace all null values inside pythonScriptInformationHashMap with what's already in allLoadedScripts

        pythonScriptInformationHashMap.forEach((key, value) -> {
            String[] splitKey = key.split(":");
            if (splitKey.length == 2) {
                if (value.modID == null || value.modID.isEmpty()) value.modID = splitKey[0];
                if (value.eventName == null || value.eventName.isEmpty()) value.eventName = splitKey[1];
            }

            for (Field field : PythonScriptInformation.class.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    String lookupKey = value.modID + ":" + value.eventName;
                    PythonScriptInformation existingScriptInfo = allLoadedScripts.get(lookupKey);

                    if (field.get(value) == null) {
                        if (existingScriptInfo != null) {
                            if (field.getName().equals("script")) {
                                field.set(value, existingScriptInfo.script);
                            } else if (field.getName().equals("scriptName")) {
                                field.set(value, existingScriptInfo.scriptName);
                            } else {
                                field.set(value, field.get(existingScriptInfo));
                            }
                        }
                    } else if (field.getName().equals("script") && ((String) field.get(value)).isEmpty()) {
                        // If script is empty (as it might be if it was deserialized from a default constructor/YACL)
                        // but we have it in memory, restore it.
                        if (existingScriptInfo != null && existingScriptInfo.script != null && !existingScriptInfo.script.isEmpty()) {
                            field.set(value, existingScriptInfo.script);
                        }
                    }

                    // Always restore scriptName if it's missing in the new info but present in old
                    if (field.getName().equals("scriptName") && (field.get(value) == null || ((String) field.get(value)).isEmpty())) {
                        if (existingScriptInfo != null && existingScriptInfo.scriptName != null && !existingScriptInfo.scriptName.isEmpty()) {
                            field.set(value, existingScriptInfo.scriptName);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error replacing null values...", e);
                }
            }

        });

        allLoadedScripts = pythonScriptInformationHashMap;


        synchronized (allRunningScripts) {
            allRunningScripts.values().forEach(Thread::interrupt);
            allRunningScripts.clear();
        }
    }

    @Override
    public boolean initPythonScriptPath(String modID, String scriptName, String eventName, int priority) {
        String key = modID + ":" + eventName;
        if (allLoadedScripts.containsKey(key)) {
            CommonOwocraft.getLogger().log(Level.WARNING, "Python script for event " + eventName + " is already in use!");
            return false;
        }
        try {
            String script = createOrGetScript(modID, scriptName);
            PythonScriptInformation info = new PythonScriptInformation(modID, script, eventName, priority);
            info.scriptName = scriptName;
            allLoadedScripts.put(key, info);
            // FIXME: figure out why this sometimes triggers before it's loaded?
            if (OwocraftConfig.pythonConfigInformation != null) {
                OwocraftConfig.pythonConfigInformation.put(key, info);
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error reading the python script...", e);
        }
    }

    private String createOrGetScript(String modID, String scriptName) throws IOException {
        Path scriptPath = Paths.get(pythonScriptPath + "/" + modID + "/" + scriptName);
        if (Files.notExists(scriptPath.getParent())) {
            Files.createDirectories(scriptPath.getParent());
        }
        if (Files.notExists(scriptPath)) {
            Files.createFile(scriptPath);
        }
        String script = Files.readString(scriptPath);
        return script;
    }

    @Override
    public void stopAllPythonScripts() {
        synchronized (allRunningScripts) {
            allRunningScripts.values().forEach(Thread::interrupt);
            allRunningScripts.clear();
        }
    }

    private final Map<String, Thread> allRunningScripts = new HashMap<>();

    @Override
    public void stopPythonScript(String eventName) {
        synchronized (allRunningScripts) {
            if (allRunningScripts.containsKey(eventName)) {
                allRunningScripts.get(eventName).interrupt();
            }
        }
    }

    // this is kinda bad but it works
    int prevEventPriority = 0;



    long currentTime = System.currentTimeMillis();


    long lastEnvScriptTime;

    boolean shouldDelayEnvironmentScripts = false;

    // TODO: make a config
    double ENV_SCRIPT_DELAY = 5000;

    @Override
    public boolean runPythonScript(String modID, String event, PairedVariableArgument... args) {

        PythonScriptInformation scriptInfo = allLoadedScripts.get(modID + ":" + event);


        if (!event.startsWith("environment")) {

            // Check if enough time has passed since the last environmental script was executed
            shouldDelayEnvironmentScripts = true;
            lastEnvScriptTime = System.currentTimeMillis();


        }

        if (event.startsWith("environment")) {
            if (shouldDelayEnvironmentScripts) {
                if (System.currentTimeMillis() - lastEnvScriptTime > ENV_SCRIPT_DELAY) {
                    shouldDelayEnvironmentScripts = false;
                    lastEnvScriptTime = System.currentTimeMillis(); // Update time of last environmental script run

                }
                return false;

            }
        }


        if (event != null && !allRunningScripts.containsKey(event)) {
            if (!scriptInfo.runGenericEventInstead) {
                prevEventPriority = scriptInfo.priority; // sets priority when this is supposed to fire

                runPythonCodeFromEvent(event, args, scriptInfo);
                return false;
            } else {
                PythonScriptInformation genericScript = allLoadedScripts.get(modID + ":" + "generic_event");
                runPythonCodeFromEvent("generic_event", args, genericScript);
            }

        } else if (event == null) {
            CommonOwocraft.getLogger().log(Level.WARNING, "Python script for event " + event + " is not initialized!");
        } else {
            if (scriptInfo != null) {
                if (scriptInfo.priority >= prevEventPriority && !scriptInfo.shouldFinishEventFirst) {
                    // Interrupt all running scripts if the new one has higher or equal priority
                    synchronized (allRunningScripts) {
                        allRunningScripts.values().forEach(Thread::interrupt);
                        allRunningScripts.clear();
                    }

                    if (!scriptInfo.runGenericEventInstead) {
                        runPythonCodeFromEvent(event, args, scriptInfo);
                    } else {
                        PythonScriptInformation genericScript = allLoadedScripts.get(modID + ":" + "generic_event");
                        runPythonCodeFromEvent("generic_event", args, genericScript);
                    }
                    return false;
                } else {
                    // removed log
                }
            }
        }

        return true;
    }

    private String getScriptContent(String modID, String eventName) {
        // Try to find the script name from the loaded info
        String key = modID + ":" + eventName;
        PythonScriptInformation info = allLoadedScripts.get(key);
        if (info == null) return null;

        String scriptName = info.scriptName;
        if (scriptName == null || scriptName.isEmpty()) {
            // Construct the path - assuming scriptName is "event-name-script.py" style
            // We might need to store the scriptName in PythonScriptInformation if it differs
            // For now, let's use the naming convention from OwocraftClient.java
            if (eventName.startsWith("environment-") || eventName.startsWith("event-")) {
                scriptName = eventName + "-script.py";
            } else if (eventName.equals("generic_event")) {
                scriptName = "generic_event-script.py";
            } else {
                // Fallback for damage types if the above didn't catch it
                // Damage types are initialized as "damage-" + path + "-script.py"
                scriptName = "damage-" + eventName + "-script.py";
            }
        }

        try {
            Path scriptPath = Paths.get(pythonScriptPath + "/" + modID + "/" + scriptName);
            if (Files.exists(scriptPath)) {
                String content = Files.readString(scriptPath);
                if (!content.isEmpty()) {
                    // Update the cached script in info so it can be viewed in YACL (if it's ever used there)
                    // but we always prefer reading from file here.
                    info.script = content;
                    return content;
                }
            }
        } catch (IOException e) {
            // Log error?
        }
        return info.script; // Fallback to memory/config script if file read fails or is empty
    }

    private synchronized void runPythonCodeFromEvent(String event, PairedVariableArgument[] args, PythonScriptInformation scriptInfo) {
        String scriptToRun = getScriptContent(scriptInfo.modID, scriptInfo.eventName);
        if (scriptToRun == null || scriptToRun.isEmpty()) {
            return;
        }
        currentTime = System.currentTimeMillis();


        var pyScript = new Thread(() -> {
            synchronized (INTERPRETER) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                try {
                    for (PairedVariableArgument arg : args) {
                        if (Thread.currentThread().isInterrupted()) return;
                        // TODO: pass proper arg variable name
                        INTERPRETER.set(arg.variableName(), arg.object());
                    }
                    // FIXME: I could make it call a new interpreter but the issue is sensations start flickering
                    // TODO: make it so it can run multiple scripts at once
                    if (Thread.currentThread().isInterrupted()) return;
                    INTERPRETER.exec(scriptToRun);


                } catch (PyException pyException) {
                    pyException.printStackTrace();
                    // TODO: proper logging through config
                    if (Services.PLATFORM.isDevelopmentEnvironment()) {
                        pyException.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    synchronized (allRunningScripts) {
                        allRunningScripts.remove(event);
                    }
                }
            }
        });

        synchronized (allRunningScripts) {
            allRunningScripts.put(event, pyScript);
        }

        pyScript.start();
    }

    @Override
    public void refreshAllScriptsFromFiles() {
        allLoadedScripts.forEach((key, info) -> {
            getScriptContent(info.modID, info.eventName);
        });
    }
}
