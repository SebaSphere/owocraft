package dev.sebastianb.owocraft.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OwocraftDefaultScriptsLoader {

    public static void register() {

        System.out.println("MEOW");
        Path modContainerPath = FabricLoader.getInstance()
                .getModContainer("owocraft")
                .orElseThrow(() -> new RuntimeException("Failed to get mod container"))
                .getPath("assets/owocraft/py");

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
        System.out.println(configPath);

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
