package dev.sebastianb.owocraft.client;

import dev.sebastianb.owocraft.client.owo_api.impl.OwoAPIImpl;
import dev.sebastianb.owocraft.client.owo_api.interfaces.OwoAPI;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.ConnectionStateManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;
import net.fabricmc.api.ClientModInitializer;

public class OwocraftClient implements ClientModInitializer {

    private static OwoAPI.API API;
    private static PanamaBindingManager panamaBindingManager;
    private static ConnectionStateManager connectionStateManager;
    private static SensationManager sensationManager;

    public static PanamaBindingManager getPanamaBindingManager() {
        return panamaBindingManager;
    }

    public static ConnectionStateManager getConnectionStateManager() {
        return connectionStateManager;
    }

    public static SensationManager getSensationManager() {
        return sensationManager;
    }

    @Override
    public void onInitializeClient() {
        // init owo api on client start as it only exists there
        OwoAPI._init(OwoAPI.api());
        API = OwoAPIImpl.INSTANCE;

        // load all states
        panamaBindingManager = API.getPanamaBindingManager();
        connectionStateManager = API.getConnectionStateManager();
        sensationManager = API.getSensationManager();

        panamaBindingManager.loadDLL();
        System.out.println(connectionStateManager.getState());
        panamaBindingManager.runEmptyVoidMethod("startOwoSearch");


    }
}
