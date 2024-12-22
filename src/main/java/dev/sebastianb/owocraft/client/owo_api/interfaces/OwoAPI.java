package dev.sebastianb.owocraft.client.owo_api.interfaces;

import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.ConnectionStateManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;

public class OwoAPI {

    private static API instance;

    public interface API {
        PanamaBindingManager getPanamaBindingManager();
        PythonRunnerManager getPythonRunnerManager();
        ConnectionStateManager getConnectionStateManager();
        SensationManager getSensationManager();
    }

    public static API api() {
        return instance;
    }

    public static void _init(API instance) {
        if (OwoAPI.instance != null) {
            throw new IllegalStateException("can't init more than once!");
        }
        OwoAPI.instance = instance;
    }


}
