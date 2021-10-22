public class ReceiverPipeline extends TransportLayer{

    byte[]data;
    TransportLayerPacket[] sndPkt;
    TransportLayerPacket[] rcvPkt;
    private int sendBase;
    private int nextSeqnum;
    private boolean[] sent;
    private boolean[] acked;


    public ReceiverPipeline(String name, NetworkSimulator simulator) {

        super(name, simulator);


    }


    @Override
    public void init() {
    }

    @Override
    public void rdt_send(byte[] data) {

    }

    @Override
    public void rdt_receive(TransportLayerPacket[] pkt) {

    }

    @Override
    public void timerInterrupt() {
        simulator.startTimer(this, 1.0);
    }
}
