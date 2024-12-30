package dev.sebastianb.owocraft.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PythonScriptInformation;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class OwocraftConfig {

    public static ConfigClassHandler<OwocraftConfig> HANDLER = ConfigClassHandler.createBuilder(OwocraftConfig.class)
            .id(ResourceLocation.fromNamespaceAndPath("owocraft", "owocraft_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("owocraft_config.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public static HashMap<String, PythonScriptInformation> pythonConfigInformation
            = OwocraftClient.getPythonRunnerManager().getPythonScripts();


    public static void reload() {
        HANDLER.load();
        OwocraftClient.getPythonRunnerManager()
                .reloadWithScript(OwocraftConfig.pythonConfigInformation);
        pythonConfigInformation = OwocraftClient.getPythonRunnerManager().getPythonScripts();
        HANDLER.save();
    }

}
