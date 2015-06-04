package net.beaconpe.blockclient.network.packet.raknet;

import org.blockserver.net.protocol.pe.PeProtocolConst;
import org.blockserver.net.protocol.pe.raknet.RakNetPacket;

import java.nio.ByteBuffer;

/**
 * ID_CONNECTED_PING_OPEN_CONNECTIONS (0x01)
 */
public class RakNetPing extends RakNetPacket{
    public long pingID;

    @Override
    protected void _encode(ByteBuffer bb) {
        bb.put(PeProtocolConst.RAKNET_BROADCAST_PING_1);
        bb.putLong(pingID);
        bb.put(PeProtocolConst.MAGIC);
    }

    @Override
    public int getLength() {
        return 25;
    }
}
