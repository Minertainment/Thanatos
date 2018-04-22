package com.minertainment.thanatos.slave.config;

import com.minertainment.athena.configuration.GSONConfig;
import org.bukkit.plugin.Plugin;

public class SlaveConfiguration extends GSONConfig {

    private String clusterId;

    private int softPlayerLimit, hardPlayerLimit;
    private double softTPSLimit, hardTPSLimit;

    public SlaveConfiguration(Plugin plugin) {
        super(plugin.getDataFolder(), "slave.json");

        clusterId = "Cluster";
        softPlayerLimit = 150;
        hardPlayerLimit = 250;
        softTPSLimit = 15.0;
        hardTPSLimit = 10.0;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public int getSoftPlayerLimit() {
        return softPlayerLimit;
    }

    public void setSoftPlayerLimit(int softPlayerLimit) {
        this.softPlayerLimit = softPlayerLimit;
    }

    public int getHardPlayerLimit() {
        return hardPlayerLimit;
    }

    public void setHardPlayerLimit(int hardPlayerLimit) {
        this.hardPlayerLimit = hardPlayerLimit;
    }

    public double getSoftTPSLimit() {
        return softTPSLimit;
    }

    public void setSoftTPSLimit(double softTPSLimit) {
        this.softTPSLimit = softTPSLimit;
    }

    public double getHardTPSLimit() {
        return hardTPSLimit;
    }

    public void setHardTPSLimit(double hardTPSLimit) {
        this.hardTPSLimit = hardTPSLimit;
    }

}