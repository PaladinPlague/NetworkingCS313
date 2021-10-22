public class ReceiverPipeline extends TransportLayer{

    byte[]data;
    TransportLayerPacket[] sndPkt;
    TransportLayerPacket[] rcvPkt;
    TransportLayerPacket[] buffer;
    private int sendBase;
    private int expectedNext;
    private boolean[] sent;
    private boolean[] acked;
    private int bufferIndex;


    public ReceiverPipeline(String name, NetworkSimulator simulator, int length) {

        super(name, simulator);

        sndPkt = new TransportLayerPacket[length];
        rcvPkt = new TransportLayerPacket[length];
        buffer = new TransportLayerPacket[length];
        sendBase = 0;
        expectedNext = 0;
        sent = new boolean[length];
        acked = new boolean[length];
        bufferIndex = 0;
    }


    @Override
    public void init() {


    }

    @Override
    public void rdt_send(byte[] data) {

        for (int i = 0; sndPkt[i] != null; i++) {
            simulator.sendToNetworkLayer(this, this.sndPkt[i]);
        }


    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        if (pkt.getSeqnum() == expectedNext) {
            rcvPkt[pkt.getSeqnum()] = pkt;
            sent[pkt.getSeqnum()] = true;
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
