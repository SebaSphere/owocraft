package dev.sebastianb.owocraft.client.owo_api.impl.owo;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.interfaces.OwoAPI;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.SensationManager;

public enum SensationManagerImpl implements SensationManager {

    INSTANCE;

    @Override
    public boolean runSensation(String sensationFromParsedString) {
        return OwocraftClient.getPanamaBindingManager()
                .getBooleanStateInvokeOnePassedStringMethod("runParsedSensationEvent", sensationFromParsedString);
    }
}
