package dev.sebastianb.owocraft.client.facade;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.impl.bindings.PythonRunnerManagerImpl;
import dev.sebastianb.owocraft.client.owo_api.interfaces.bindings.PythonRunnerManager;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;

public class HapticVestInteraction implements SensationManager {

    public HapticVestInteraction() {

    }

    @Override
    public boolean runSensation(String sensationFromParsedString) {
        return OwocraftClient.getSensationManager().runSensation(sensationFromParsedString);
    }
}
