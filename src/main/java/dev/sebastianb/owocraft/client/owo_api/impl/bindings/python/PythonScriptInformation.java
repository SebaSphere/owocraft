package dev.sebastianb.owocraft.client.owo_api.impl.bindings.python;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

public class PythonScriptInformation {

    public transient final String script;
    @SerialEntry
    public int priority;
    public transient final String eventName;


    public PythonScriptInformation(String script, String eventName, int priority) {
        this.script = script;
        this.priority = priority;
        this.eventName = eventName;
    }

    @Override
    public int hashCode() {
        return eventName.hashCode();
    }

    @Override
    public String toString() {
        return "PythonScriptInformation{" +
                "script='" + script + '\'' +
                ", priority=" + priority +
                ", eventName='" + eventName + '\'' +
                '}';
    }
}
