package dev.sebastianb.owocraft.client.owo_api.impl.bindings;

import dev.sebastianb.owocraft.Owocraft;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.Optional;
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
    public void loadDLL() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            var classPath = Owocraft.class.getClassLoader().getResource("libs/OWOCraftLib.dll");
            if (classPath != null) {
                System.load(classPath.getPath());
                Owocraft.getLogger().log(Level.INFO, "Loaded OWOCraftLib.dll successfully");
            } else {
                throw new RuntimeException("Could not find OWOCraftLib.dll, something has gone terribly wrong loading OWOCraft.....\n" +
                        "Contact me@sebastianb.dev with your log - https://sebastianb.dev");
            }
        } else {
            // TODO: make a dev config file for this
            System.load("C:\\Users\\me\\CLionProjects\\OWOCraftLib\\cmake-build-release\\OWOCraftLib.dll");

        }
        nativeLinker = Linker.nativeLinker();
        stdlibLookup = nativeLinker.defaultLookup();
        loaderLookup  = SymbolLookup.loaderLookup();


    }

}
