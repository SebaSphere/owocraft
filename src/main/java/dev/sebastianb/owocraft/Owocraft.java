package dev.sebastianb.owocraft;

import dev.sebastianb.owocraft.networking.OCPackets;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

import java.util.logging.Logger;

public class Owocraft implements ModInitializer {

    private static final Logger LOGGER = Logger.getLogger(Owocraft.class.getName());

    public static Logger getLogger() {
        return LOGGER;
    }


    public static ResourceLocation id(String location) {
        return ResourceLocation.fromNamespaceAndPath("owocraft", location);
    }

    @Override
    public void onInitialize() {

        OCPackets.register();


    }
}
