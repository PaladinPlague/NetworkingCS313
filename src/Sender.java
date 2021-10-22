public class Sender extends TransportLayer {

    byte[]data;
    TransportLayerPacket sndPkt;
    TransportLayerPacket rcvPkt;
    Checksum checksum;
    int nextSeqnum;
    int base;
    int timer;
    int timerLimit;
    int N;


    public Sender(String name, NetworkSimulator simulator) {

        super(name, simulator);
    }


    @Override
    public void init() {

        data = null;
        sndPkt = new TransportLayerPacket(null);
        rcvPkt = new TransportLayerPacket(null);
        base = 1;
        nextSeqnum = 1;

    }

    @Override
    public void rdt_send(byte[] data) {
        if (nextSeqnum < base+N) {
            //set new sendpacket with next sequence number, data and checksum
            simulator.sendToNetworkLayer(this,this.sndPkt);
            if (base == nextSeqnum) {
                simulator.startTimer(this, 1);
            }
        } else {
            //Refuse the data
        }
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        rcvPkt = pkt;
        nextSeqnum += 1;
    }

    @Override
    public void timerInterrupt() {
        rdt_send(this.sndPkt.getData());

    }


}
