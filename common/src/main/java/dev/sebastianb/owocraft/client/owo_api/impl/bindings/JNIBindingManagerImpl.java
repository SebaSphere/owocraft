package dev.sebastianb.owocraft.client.owo_api.impl.bindings;

import dev.sebastianb.owocraft.CommonOwocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public enum JNIBindingManagerImpl implements PanamaBindingManager {

    // god this is so bad and cursed
    INSTANCE;

    @Override
    public void runEmptyVoidMethod(String methodName) {
        switch (methodName) {
            case "hello" -> hello();
            case "startOwoSearch" -> startOwoSearch();
            case "startSensationRuns" -> startSensationRuns();
        }
    }

    @Override
    public long getLongStateInvokeVoidMethod(String methodName) {
        return switch (methodName) {
            case "getConnectionState" -> getConnectionState();
            default -> 0L;
        };
    }

    @Override
    public boolean getBooleanStateInvokeMultiplePassedStringMethod(String methodName, String... passedStrings) {
        return switch (methodName) {
            case "runParsedSensationEvent" -> runParsedSensationEvent(passedStrings[0], passedStrings[1]);
            case "connectToVest" -> connectToVest(passedStrings[0]);
            case "shouldAutoconnect" -> {
                shouldAutoconnect(Boolean.parseBoolean(passedStrings[0]));
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public String getStringFromMethod(String methodName) {
        return switch (methodName) {
            case "getScannedIPs" -> getScannedIPs();
            default -> "";
        };
    }

    @Override
    public void loadDLL() {
        try {
            // Load DLL from JAR
            String resourcePath = "/libs/OWOCraftLib.dll";
            InputStream in = CommonOwocraft.class.getResourceAsStream(resourcePath);

            if (in == null) {
                throw new RuntimeException("Could not find OWOCraftLib.dll inside JAR at " + resourcePath + "\n" +
                        "Contact me@sebastianb.dev with your log - https://sebastianb.dev");
            }

            // Extract to temp file
            Path tempDir = Files.createTempDirectory("owocraft_jni");
            Path extractedLib = tempDir.resolve("OWOCraftLib.dll");
            Files.copy(in, extractedLib, StandardCopyOption.REPLACE_EXISTING);
            in.close();

            // Load the library
            System.load(extractedLib.toAbsolutePath().toString());
            extractedLib.toFile().deleteOnExit();

            CommonOwocraft.getLogger().log(Level.INFO, "Loaded OWOCraftLib.dll successfully for JNI from JAR");

        } catch (IOException e) {
            throw new RuntimeException("Failed to extract and load OWOCraftLib.dll for JNI", e);
        }
    }

    private native void hello();

    private native void startOwoSearch();

    private native long getConnectionState();

    private native boolean runParsedSensationEvent(String sensation, String muscles);

    private native String getScannedIPs();

    private native boolean connectToVest(String ipAddresses);

    private native void shouldAutoconnect(boolean shouldAutoconnect);

    private native void startSensationRuns();
}
