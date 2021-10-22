public class Receiver extends TransportLayer{

    int nextSeqnum;
    int base;
    int timer;
    int N;

    public Receiver(String name, NetworkSimulator simulator) {

        super(name, simulator);


    }


    @Override
    public void init() {
        base = 0;
        nextSeqnum = 0;
    }

    @Override
    public void rdt_send(byte[] data) {
        if (nextSeqnum < base+N) {
            //sndpkt[nextseqnum] = make_pkt(nextseqnum,data,chksum);

        }
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        //extract(pkt.getData());
        //deliver_data
    }

    @Override
    public void timerInterrupt() {
        simulator.startTimer(this, 1.0);
    }
}
