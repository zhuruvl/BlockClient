package net.beaconpe.blockclient.network;

import net.beaconpe.blockclient.MinecraftPEClient;
import net.beaconpe.blockclient.network.packet.login.ClientConnect;
import net.beaconpe.blockclient.network.packet.raknet.*;
import net.beaconpe.blockclient.utility.BlockThread;
import static org.blockserver.net.protocol.pe.PeProtocolConst.*;

import org.blockserver.net.protocol.pe.PacketAssembler;
import org.blockserver.net.protocol.pe.raknet.*;
import org.blockserver.net.protocol.pe.sub.PeDataPacket;
import org.blockserver.net.protocol.pe.sub.v27.BatchPacket;
import org.blockserver.ticker.CallableTask;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Networking class to handle packets.
 */
public class Network extends BlockThread{
    private int MTU;
    private int lastSeqNum = 0;
    private int currentSeqNum = 0;
    private int currentMessageIndex = 0;

    private List<Integer> ACKQueue = new ArrayList<>();
    private List<Integer> NACKQueue = new ArrayList<>();
    private CustomPacket currentQueue = new CustomPacket();
    private Map<Integer, CustomPacket> recoveryQueue = new HashMap<>();
    private CallableTask updateQueueTask;

    private boolean connected = false;

    private MinecraftPEClient client;

