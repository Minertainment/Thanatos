package com.minertainment.thanatos.commons.network;

public interface NetworkChangeListener {

    // Server connects to network
    void onServerConnect();

    // Server shuts down safely.
    void onServerShutdown();

    // Server becomes unresponsive (crash, unreachable, etc).
    void onServerUnresponsive();

}