package dev.sebastianb.owocraft.client.owo_api.impl.bindings;

import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;
import jdk.jfr.MemoryAddress;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

// https://github.com/apache/sis/blob/ac4ad3f36a17457e51dce01a35a5a2ad5645ac39/optional/src/org.apache.sis.storage.gdal/main/org/apache/sis/storage/panama/NativeFunctions.java#L134
// not a bad class to look at
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
                                                Arrays.stream(passedStrings)
                                                .map(s -> ValueLayout.ADDRESS)
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
                        .invokeWithArguments(segments.toArray(MemorySegment[]::new));
            } else {
                throw new RuntimeException("Method " + methodName + " not found");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isNull(final MemorySegment result) {
        return (result == null) || result.address() == 0;
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
                return isNull(result) ? null : result.reinterpret(Integer.MAX_VALUE).getString(0);
            } else {
                throw new RuntimeException("Method " + methodName + " not found");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull MemorySegment getMemorySegmentFromString(String[] passedStrings, int x) {
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
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            try {
                // Load DLL from JAR
                String resourcePath = "/libs/OWOCraftLib.dll";
                InputStream in = Owocraft.class.getResourceAsStream(resourcePath);

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

                Owocraft.getLogger().log(Level.INFO, "Loaded OWOCraftLib.dll successfully from JAR");

            } catch (IOException e) {
                throw new RuntimeException("Failed to extract and load OWOCraftLib.dll", e);
            }
        } else {
            // Load directly from local dev path
            System.load("C:\\Users\\me\\CLionProjects\\OWOCraftLib\\cmake-build-release\\OWOCraftLib.dll");
        }

        nativeLinker = Linker.nativeLinker();
        stdlibLookup = nativeLinker.defaultLookup();
        loaderLookup = SymbolLookup.loaderLookup();
    }


}
