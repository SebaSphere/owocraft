package dev.sebastianb.owocraft.client.owo_api.impl.bindings;

import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.util.Optional;

public enum PanamaBindingManagerImpl implements PanamaBindingManager {

    INSTANCE;

    Linker nativeLinker;

    SymbolLookup stdlibLookup;


    SymbolLookup loaderLookup ;

    @Override
    public void testHelloBinding() {
        new Thread(() -> {
            try {
                Optional<MemorySegment> memSeg = loaderLookup.find("startOwoSearch");
                if (memSeg.isPresent()) {
                    MethodHandle methodHandle = memSeg.or(() -> stdlibLookup.find("startOwoSearch"))
                            .map(symbolSeg -> nativeLinker.downcallHandle(symbolSeg, FunctionDescriptor.ofVoid()))
                            .orElse(null);
                    methodHandle.invoke();
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void loadDLL() {
        // TODO: I'll likely need to download it from a repo
        // TODO: make this not static, perhaps download automagically
        System.load("C:\\Users\\me\\CLionProjects\\OWOCraftLib\\cmake-build-release\\OWOCraftLib.dll");
        nativeLinker = Linker.nativeLinker();
        stdlibLookup = nativeLinker.defaultLookup();
        loaderLookup  = SymbolLookup.loaderLookup();


    }

}
