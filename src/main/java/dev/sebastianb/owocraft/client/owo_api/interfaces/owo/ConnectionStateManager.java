package dev.sebastianb.owocraft.client.owo_api.interfaces.owo;

import org.python.antlr.ast.Str;

import java.util.List;

public interface ConnectionStateManager {
     enum States {
         DISCONNECTED, CONNECTING, CONNECTED;
     }

     States getState();
     void setState(States state);

     void setIpAddresses(List<String> ipAddress);
     void addIpAddress(String ipAddress);
     List<String> getIPAddresses();

     void setShouldAutoconnnect(boolean shouldAutoconnect);
     boolean shouldAutoconnect();
}
