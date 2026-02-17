package dev.sebastianb.owocraft.services;

import java.nio.file.Path;

public interface IPlatformHelper {
    String getPlatformName();
    boolean isModLoaded(String modId);
    boolean isDevelopmentEnvironment();
    Path getConfigDir();

    Path getModContainerPath(String mod);
}
