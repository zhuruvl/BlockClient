package net.beaconpe.blockclient.network.packet.raknet;

import org.blockserver.net.protocol.pe.raknet.RakNetPacket;

import java.nio.ByteBuffer;

/**
 * ID_UNCONNECTED_PING_OPEN_CONNECTIONS (0x1C)
 */
public class RakNetPong extends RakNetPacket{
    public long pingID;
    public long serverID;
    public String serverName;
    public int playerCount;
    public int maxPlayers;
    public int protocol;
    public String version;

    @Override
    protected void _decode(ByteBuffer bb) {
        bb.get();
        pingID = bb.getLong();
        serverID = bb.getLong();
        bb.get(new byte[16]);

        int len = bb.getShort();
        byte[] identifierBytes = new byte[len];
        bb.get(identifierBytes);

        String identifier = new String(identifierBytes);
        if(identifier.startsWith("MCPE;")) {
            String[] array = identifier.split(";");
            serverName = array[1];
            protocol = Integer.parseInt(array[2]);
            version = array[3];
            playerCount = Integer.parseInt(array[4]);
            maxPlayers = Integer.parseInt(array[5]);
        } else {
            serverName = identifier.replaceAll("MCCPP;Demo;", "").replaceAll("MCCPP;MINECON;", "");
        }
    }

    @Override
    public int getLength() {
        return -1;
    }
}
