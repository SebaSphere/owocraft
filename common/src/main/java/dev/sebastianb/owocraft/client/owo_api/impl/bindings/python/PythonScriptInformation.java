package dev.sebastianb.owocraft.client.owo_api.impl.bindings.python;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class PythonScriptInformation {

    public String script;
    public String scriptName;
    @SerialEntry
    public int priority = 1;
    @SerialEntry
    public String eventName;

    @SerialEntry
    public boolean shouldFinishEventFirst = true;

    @SerialEntry
    public boolean runGenericEventInstead = false;
    @SerialEntry
    public String modID = "minecraft";


    public PythonScriptInformation() {
        this.modID = "minecraft";
        this.script = "";
        this.scriptName = "";
        this.eventName = "";
        this.priority = 1;
    }

    public PythonScriptInformation(String modID, String script, String eventName, int priority) {
        this.modID = modID;
        this.script = script;
        this.scriptName = "";
        this.priority = priority;
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "PythonScriptInformation{" +
                ", priority=" + priority +
                ", eventName='" + eventName + '\'' +
                ", shouldFinishEventFirst=" + shouldFinishEventFirst +
                ", runGenericEventInstead=" + runGenericEventInstead +
                "}\n";
    }

    @Override
    public int hashCode() {
        return eventName.hashCode();
    }
}
