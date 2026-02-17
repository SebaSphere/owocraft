package dev.sebastianb.owocraft.client.owo_api.impl.bindings.python;

public record PairedVariableArgument(String variableName, Object object) {
    @Override
    public Object object() {
        return object;
    }

    @Override
    public String variableName() {
        return variableName;
    }
}
