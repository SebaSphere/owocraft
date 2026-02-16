package dev.sebastianb.owocraft;

import dev.sebastianb.owocraft.networking.OCPackets;
import net.minecraft.resources.ResourceLocation;

import java.util.logging.Logger;

public class CommonOwocraft {

    private static final Logger LOGGER = Logger.getLogger(CommonOwocraft.class.getName());

    public static Logger getLogger() {
        return LOGGER;
    }


    public static ResourceLocation id(String location) {
        return new ResourceLocation("owocraft", location);
    }

    public static void init() {
        OCPackets.register();
    }
}
