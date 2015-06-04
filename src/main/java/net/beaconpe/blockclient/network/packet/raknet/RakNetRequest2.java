package net.beaconpe.blockclient.network.packet.raknet;

import org.blockserver.net.protocol.pe.PeProtocolConst;
import org.blockserver.net.protocol.pe.raknet.ConnectionRequest2Packet;

import java.nio.ByteBuffer;

/**
 * ID_OPEN_CONNECTION_REQUEST_2 (0x07)
 */
public class RakNetRequest2 extends ConnectionRequest2Packet{

    @Override
    protected void _encode(ByteBuffer bb) {
        bb.put(PeProtocolConst.RAKNET_OPEN_CONNECTION_REQUEST_2);
        bb.put(PeProtocolConst.MAGIC);
        bb.put(new byte[] {0x04, 0x3f, 0x57, (byte) 0xfe, (byte) 0xfd});
        bb.putShort(serverUdpPort);
        bb.putShort(mtuSize);
        bb.putLong(clientID);
    }
}
