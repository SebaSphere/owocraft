package dev.sebastianb.owocraft.client.owo_api.impl.bindings.python;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class PythonScriptInformation {

    public transient final String script;
    @SerialEntry
    public int priority;
    public transient final String eventName;

    @SerialEntry
    public boolean shouldFinishEventFirst = true;

    @SerialEntry
    public boolean runGenericEventInstead = false;


    public PythonScriptInformation(String script, String eventName, int priority) {
        this.script = script;
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
