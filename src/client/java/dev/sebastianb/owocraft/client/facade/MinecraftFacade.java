package dev.sebastianb.owocraft.client.facade;

import net.minecraft.client.Minecraft;

public class MinecraftFacade {

    public MinecraftFacade() {}

    public long getSystemTime() {
        return System.currentTimeMillis();
    }

}
