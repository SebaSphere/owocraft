package dev.sebastianb.owocraft;

import net.fabricmc.api.ModInitializer;

public class FabricOwocraft implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonOwocraft.init();
    }
}
