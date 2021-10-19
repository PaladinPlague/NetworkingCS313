public class Sender extends TransportLayer {

    byte[]data;
    TransportLayerPacket sndPkt;
    TransportLayerPacket rcvPkt;
    Checksum checksum;


    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        data = null;
        sndPkt = new TransportLayerPacket(null);
        rcvPkt  = new TransportLayerPacket(null);

    }

    @Override
    public void rdt_send(byte[] data) {
        sndPkt.makePkt(0, data);

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        rcvPkt = pkt;
    }

    @Override
    public void timerInterrupt() {

    }
}
