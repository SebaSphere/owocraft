package dev.sebastianb.owocraft.client.owo_api.impl.owo;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.ConnectionStateManager;

public enum ConnectionStateManagerImpl implements ConnectionStateManager {
    INSTANCE;

    @Override
    public States getState() {
        long enumIndex = OwocraftClient.getPanamaBindingManager()
                .getLongStateInvokeVoidMethod("getConnectionState");
        return States.values()[(int) enumIndex];
    }

    @Override
    public void setState(States state) {

    }

}
