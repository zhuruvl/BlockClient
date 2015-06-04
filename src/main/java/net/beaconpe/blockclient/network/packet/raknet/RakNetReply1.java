package net.beaconpe.blockclient.network.packet.raknet;

import org.blockserver.net.protocol.pe.raknet.ConnectionReply1Packet;

import java.nio.ByteBuffer;

/**
 * ID_OPEN_CONNECTION_REPLY_1 (0x06)
 */
public class RakNetReply1 extends ConnectionReply1Packet{

    @Override
    protected void _decode(ByteBuffer bb) {
        bb.get();
        bb.get(new byte[16]);
        serverID = bb.getLong();
        security = bb.get();
        mtuSize = bb.getShort();
    }
}
