package com.minertainment.thanatos.commons.profile;

import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.profile.Profile;
import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.plugin.ThanatosServerType;

import java.util.UUID;

public class ThanatosProfile extends Profile {

    private String lastCluster;
    private String lastSlave;
    private LazyLocation lastLocation;

    public ThanatosProfile(UUID uuid) {
        super(uuid);

        lastCluster = null;
        lastSlave = null;
        lastLocation = null;
    }

    public String getLastCluster() {
        return lastCluster;
    }

    public void setLastCluster(String lastCluster) {
        this.lastCluster = lastCluster;
    }

    public String getLastSlave() {
        return lastSlave;
    }

    public void setLastSlave(String lastSlave) {
        this.lastSlave = lastSlave;
    }

    public LazyLocation getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(LazyLocation location) {
        this.lastLocation = location;
    }

    @Override
    public void onLeave() {
        Thanatos.getServer().onProfileLeave(this);
    }

}