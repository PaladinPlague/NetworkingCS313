public class SenderPipeline extends TransportLayer {

    byte[]data;
    TransportLayerPacket[] sndPkt;
    TransportLayerPacket[] rcvPkt;
    TransportLayerPacket[] delivered;
    Checksum checksum;
    private int N;
    private int sendBase;
    private boolean[] sent;
    private boolean[] acked;
    private int timeout;


    public SenderPipeline(String name, NetworkSimulator simulator, int length, int windowSize, int timer) {
        super(name, simulator);
    }


    @Override
    public void init() {

        //Initially, sendBase (first element of window) is 0 and next sequence number expected is 0.
        sendBase = 0;

    }

    @Override
    public void rdt_send(byte[] data) {
        //Loop through all packets of current window
        for (int nextSeqnum = sendBase; nextSeqnum < sendBase + N; nextSeqnum++) {
            //If acknowledgement for this packet is not yet sent, send it.
            if (!acked[nextSeqnum]) {
                simulator.sendToNetworkLayer(this,this.sndPkt[nextSeqnum]);
            }
        }
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        //put recieved packet into relevant part of array, along with setting boolean value in "acked" array true
        rcvPkt[pkt.getAckNum()] = pkt;
        acked[pkt.getAckNum()] = true;
        //Check if the acknowledgement for this packet is of the first packet yet to be acknowledged
        if(pkt.getAckNum() == sendBase) {
            //If this is the case, check if the window size can be shifted right and then carry it out until no longer possible or unacked packet is found.
            while (!acked[sendBase] && sendBase + N < sndPkt.length) {
                sendBase += 1;
            }
        }
    }

    @Override
    public void timerInterrupt() {
        //resend packet at sequence base number and wait for acknowledement
        rdt_send(sndPkt[sendBase].getData());
    }


}
