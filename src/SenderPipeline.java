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
    private int timeout;


    public SenderPipeline(String name, NetworkSimulator simulator, int length, int windowSize, int timer) {

        super(name, simulator);

        sndPkt = new TransportLayerPacket[length];
        rcvPkt = new TransportLayerPacket[length];
        N = windowSize;
        this.timeout = timer;
        sent = new boolean[length];
        acked = new boolean[length];

    }


    @Override
    public void init() {

        nextSeqnum = 0;
        sendBase = 0;

    }

    @Override
    public void rdt_send(byte[] data) {
        if (nextSeqnum < sendBase) {
            for (int i = sendBase; i < sendBase + N; i++) {
                if (i > nextSeqnum) {
                    nextSeqnum = i;
                }
                this.sndPkt[i].setSeqnum(i);
                simulator.sendToNetworkLayer(this, this.sndPkt[i]);
                sent[i] = true;
            }
        }
        simulator.startTimer(this, timeout);
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        if (pkt.getAcknum() == sendBase && sendBase + N < sndPkt.length) {
            this.rcvPkt[pkt.getAcknum()] = pkt;
            acked[pkt.getAcknum()] = true;
            if (pkt.getAcknum() == N + sendBase) {
                while (acked[sendBase]) {
                    sendBase += 1;
                }
            }
        }

    }

    @Override
    public void timerInterrupt() {
        simulator.startTimer(this, simulator.simulationTime);

        for (int i = sendBase; i < nextSeqnum; i++) {
            rdt_send(sndPkt[i].getData());
        }

        //Prototype version of resend packets - May or may not want to discard
        /*boolean flawFound = false;
        for (int i = sendBase; i < nextSeqnum && flawFound; i++) {
            if (sent[i] && acked[i]) {
                i += 1;
            } else {
                flawFound = true;
                sendBase = i;
                rdt_send(data);
            }
        }*/
    }


}
