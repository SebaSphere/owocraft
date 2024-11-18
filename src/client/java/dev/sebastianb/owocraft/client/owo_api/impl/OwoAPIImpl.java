package dev.sebastianb.owocraft.client.owo_api.impl;

import dev.sebastianb.owocraft.client.owo_api.impl.bindings.PanamaBindingManagerImpl;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.PythonRunnerManagerImpl;
import dev.sebastianb.owocraft.client.owo_api.interfaces.OwoAPI;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;

public enum OwoAPIImpl implements OwoAPI.API {
    INSTANCE;


    @Override
    public PanamaBindingManager getPanamaBindingManager() {
        return PanamaBindingManagerImpl.INSTANCE;
    }

    @Override
    public PythonRunnerManager getPythonRunnerManager() {
        return PythonRunnerManagerImpl.INSTANCE;
    }
}
