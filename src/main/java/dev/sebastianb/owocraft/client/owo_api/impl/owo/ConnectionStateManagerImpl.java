package dev.sebastianb.owocraft.client.owo_api.impl.owo;

import dev.sebastianb.owocraft.client.OwocraftClient;
import dev.sebastianb.owocraft.client.owo_api.interfaces.owo.ConnectionStateManager;

import java.util.ArrayList;
import java.util.List;

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

    List<String> ipAddresses = List.of("127.0.0.1");

    @Override
    public void setIpAddresses(List<String> ipAddress) {
        this.ipAddresses = ipAddress;
    }

    @Override
    public void addIpAddress(String ipAddress) {
        this.ipAddresses.add(ipAddress);
    }

    @Override
    public List<String> getIPAddresses() {
        return ipAddresses;
    }

    boolean shouldAutoconnect = true;

    @Override
    public void setShouldAutoconnnect(boolean shouldAutoconnect) {
        this.shouldAutoconnect = shouldAutoconnect;
    }

    @Override
    public boolean shouldAutoconnect() {
        return shouldAutoconnect;
    }

}
