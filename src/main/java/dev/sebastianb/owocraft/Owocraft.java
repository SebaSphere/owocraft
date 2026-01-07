package dev.sebastianb.owocraft;

import dev.sebastianb.owocraft.networking.OCPackets;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

import java.util.logging.Logger;

public class Owocraft implements ModInitializer {

    private static final Logger LOGGER = Logger.getLogger(Owocraft.class.getName());

    public static Logger getLogger() {
        return LOGGER;
    }


    public static Identifier id(String location) {
        return Identifier.fromNamespaceAndPath("owocraft", location);
    }

    @Override
    public void onInitialize() {

        OCPackets.register();

    }
}
