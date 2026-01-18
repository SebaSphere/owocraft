package dev.sebastianb.owocraft.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PythonScriptInformation;
import dev.sebastianb.owocraft.services.Services;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;

public class OwocraftConfig {

    public static ConfigClassHandler<OwocraftConfig> HANDLER = ConfigClassHandler.createBuilder(OwocraftConfig.class)
            .id(ResourceLocation.fromNamespaceAndPath("owocraft", "owocraft_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Services.PLATFORM.getConfigDir().resolve("owocraft_config.json5"))
                    .appendGsonBuilder(builder -> builder.setPrettyPrinting().serializeNulls()) // serialize nulls
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public static HashMap<String, PythonScriptInformation> pythonConfigInformation
            = OwocraftClient.getPythonRunnerManager().getPythonScripts();

    @SerialEntry
    public static List<String> ipAddresses = OwocraftClient.getConnectionStateManager().getIPAddresses();

    @SerialEntry
    public static boolean shouldAutoconnect = OwocraftClient.getConnectionStateManager().shouldAutoconnect();

    public static void reload() {


        HANDLER.load();
        OwocraftClient.getPythonRunnerManager()
                .reloadWithScript(OwocraftConfig.pythonConfigInformation);
        pythonConfigInformation = OwocraftClient.getPythonRunnerManager().getPythonScripts();


        HANDLER.save();

    }

}
