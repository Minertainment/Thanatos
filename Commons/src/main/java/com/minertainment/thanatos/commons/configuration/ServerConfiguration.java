package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.MongoConfig;
import com.minertainment.thanatos.commons.slave.Slave;

public class ServerConfiguration extends MongoConfig {

    public static Server[] servers;

    public ServerConfiguration() {
        super("ServerMgt");

        servers = new Server[] {
                new Server("142.44.142.135:25555", new String[] {
                        "plots01",
                        "donator01",
                        "donator02",
                        "donator03",
                        "donator04",
                        "donator05",
                        "jail01",
                        "jail02",
                        "jail03",
                        "jail04",
                        "jail05"
                }),
                new Server("66.70.181.135:25555", new String[] {
                        "events01",
                        "spawnam01",
                        "spawnam02",
                        "spawnam03",
                        "spawnam04",
                        "spawnam05",
                        "nz01",
                        "nz02",
                        "nz03",
                        "nz04",
                        "nz05"
                })
        };
    }

    public static Server[] getServers() {
        return servers;
    }

    public static Server getServer(Slave slave) {
        for(Server server : getServers()) {
            for(String slaveId : server.getSlaves()) {
                if(slaveId.equalsIgnoreCase(slave.getServerId())) {
                    return server;
                }
            }
        }
        return null;
    }

    public class Server {

        private String address;
        private String[] slaves;

        public Server(String address, String[] slaves) {
            this.address = address;
            this.slaves = slaves;
        }

        public String getAddress() {
            return address;
        }

        public String[] getSlaves() {
            return slaves;
        }

    }

}