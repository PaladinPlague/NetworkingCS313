public class ReceiverPipeline extends TransportLayer{

    byte[]data;
    TransportLayerPacket[] sndPkt;
    TransportLayerPacket[] rcvPkt;
    TransportLayerPacket[] buffer;
    Checksum checksum;
    private int sendBase;
    private int expectedNext;
    private boolean[] sent;
    private boolean[] acked;
    private int bufferIndex;
    private int lastRecieved;

    Receiver receiver;
    int prevSeqNum;


    public ReceiverPipeline(String name, NetworkSimulator simulator, int length) {

        super(name, simulator);
    }


    @Override
    public void init() {
        //Initiate expected next and buffer index integers for program
        expectedNext = 0;
        bufferIndex = 0;
    }

    @Override
    public void rdt_send(byte[] data) {

        simulator.sendToNetworkLayer(this, sndPkt[lastRecieved]);

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        //Show within arrays that expected sending packet has been received, and send acknowledgement packet to sender
        rcvPkt[expectedNext] = pkt;
        sent[expectedNext] = true;
        lastRecieved = pkt.getSeqNum();
        rdt_send(sndPkt[lastRecieved].getData());

        //If sent packet is the next one the receiver expects, set the next one to the packet after
        if (pkt.getSeqNum() == expectedNext) {
            expectedNext += 1;
        //otherwise, put the packet in the buffer list an increment it's current next number
        } else {
            buffer[bufferIndex] = pkt;
            bufferIndex += 1;
        }
    }

    //This function delivers any buffered packets that are contained
    public void deliverBuffered() {
        //Go through all index positions that should have buffer content
        for (int i = 0; i < bufferIndex && i < buffer.length; i++) {
            deliver(buffer[i]);
            buffer[i] = null;
        }
        //Reset buffer index postion
        bufferIndex = 0;
    }

    //This function carries out the delivery process of a packet
    public void deliver(TransportLayerPacket pkt) {
        //Currently unsure as to how to approach this
    }

    @Override
    public void timerInterrupt() {

    }
}
