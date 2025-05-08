package dev.sebastianb.owocraft.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OwocraftDefaultScriptsLoader {

    public static void register() {




        Path modContainerPath = FabricLoader.getInstance()
                .getModContainer("owocraft")
                .orElseThrow(() -> new RuntimeException("Failed to get mod container"))
                .findPath("assets/owocraft/py").get();


        // TODO: 3CHECK THIS
        Path configFilePath = modContainerPath.resolve("../owocraft_config.json5");

        // Check if the target file exists in the same directory as the .py files
        if (Files.exists(configFilePath)) {
            // Define where you want the config file to be copied
            Path targetPath = FabricLoader.getInstance().getConfigDir()
                    .resolve("owocraft_config.json5"); // TODO: make this dependent on mod id


            // Check if the config file already exists at the target
            if (!Files.exists(targetPath)) {
                // Copy the file
                try {
                    Files.copy(configFilePath, targetPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        // for loop of all .py files inside modContainerPath recursively within folder
        try {
            Files.walk(modContainerPath.toAbsolutePath())
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        if (filePath.toString().endsWith(".py")) {
                            moveDefaultScriptToConfig(filePath);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void moveDefaultScriptToConfig(Path scriptPath) {
        Path configDir = FabricLoader.getInstance().getConfigDir()
                .resolve("owocraft/python/minecraft"); // TODO: make this mod id-dependent



        try {
            // Ensure config path exists
            Files.createDirectories(configDir);

            // Extract just the filename
            String fileName = scriptPath.getFileName().toString();

            // Convert to default filesystem to avoid provider mismatch
            Path scriptFileSystemSafe = Paths.get(scriptPath.toUri());

            // Use default filesystem path for target
            Path targetFile = configDir.resolve(fileName);

            // Only copy if target doesn't exist
            if (!Files.exists(targetFile)) {
                Files.copy(scriptFileSystemSafe, targetFile);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
