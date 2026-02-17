package dev.sebastianb.owocraft.platform;

import dev.sebastianb.owocraft.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getModContainerPath(String mod) {
        return FabricLoader.getInstance().getModContainer(mod)
                .map(container -> container.getRootPaths().get(0))
                .orElse(null);
    }
}
