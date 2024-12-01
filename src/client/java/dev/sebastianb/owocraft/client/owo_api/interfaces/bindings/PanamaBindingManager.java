package dev.sebastianb.owocraft.client.owo_api.interfaces.bindings;

public interface PanamaBindingManager {

    void runEmptyVoidMethod(String methodName);

    long getLongStateInvokeVoidMethod(String methodName);

    boolean getBooleanStateInvokeOnePassedStringMethod(String methodName, String passedString);

    void loadDLL();

}
