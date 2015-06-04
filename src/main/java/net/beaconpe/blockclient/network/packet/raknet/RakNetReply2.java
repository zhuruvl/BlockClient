package net.beaconpe.blockclient.network.packet.raknet;

import org.blockserver.net.protocol.pe.raknet.ConnectionReply2Packet;

import java.nio.ByteBuffer;

/**
 * ID_OPEN_CONNECTION_REPLY_2 (0x08)
 */
public class RakNetReply2 extends ConnectionReply2Packet{

    @Override
    protected void _decode(ByteBuffer bb) {
        bb.get();
        bb.get(new byte[16]);
        serverID = bb.getLong();
        clientUdpPort = bb.getShort();
        mtuSize = bb.getShort();
        security = bb.get();
    }
}
