package net.beaconpe.blockclient.network.packet.login;

import org.blockserver.io.BinaryWriter;
import org.blockserver.net.protocol.pe.sub.PeDataPacket;

import java.io.IOException;

/**
 * CLIENT_CONNECT (0x09)
 */
public class ClientConnect extends PeDataPacket{
    public long clientID;
    public long session;
    public byte unknown = 0;

    @Override
    protected void _encode(BinaryWriter writer) throws IOException {
        writer.writeByte(MC_CLIENT_CONNECT);
        writer.writeLong(clientID);
        writer.writeLong(session);
        writer.writeByte(unknown);
    }

    @Override
    protected int getLength() {
        return 18;
    }
}
