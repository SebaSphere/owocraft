package dev.sebastianb.owocraft.client.owo_api.impl.bindings;

import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;

import net.fabricmc.loader.api.FabricLoader;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public enum PythonRunnerManagerImpl implements PythonRunnerManager {
    INSTANCE;

    private final Map<String, String> allLoadedScripts = new HashMap<>();

    private final Map<String, String> allRunningScripts = new HashMap<>();

    private static final PythonInterpreter INTERPRETER = new PythonInterpreter();

    private final String pythonScriptPath = FabricLoader.getInstance().getConfigDir().resolve("owocraft/python/").toString();

    @Override
    public boolean initPythonScriptPath(String modID, String scriptName, String eventName) {
        if (allLoadedScripts.containsKey(eventName)) {
            Owocraft.getLogger().log(Level.WARNING, "Python script for event " + eventName + " is already in use!");
            return false;
        }
        try {
            String script = createOrGetScript(modID, scriptName);
            allLoadedScripts.put(modID + ":" + eventName, script);
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

    @Override
    public void stopPythonScript(String eventName) {

    }

    @Override
    public void runPythonScript(String modID, String event, Object... args) {
        String script;
        script = allLoadedScripts.getOrDefault(modID + ":" + event, null);

        if (event != null) {
            // TODO: add to thread process of running threads (alongside the event name hashmap)
            // TODO: add priority system (so it won't run if an event is already running and overrides if so)
            var pyScript = new Thread(() -> {
                for (Object arg : args) {
                    INTERPRETER.set(arg.toString(), arg);
                }
                INTERPRETER.exec(script);
            });

            pyScript.start();

        } else {
            Owocraft.getLogger().log(Level.WARNING, "Python script for event " + event + " is not initialized!");
        }
    }
}
