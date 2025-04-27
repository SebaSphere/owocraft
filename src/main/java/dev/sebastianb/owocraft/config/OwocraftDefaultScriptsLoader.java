package dev.sebastianb.owocraft.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OwocraftDefaultScriptsLoader {

    public static void register() {




        Path modContainerPath = FabricLoader.getInstance()
                .getModContainer("owocraft")
                .orElseThrow(() -> new RuntimeException("Failed to get mod container"))
                .getPath("assets/owocraft/py");


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
            Files.walk(modContainerPath)
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
        Path configPath = FabricLoader.getInstance().getConfigDir()
                .resolve("owocraft/python/minecraft"); // TODO: make this dependent on mod id

        // check if the filename of scriptPath is inside configPath
        if (!Files.exists(configPath.resolve(scriptPath.getFileName()))) {
            // create directories if they don't exist
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // copy if it doesn't exist
            try {
                Files.copy(scriptPath, configPath.resolve(scriptPath.getFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
