package net.beaconpe.blockclient;

import net.beaconpe.blockclient.network.Network;
import net.beaconpe.blockclient.utility.BlockThread;
import net.beaconpe.blockclient.utility.ClientTicker;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * An implementation of a Minecraft: Pocket Edition client.
 */
public class MinecraftPEClient extends BlockThread{
    private InetSocketAddress serverAddress;
    private String name;

    private ClientTicker ticker;
    private Network network;

    private DatagramSocket socket;
    private Logger logger;

    MinecraftPEClient(InetSocketAddress serverAddress, String name, Logger logger){
        this.serverAddress = serverAddress;
        this.name = name;
        this.logger = logger;

        addStartupTask(() -> {
            ticker = new ClientTicker();
            ticker.startup();
            network = new Network(this);
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        });

        addShutdownTask(() -> socket.close());
        addShutdownTask(() -> {
            try {
                ticker.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void run() {
        logger.info("Starting Minecraft: PE Client version: "+BlockClient.VERSION_STRING);
        try {
            if(network.testPing()){
                logger.info("Attempting to connect to: "+serverAddress.toString());
                if(network.connect()){
                    logger.info("Connected to: "+serverAddress.toString());
                    network.startInCurrentThread();
                } else {
                    logger.fatal(serverAddress.toString()+" did not respond, exiting...");
                    System.exit(1);
                }
            } else {
                logger.fatal("Could not ping " + serverAddress.toString() + ", exiting...");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendPacket(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress);
        socket.send(packet);
    }

    public byte[] recievePacket() throws SocketTimeoutException {
        DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
        try {
            socket.setSoTimeout(5000);
            socket.receive(packet);
            System.out.println("Recieved packet.");
        } catch(SocketTimeoutException e){
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.copyOf(packet.getData(), packet.getLength());
    }

    public byte[] recievePacket(int timeout) throws SocketTimeoutException {
        DatagramPacket packet = new DatagramPacket(new byte[2048], 2048);
        try {
            socket.setSoTimeout(timeout);
            socket.receive(packet);
        } catch(SocketTimeoutException e){
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.copyOf(packet.getData(), packet.getLength());
    }

    public ClientTicker getTicker() {
        return ticker;
    }

    public DatagramSocket getSocket(){
        return socket;
    }

    public Logger getLogger() {
        return logger;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }
}
