package dev.sebastianb.owocraft.client;

import dev.sebastianb.owocraft.config.OwocraftConfig;
import dev.sebastianb.owocraft.client.owo_api.impl.OwoAPIImpl;
import dev.sebastianb.owocraft.client.owo_api.interfaces.OwoAPI;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.ConnectionStateManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;
import dev.sebastianb.owocraft.config.OwocraftDefaultScriptsLoader;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.lang.reflect.Field;
import java.util.Random;

public class OwocraftClient implements ClientModInitializer {

    private static OwoAPI.API API;
    private static PanamaBindingManager panamaBindingManager;
    private static ConnectionStateManager connectionStateManager;
    private static SensationManager sensationManager;
    private static PythonRunnerManager pythonRunnerManager;

    public static PanamaBindingManager getPanamaBindingManager() {
        return panamaBindingManager;
    }

    public static ConnectionStateManager getConnectionStateManager() {
        return connectionStateManager;
    }

    public static SensationManager getSensationManager() {
        return sensationManager;
    }

    public static PythonRunnerManager getPythonRunnerManager() {
        return pythonRunnerManager;
    }

    @Override
    public void onInitializeClient() {
        // move scripts that should have default configs to config folder
        OwocraftDefaultScriptsLoader.register();

        // init owo api on client start as it only exists there
        OwoAPI._init(OwoAPI.api());
        API = OwoAPIImpl.INSTANCE;

        // load all states
        panamaBindingManager = API.getPanamaBindingManager();
        connectionStateManager = API.getConnectionStateManager();
        sensationManager = API.getSensationManager();
        pythonRunnerManager = API.getPythonRunnerManager();

        panamaBindingManager.loadDLL();
        panamaBindingManager.runEmptyVoidMethod("startOwoSearch");

        pythonRunnerManager.initPythonScriptPath("minecraft", "test-script.py", "test", 1);


        // Use damageTypes interface's class object
        Class<DamageTypes> damageTypesClass = DamageTypes.class;

        // Get all declared fields in the interface
        Field[] fields = damageTypesClass.getDeclaredFields();

        // Iterate through fields
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                ResourceKey<DamageType> resourceKey = (ResourceKey<DamageType>) field.get(null);

                pythonRunnerManager.initPythonScriptPath(
                        resourceKey.location().getNamespace(), // mod id
                        "damage-" + resourceKey.location().getPath() + "-script.py", // python path
                        resourceKey.location().getPath(), // event name
                        (int) resourceKey.location().getPath().toCharArray()[0] // priority based off first letter, just for default config
                );

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        pythonRunnerManager.initPythonScriptPath(
                "minecraft", // mod id
                "environment-raining-script.py", // python path
                "environment-raining", // event name
                new Random().nextInt(50)
        );

        pythonRunnerManager.initPythonScriptPath(
                "minecraft", // mod id
                "environment-underwater-script.py", // python path
                "environment-underwater", // event name
                new Random().nextInt(50)
        );

        pythonRunnerManager.initPythonScriptPath(
                "minecraft", // mod id
                "environment-in_dimension-script.py", // python path
                "environment-in_dimension", // event name
                new Random().nextInt(50)
        );


        pythonRunnerManager.initPythonScriptPath(
                "minecraft", // mod id
                "environment-played_sound-script.py", // python path
                "environment-played_sound", // event name
                new Random().nextInt(50)
        );

        pythonRunnerManager.initPythonScriptPath(
                "minecraft", // mod id
                "event-speed-script.py", // python path
                "event-speed", // event name
                new Random().nextInt(50)
        );

        pythonRunnerManager.initPythonScriptPath(
                "minecraft", // mod id
                "event-in_portal-script.py", // python path
                "event-in_portal", // event name
                new Random().nextInt(50)
        );


        pythonRunnerManager.initPythonScriptPath(
                "minecraft", // mod id
                "event-gained_experience-script.py", // python path
                "event-gained_experience", // event name
                new Random().nextInt(50)
        );

        OwocraftConfig.reload();

    }
}
