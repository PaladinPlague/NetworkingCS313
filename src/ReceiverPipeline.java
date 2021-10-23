public class ReceiverPipeline extends TransportLayer{

    byte[]data;
    TransportLayerPacket sndPkt;
    TransportLayerPacket rcvPkt;
    TransportLayerPacket[] buffer;
    Checksum checksum;
    private int sendBase;
    private int expectedNext;
    private boolean[] sent;
    private boolean[] acked;
    private int bufferIndex;


    public ReceiverPipeline(String name, NetworkSimulator simulator, int length) {

        super(name, simulator);

        sendBase = 0;
        expectedNext = 0;
        sent = new boolean[length];
        acked = new boolean[length];
        bufferIndex = 0;
    }


    @Override
    public void init() {

        expectedNext = 0;
        sndPkt = new TransportLayerPacket(0, data, checksum);

    }

    @Override
    public void rdt_send(byte[] data) {

        simulator.sendToNetworkLayer(this, this.sndPkt);


    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        if (pkt.checksum != null && pkt.getSeqnum() == expectedNext) {
            rcvPkt = pkt;
            data = pkt.getData();
            sent[pkt.getSeqnum()] = true;
            sndPkt = new TransportLayerPacket(expectedNext, sndPkt.getData(), checksum);
            expectedNext += 1;
            rdt_send(data);
        } else {
            buffer[bufferIndex] = pkt;
            bufferIndex += 1;
        }
    }

    @Override
    public void timerInterrupt() {

    }
}
