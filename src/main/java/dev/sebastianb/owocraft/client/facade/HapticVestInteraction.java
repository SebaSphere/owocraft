package dev.sebastianb.owocraft.client.facade;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.python.PythonRunnerManagerImpl;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;

public class HapticVestInteraction implements SensationManager {

    public HapticVestInteraction() {

    }

    private static long currentTime = System.currentTimeMillis();
    @Override
    public boolean runSensation(String sensationFromParsedString, String muscleFromParsedString) {
        return OwocraftClient.getSensationManager().runSensation(sensationFromParsedString, muscleFromParsedString);
    }
}
