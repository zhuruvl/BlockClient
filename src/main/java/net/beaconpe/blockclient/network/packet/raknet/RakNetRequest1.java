package net.beaconpe.blockclient.network.packet.raknet;

import org.blockserver.net.protocol.pe.PeProtocolConst;
import org.blockserver.net.protocol.pe.raknet.ConnectionRequest1Packet;
import org.blockserver.net.protocol.pe.raknet.RakNetPacket;

import java.nio.ByteBuffer;

/**
 * ID_OPEN_CONNECTION_REQUEST_1 (0x05)
 */
public class RakNetRequest1 extends ConnectionRequest1Packet{

    @Override
    protected void _encode(ByteBuffer bb) {
        bb.put(PeProtocolConst.RAKNET_OPEN_CONNECTION_REQUEST_1);
        bb.put(PeProtocolConst.MAGIC);
        bb.put(PeProtocolConst.RAKNET_PROTOCOL_VERSION);
        bb.put(new byte[nullPayloadLength]);
    }

}