    public Network(MinecraftPEClient client){
        this.client = client;
        addStartupTask(() -> {
            try {
                updateQueueTask = new CallableTask(this, "updateQueues");
                client.getTicker().registerRepeatingTask(updateQueueTask, 10);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendPacket(RakNetPacket packet){
        try {
            client.sendPacket(packet.encode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToQueue(PeDataPacket dp){
        byte[] buffer = dp.encode();
        if(buffer.length + currentQueue.getLength() + 34 >= MTU){
            synchronized (currentQueue){
                if(!currentQueue.packets.isEmpty()){
                    currentQueue.sequenceNumber = currentSeqNum++;
                    sendPacket(currentQueue);
                    recoveryQueue.put(currentQueue.sequenceNumber, currentQueue);
                    currentQueue.packets.clear();
                }
            }
        }
        if(buffer.length + currentQueue.getLength() + 34 >= MTU){
            //Need to be compressed
            BatchPacket bp = BatchPacket.fromBuffer(buffer);
            buffer = bp.encode();
        }
        CustomPacket.InternalPacket ip = new CustomPacket.InternalPacket();
        if(dp.getChannel() == NetworkChannel.CHANNEL_NONE){
            ip.reliability = 2;
        } else {
            ip.reliability = 3;
            ip.orderChannel = dp.getChannel().getAsByte();
            ip.orderIndex = 0;
        }
        ip.messageIndex = currentMessageIndex++;
        ip.buffer = buffer;
        synchronized (currentQueue) {
            if (PacketAssembler.checkIfSplitNeeded(currentQueue, ip, MTU)) {
                //TODO
                currentQueue.packets.add(ip);
            } else {
                currentQueue.packets.add(ip);
            }
        }
    }

    public void updateQueues(){
        synchronized (ACKQueue){
            if(!ACKQueue.isEmpty()){
                int[] nums = new int[ACKQueue.size()];
                for(int i = 0; i < nums.length; i++){
                    nums[i] = ACKQueue.get(i);
                }
                ACKPacket ack = new ACKPacket();
                ack.sequenceNumbers = nums;
                sendPacket(ack);
            }
        }
        synchronized (NACKQueue){
            if(!NACKQueue.isEmpty()){
                int[] nums = new int[NACKQueue.size()];
                for(int i = 0; i < nums.length; i++){
                    nums[i] = NACKQueue.get(i);
                }
                NACKPacket nack = new NACKPacket();
                nack.sequenceNumbers = nums;
                sendPacket(nack);
            }
        }
        synchronized (currentQueue){
            if(!currentQueue.packets.isEmpty()){
                currentQueue.sequenceNumber = currentSeqNum++;
                sendPacket(currentQueue);
                recoveryQueue.put(currentQueue.sequenceNumber, currentQueue);
                currentQueue.packets.clear();
            }
        }
    }

    public boolean connect() throws IOException {
        if(connected){
            throw new RuntimeException("Already connected.");
        }
        for(int i = 0; i < 13; i++){
            if(i <= 4){
                byte[] reply = connect(1447);
                if(reply != null){
                    handlePacket(reply);
                    MTU = 1447;
                    connected = true;
                    return true;
                }
            } else if(i <= 8){
                byte[] reply = connect(1155);
                if(reply != null){
                    handlePacket(reply);
                    MTU = 1155;
                    connected = true;
                    return true;
                }
            } else {
                byte[] reply = connect(531);
                if(reply != null){
                    handlePacket(reply);
                    MTU = 531;
                    connected = true;
                    return true;
                }
            }
        }
        return false;
    }

    private byte[] connect(int mtuSize) throws IOException {
        RakNetRequest1 request1 = new RakNetRequest1();
        request1.nullPayloadLength = 1447;
        client.sendPacket(request1.encode());

        try {
            byte[] reply = client.recievePacket(500);
            if(reply[0] == RAKNET_OPEN_CONNECTION_REPLY_1){
                return reply;
            } else {
                return null;
            }
        } catch(SocketTimeoutException e){
            return null;
        }
    }

    public void run(){
        while(isRunning()) {
            try {
                byte[] buffer = client.recievePacket();
                handlePacket(buffer);
            } catch (SocketTimeoutException e) {

            }
        }
    }

    private void handlePacket(byte[] buffer) {
        byte pid = buffer[0];
        System.out.println("Handling packet: "+pid);
        switch (pid){
            case RAKNET_OPEN_CONNECTION_REPLY_1:
                client.getLogger().debug("Got reply 1");
                RakNetReply1 reply1 = new RakNetReply1();
                reply1.decode(buffer);

                RakNetRequest2 request2 = new RakNetRequest2();
                request2.serverUdpPort = (short) client.getServerAddress().getPort();
                request2.mtuSize = reply1.mtuSize;
                request2.clientID = new Random().nextLong();
                sendPacket(request2);
                break;

            case RAKNET_OPEN_CONNECTION_REPLY_2:
                client.getLogger().debug("Got reply 2.");
                RakNetReply2 reply2 = new RakNetReply2();
                reply2.decode(buffer);

                ClientConnect cc = new ClientConnect();
                cc.session = new Random().nextLong();
                cc.clientID = new Random().nextLong();
                addToQueue(cc);
                break;

            default:
                if(pid >= RAKNET_CUSTOM_PACKET_MIN && pid <= RAKNET_CUSTOM_PACKET_MAX){
                    CustomPacket cp = new CustomPacket();
                    cp.decode(buffer);
                    handleCustomPacket(cp);
                } else {
                    client.getLogger().warn("Unknown packet: " + Arrays.toString(buffer));
                }
                break;
        }
    }

    private void handleCustomPacket(CustomPacket cp) {
        if(cp.sequenceNumber - lastSeqNum == 1){
            lastSeqNum = cp.sequenceNumber;
        } else {
            for(int i = lastSeqNum; i < cp.sequenceNumber; i++){
                synchronized (NACKQueue){
                    NACKQueue.add(i);
                }
            }
        }

        synchronized (ACKQueue){
            ACKQueue.add(cp.sequenceNumber);
        }

        if(PacketAssembler.checkForSplitPackets(cp)){
            List<CustomPacket.InternalPacket> splitPackets = PacketAssembler.getSplitPackets(cp);
            cp.packets.removeAll(splitPackets);
            List<PacketAssembler.AssembledPacket> assembledPackets = PacketAssembler.assemblePackets(splitPackets);

            for(CustomPacket.InternalPacket ip : cp.packets){
                handleDataPacket(ip.buffer);
            }
            for(PacketAssembler.AssembledPacket assembledPacket : assembledPackets){
                handleDataPacket(assembledPacket.getBuffer());
            }
        } else {
            for(CustomPacket.InternalPacket ip : cp.packets){
                handleDataPacket(ip.buffer);
            }
        }
    }

    private void handleDataPacket(byte[] buffer) {
        byte pid = buffer[0];
        switch (pid){
            case MC_SERVER_HANDSHAKE:
                //TODO
                break;
        }
    }

    public boolean testPing() throws IOException{
        RakNetPing ping = new RakNetPing();
        ping.pingID = 0;
        long startTime = System.currentTimeMillis();
        client.getLogger().info("Pinging "+client.getServerAddress());
        client.sendPacket(ping.encode());

        try {
            byte[] buffer = client.recievePacket();
            RakNetPong pong = new RakNetPong();
            pong.decode(buffer);
            long endTime = System.currentTimeMillis() - startTime;
            client.getLogger().info("Got a reply from "+client.getServerAddress().toString()+" ("+endTime+" ms)");
        } catch(SocketTimeoutException e){
            client.getLogger().error("No reply from " + client.getServerAddress().toString());
            return false;
        }
        return true;
    }
}
