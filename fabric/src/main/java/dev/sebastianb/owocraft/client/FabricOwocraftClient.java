package dev.sebastianb.owocraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class FabricOwocraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OwocraftClient.init();
    }
}
