package dev.sebastianb.owocraft.client.owo_api.impl.bindings.python;

import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;

import net.fabricmc.loader.api.FabricLoader;
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
import java.util.Random;
import java.util.logging.Level;

public enum PythonRunnerManagerImpl implements PythonRunnerManager {
    INSTANCE;

    // TODO: make this just seperated into this class only
    public HashMap<String, PythonScriptInformation> allLoadedScripts = new HashMap<>();

    private static final PythonInterpreter INTERPRETER = new PythonInterpreter();

    private final String pythonScriptPath = FabricLoader.getInstance().getConfigDir().resolve("owocraft/python/").toString();

    @Override
    public HashMap<String, PythonScriptInformation> getPythonScripts() {
        return allLoadedScripts;
    }

    @Override
    public void reloadWithScript(HashMap<String, PythonScriptInformation> pythonScriptInformationHashMap) {

        // so I want to take the python script and replace all null values inside pythonScriptInformationHashMap with what's already in allLoadedScripts

        pythonScriptInformationHashMap.forEach((key, value) -> {

            for (Field field : PythonScriptInformation.class.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (field.get(value) == null) {
                        PythonScriptInformation existingScriptInfo = allLoadedScripts.get(key);
                        if (existingScriptInfo != null) {
                            field.set(value, field.get(existingScriptInfo));
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error replacing null values...", e);
                }
            }

        });

        allLoadedScripts = pythonScriptInformationHashMap;


        allRunningScripts.values().forEach(Thread::interrupt);
        allRunningScripts.clear();
    }

    @Override
    public boolean initPythonScriptPath(String modID, String scriptName, String eventName, int priority) {
        if (allLoadedScripts.containsKey(eventName)) {
            Owocraft.getLogger().log(Level.WARNING, "Python script for event " + eventName + " is already in use!");
            return false;
        }
        try {
            String script = createOrGetScript(modID, scriptName);
            allLoadedScripts.put(modID + ":" + eventName, new PythonScriptInformation(script, eventName, priority));
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
            System.out.println(scriptPath);
            Files.createFile(scriptPath);
        }
        String script = Files.readString(scriptPath);
        return script;
    }

    @Override
    public void stopAllPythonScripts() {

    }

    private final Map<String, Thread> allRunningScripts = new HashMap<>();

    @Override
    public void stopPythonScript(String eventName) {
        if (allRunningScripts.containsKey(eventName)) {
            allRunningScripts.get(eventName).interrupt();
        }
    }

    // this is kinda bad but it works
    int prevEventPriority = 0;


    // TODO: see why it barely pops up for a second
    @Override
    public void runPythonScript(String modID, String event, Object... args) {
        PythonScriptInformation scriptInfo = allLoadedScripts.get(modID + ":" + event);

        if (event != null && !allRunningScripts.containsKey(event) && allRunningScripts.isEmpty()) {

            prevEventPriority = scriptInfo.priority; // sets priority when this is supposed to fire

            System.out.println("RUNNING SCRIPT FOR EVENT: " + event);
            System.out.println(scriptInfo);
            runPythonCodeFromEvent(event, args, scriptInfo);

        } else if (event == null) {
            Owocraft.getLogger().log(Level.WARNING, "Python script for event " + event + " is not initialized!");
        } else { // allRunningScripts.isEmpty()
            if (scriptInfo.priority >= prevEventPriority && !scriptInfo.shouldFinishEventFirst) {
                // not relevant as the INTERPRETER is static (meaning it can only block one script at a time)
                // START
//                allRunningScripts.values().forEach(Thread::interrupt);
//                allRunningScripts.clear();
//                System.out.println("HAS A HIGHER PRIORITY, STOPPED ALL THREADS");
                // END

                // should override a existing script
                runPythonCodeFromEvent(event, args, scriptInfo);
            }
        }
    }

    private void runPythonCodeFromEvent(String event, Object[] args, PythonScriptInformation scriptInfo) {
        var pyScript = new Thread(() -> {
            try {
                int i = 1;
                for (Object arg : args) {
                    // TODO: pass proper arg variable name
                    INTERPRETER.set("variable" + i, arg);
                    i++;
                }
                // FIXME: I could make it call a new interpreter but the issue is sensations start flickering
                // TODO: make it so it can run multiple scripts at once
                INTERPRETER.exec(scriptInfo.script);

            } catch (PyException pyException) {
                // TODO: proper logging through config
                if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                    pyException.printStackTrace();
                }
            } finally {
                System.out.println("FINISHED THREAD!!!");
                allRunningScripts.remove(event);
            }
        });

        allRunningScripts.put(event, pyScript);
        System.out.println("!!!!!");
        System.out.println(allRunningScripts);

        pyScript.start();
    }
}
