package net.beaconpe.blockclient;

import net.beaconpe.blockclient.config.BlockConfig;
import net.beaconpe.blockclient.utility.Utils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Main Class for BlockClient.
 */
public class BlockClient {
    public static final String VERSION_STRING = "v1.0-SNAPSHOT implementing MCPE 0.11.0 (protocol 27)";
    public static final int PROTOCOL = 27;

    public static void main(String[] args){
        if(args == null || args.length == 0){
            //Start in normal mode
            File configFile = new File("client.properties");
            try {
                if(!configFile.exists()){
                    Utils.copyResource(configFile.getName(), configFile.getName());
                }
                BlockConfig config = BlockConfig.loadFromExisting(configFile);

                MinecraftPEClient client = new MinecraftPEClient(new InetSocketAddress(config.getServerIP(), config.getServerPort()), config.getClientName(), LogManager.getLogger("BlockClient"));
                client.startInCurrentThread();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
