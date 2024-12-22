package dev.sebastianb.owocraft.client;

import dev.sebastianb.owocraft.OwocraftConfig;
import dev.sebastianb.owocraft.client.owo_api.impl.OwoAPIImpl;
import dev.sebastianb.owocraft.client.owo_api.interfaces.OwoAPI;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PanamaBindingManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.ConnectionStateManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
        // init owo api on client start as it only exists there
        OwoAPI._init(OwoAPI.api());
        API = OwoAPIImpl.INSTANCE;

        // load all states
        panamaBindingManager = API.getPanamaBindingManager();
        connectionStateManager = API.getConnectionStateManager();
        sensationManager = API.getSensationManager();
        pythonRunnerManager = API.getPythonRunnerManager();

        panamaBindingManager.loadDLL();
        System.out.println(connectionStateManager.getState());
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
                        (int) resourceKey.location().getPath().toCharArray()[0] // priority based off first letter
                        // TODO: make priority based off config
                );

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        OwocraftConfig.reload();


    }
}
