package dev.sebastianb.owocraft.client.owo_api.interfaces.bindings;

public interface PythonRunnerManager {

    boolean initPythonScriptPath(String modID, String scriptPath, String eventName, int priority);

    void stopAllPythonScripts();

    void stopPythonScript(String eventName);

    void runPythonScript(String modID, String eventName, Object... args);

}
