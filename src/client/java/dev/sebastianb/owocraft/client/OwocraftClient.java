package dev.sebastianb.owocraft.client;

import dev.sebastianb.owocraft.client.owo_api.impl.OwoAPIImpl;
import dev.sebastianb.owocraft.client.owo_api.interfaces.OwoAPI;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.level.Level;

public class OwocraftClient implements ClientModInitializer {

    public static OwoAPI.API API;

    @Override
    public void onInitializeClient() {
        // init owo api on client start as it only exists there
        OwoAPI._init(OwoAPI.api());
        API = OwoAPIImpl.INSTANCE;

        var panama = API.getPanamaBindingManager();
        panama.loadDLL();
        panama.testHelloBinding();
        for (int i = 0; i < 4; i++) {
            System.out.println("PANAMA");

        }



    }
}
