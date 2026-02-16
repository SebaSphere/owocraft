package dev.sebastianb.owocraft.client.owo_api.impl.bindings;

import dev.sebastianb.owocraft.CommonOwocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

public enum PanamaBindingManagerImpl implements PanamaBindingManager {

    INSTANCE;

    Linker nativeLinker;

    SymbolLookup stdlibLookup;


    SymbolLookup loaderLookup ;

    @Override
    public void runEmptyVoidMethod(String methodName) {
        new Thread(() -> {
            try {
                Optional<MemorySegment> memSeg = loaderLookup.find(methodName);
                if (memSeg.isPresent()) {
                    MethodHandle methodHandle = memSeg.or(() -> stdlibLookup.find(methodName))
                            .map(symbolSeg -> nativeLinker.downcallHandle(symbolSeg, FunctionDescriptor.ofVoid()))
                            .orElse(null);
                    methodHandle.invoke();
                }
            } catch (Throwable e) {
                throw new RuntimeException("Method " + methodName + " not found");
            }
        }).start();
    }

    @Override
    public long getLongStateInvokeVoidMethod(String methodName) {
        try {
            Optional<MemorySegment> memSeg = loaderLookup.find(methodName);
            if (memSeg.isPresent()) {
                MethodHandle methodHandle = memSeg.or(() -> stdlibLookup.find(methodName))
                        .map(symbolSeg -> nativeLinker
                                .downcallHandle(symbolSeg, FunctionDescriptor.of(ValueLayout.JAVA_LONG)))
                        .orElseThrow();
                return (long) methodHandle.invokeExact();
            } else {
                throw new RuntimeException("Method " + methodName + " not found");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBooleanStateInvokeMultiplePassedStringMethod(String methodName, String... passedStrings) {
        try {
            Optional<MemorySegment> memSeg = loaderLookup.find(methodName);
            if (memSeg.isPresent()) {
                MethodHandle methodHandle = memSeg.or(() -> stdlibLookup.find(methodName))
                        .map(symbolSeg -> nativeLinker
                                .downcallHandle(symbolSeg,
                                        FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN,
                                                Collections.nCopies(passedStrings.length, ValueLayout.ADDRESS)
                                                        .toArray(ValueLayout[]::new)
                                        )
                                )
                        )
                        .orElseThrow();
                // Pass pointers to the C strings
                ArrayList<MemorySegment> segments = new ArrayList<>();

                for (int i = 0; i < passedStrings.length; i++) {
                    segments.add(getMemorySegmentFromString(passedStrings, i));
                }

                return (boolean) methodHandle
                        .invokeWithArguments(segments.toArray());
            } else {
                throw new RuntimeException("Method " + methodName + " not found");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getStringFromMethod(String methodName) {
        try {
            Optional<MemorySegment> memSeg = loaderLookup.find(methodName);
            if (memSeg.isPresent()) {
                MethodHandle methodHandle = memSeg.or(() -> stdlibLookup.find(methodName))
                        .map(symbolSeg -> nativeLinker
                                .downcallHandle(symbolSeg, FunctionDescriptor.of(ValueLayout.ADDRESS)))
                        .orElseThrow();
                MemorySegment result;
                try (Arena local = Arena.ofConfined()) {
                    result = (MemorySegment) methodHandle.invokeExact();
                }
                return result.address() == 0 ? null : result.reinterpret(Integer.MAX_VALUE).getString(0);
            } else {
                throw new RuntimeException("Method " + methodName + " not found");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static MemorySegment getMemorySegmentFromString(String[] passedStrings, int x) {
        byte[] stringBytes1 = passedStrings[x].getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(stringBytes1.length + 1);
        byteBuffer1.put(stringBytes1);

        // Null terminate the first string (C-style)
        byteBuffer1.put((byte) 0);
        byteBuffer1.flip();

        MemorySegment segment1 = MemorySegment.ofBuffer(byteBuffer1);
        return segment1;
    }

    @Override
    public void loadDLL() {
        // FIXME: we need to have a better way of loading a developer DLL
        try {
            // Load DLL from JAR
            String resourcePath = "/libs/OWOCraftLib.dll";
            InputStream in = CommonOwocraft.class.getResourceAsStream(resourcePath);

            if (in == null) {
                throw new RuntimeException("Could not find OWOCraftLib.dll inside JAR at " + resourcePath + "\n" +
                        "Contact me@sebastianb.dev with your log - https://sebastianb.dev");
            }

            // Extract to temp file
            Path tempDir = Files.createTempDirectory("owocraft");
            Path extractedLib = tempDir.resolve("OWOCraftLib.dll");
            Files.copy(in, extractedLib, StandardCopyOption.REPLACE_EXISTING);
            in.close();

            // Load the library
            System.load(extractedLib.toAbsolutePath().toString());
            extractedLib.toFile().deleteOnExit();

            CommonOwocraft.getLogger().log(Level.INFO, "Loaded OWOCraftLib.dll successfully from JAR");

        } catch (IOException e) {
            throw new RuntimeException("Failed to extract and load OWOCraftLib.dll", e);
        }

        nativeLinker = Linker.nativeLinker();
        stdlibLookup = nativeLinker.defaultLookup();
        loaderLookup = SymbolLookup.loaderLookup();
    }


}
