package dev.sebastianb.owocraft.client.owo_api.impl.bindings;

import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;

import net.fabricmc.loader.api.FabricLoader;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public enum PythonRunnerManagerImpl implements PythonRunnerManager {
    INSTANCE;

    class PythonScriptInformation {
        public final String script;
        public final int priority;

        public PythonScriptInformation(String script, String eventName, int priority) {
            this.script = script;
            this.priority = priority;
        }
    }

    private final Map<String, PythonScriptInformation> allLoadedScripts = new HashMap<>();

    private static final PythonInterpreter INTERPRETER = new PythonInterpreter();

    private final String pythonScriptPath = FabricLoader.getInstance().getConfigDir().resolve("owocraft/python/").toString();

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

    @Override
    public void runPythonScript(String modID, String event, Object... args) {
        PythonScriptInformation scriptInfo = allLoadedScripts.get(modID + ":" + event);

        if (event != null && !allRunningScripts.containsKey(event) && allRunningScripts.isEmpty()) {
            prevEventPriority = scriptInfo.priority; // sets priority when this is supposed to fire

            runPythonCodeFromEvent(event, args, scriptInfo);

        } else if (event == null) {
            Owocraft.getLogger().log(Level.WARNING, "Python script for event " + event + " is not initialized!");
        } else { // allRunningScripts.isEmpty()
            // when the existing event is higher, this should run
            if (scriptInfo.priority > prevEventPriority) {
                allRunningScripts.values().forEach(Thread::interrupt);
                allRunningScripts.clear();
                System.out.println("HAS A HIGHER PRIORITY, STOPPED ALL THREADS");
                runPythonCodeFromEvent(event, args, scriptInfo);
            }
        }
    }

    private void runPythonCodeFromEvent(String event, Object[] args, PythonScriptInformation scriptInfo) {
        var pyScript = new Thread(() -> {
            try {
                for (Object arg : args) {
                    INTERPRETER.set(arg.toString(), arg);
                }
                INTERPRETER.exec(scriptInfo.script);

            } catch (PyException pyException) {
                // TODO: proper logging
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
