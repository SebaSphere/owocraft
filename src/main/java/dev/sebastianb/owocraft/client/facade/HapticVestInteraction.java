package dev.sebastianb.owocraft.client.facade;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;

public class HapticVestInteraction implements SensationManager {

    public HapticVestInteraction() {

    }

    @Override
    public boolean runSensation(String sensationFromParsedString, String muscleFromParsedString) {
        System.out.println(muscleFromParsedString);
        return OwocraftClient.getSensationManager().runSensation(sensationFromParsedString, muscleFromParsedString);
    }
}
