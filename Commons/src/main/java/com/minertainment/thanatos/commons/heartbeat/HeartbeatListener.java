package com.minertainment.thanatos.commons.heartbeat;

public interface HeartbeatListener {

    void onHeartbeat(Heartbeat heartbeat);

    void onShutdown();

}