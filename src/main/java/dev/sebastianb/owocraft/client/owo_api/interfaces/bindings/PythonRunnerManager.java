package dev.sebastianb.owocraft.client.owo_api.interfaces.bindings;

import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PairedVariableArgument;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PythonScriptInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface PythonRunnerManager {

    HashMap<String, PythonScriptInformation> getPythonScripts();

    void reloadWithScript(HashMap<String, PythonScriptInformation> pythonScriptInformationHashMap);

    boolean initPythonScriptPath(String modID, String scriptPath, String eventName, int priority);

    void stopAllPythonScripts();

    void stopPythonScript(String eventName);

    boolean runPythonScript(String modID, String eventName, PairedVariableArgument... args);

}
