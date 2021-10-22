public class SenderPipeline extends TransportLayer {

    byte[]data;
    TransportLayerPacket[] sndPkt;
    TransportLayerPacket[] rcvPkt;
    Checksum checksum;
    private int N;
    private int sendBase;
    private int nextSeqnum;
    private boolean[] sent;
    private boolean[] acked;


    public SenderPipeline(String name, NetworkSimulator simulator, int length, int windowSize) {

        super(name, simulator);

        sndPkt = new TransportLayerPacket[length];
        rcvPkt = new TransportLayerPacket[length];
        N = windowSize;
        nextSeqnum = 0;

    }


    @Override
    public void init() {
    }

    @Override
    public void rdt_send(byte[] data) {
        for (int i = sendBase; i < sendBase + N; i++) {
           if (i > nextSeqnum) {
               nextSeqnum = i;
           }
           simulator.sendToNetworkLayer(this, this.sndPkt[i]);
           sent[i] = true;
        }
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        this.rcvPkt[i] = pkt;
    }

    public void timeout() {
        boolean flawFound = false;
        for (int i = sendBase; i < nextSeqnum && flawFound; i++) {
            if (sent[i] && acked[i]) {
                i += 1;
            } else {
                sendBase = i;
                rdt_send(data);
            }
        }
    }

    @Override
    public void timerInterrupt() {

    }


}
