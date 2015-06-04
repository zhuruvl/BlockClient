package net.beaconpe.blockclient.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * BlockClient's configuration
 */
public class BlockConfig {
    private String serverIP;
    private int serverPort;
    private String clientName;

    public static BlockConfig loadFromExisting(File file) throws IOException {
        BlockConfig config = new BlockConfig();

        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        config.serverIP = properties.getProperty("serverIP", "127.0.0.1");
        config.serverPort = Integer.parseInt(properties.getProperty("serverPort", "19132"));
        config.clientName = properties.getProperty("clientName", "Steve");

        return config;
    }

    public String getClientName() {
        return clientName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getServerIP() {
        return serverIP;
    }
}
