package dev.sebastianb.owocraft.platform;

import dev.sebastianb.owocraft.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }


    // this should be the config dir of the mod
    @Override
    public Path getConfigDir() {
        // FMLPaths.CONFIG_DIR points to the /config folder in the game directory
        return FMLPaths.CONFIGDIR.get();
    }

    // this should be the path to the actual mod jar
    @Override
    public Path getModContainerPath(String modId) {
        // ModList.get() retrieves the container, and findResource() provides the Path to the resource.
        return ModList.get()
                .getModContainerById(modId)
                .map(container -> container.getModInfo().getOwningFile().getFile().findResource("."))
                .orElse(null);
    }
}