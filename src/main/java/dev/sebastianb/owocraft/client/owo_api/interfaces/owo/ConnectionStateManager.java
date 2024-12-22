package dev.sebastianb.owocraft.client.owo_api.interfaces.owo;

public interface ConnectionStateManager {
     enum States {
         DISCONNECTED, CONNECTING, CONNECTED;
     }

     States getState();
     void setState(States state);
}
