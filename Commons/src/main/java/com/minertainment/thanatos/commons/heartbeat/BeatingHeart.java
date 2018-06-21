package com.minertainment.thanatos.commons.heartbeat;

import com.minertainment.thanatos.commons.configuration.SlaveConfiguration;
import com.minertainment.thanatos.commons.heartbeat.packet.HeartbeatPacket;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

public class BeatingHeart implements Runnable {

    // SlotManager (manage reservations)

    private volatile boolean running;

    private ThanatosServer thanatosServer;

    private long lastRun, interval, maxInterval;

    public BeatingHeart(ThanatosServer thanatosServer) {
        this.thanatosServer = thanatosServer;

        lastRun = -1;
        interval = 1000;
        maxInterval = 0;
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public void kill() {
        if(!running) {
            return;
        }
        running = false;
        // send shutdown notification
    }

    @Override
    public void run() {
        while(running) {
            if(System.currentTimeMillis() - lastRun < interval) {
                continue;
            }
            lastRun = System.currentTimeMillis();

            Heartbeat heartbeat = new Heartbeat(SlaveConfiguration.getClusterId(),
                    SlaveConfiguration.getServerId(), thanatosServer.getServerType(),
                    thanatosServer.getOnlinePlayers(), thanatosServer.getTPS(), thanatosServer.getLastDisconnect());
            HeartbeatPacket heartbeatPacket = new HeartbeatPacket(heartbeat);
            heartbeatPacket.send();
        }
    }

}

class HeartbeatResponse {

    public boolean test = false;

}