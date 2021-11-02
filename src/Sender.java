
import java.util.*;

public class Sender extends TransportLayer {


    TransportLayerPacket sndPkt;
    TransportLayerPacket rcvPkt;
    int prevSeqNum;
    int seqNumSending;
    static byte[] sendingData;
    String status;
    Queue<byte[]> dataQueue;
    Queue <byte[]> dataList;
    boolean doSend = false;

    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        //initialise all variable
        System.out.println("SENDER: " + getName() + " has been initialised");
        sndPkt = null;
        rcvPkt = null;
        prevSeqNum = 1;
        seqNumSending = 0;
        status = "Ready";
        dataQueue = new LinkedList<>();
        dataList = new LinkedList<>();
    }

    @Override
    public void rdt_send(byte[] data) {

        if(doSend == false) {

                System.out.println();
                System.out.println("______________________________");

                System.out.print("SENDER: The data we got: ");
                System.out.println(Arrays.toString(data));

                System.out.println("SENDER: making packet " + seqNumSending + " for data we got");
                sndPkt = mk_pkt(seqNumSending, data);
                simulator.sendToNetworkLayer(this, sndPkt);
                simulator.startTimer(this, 100);
                doSend = true;

        }
        else{
            dataQueue.add(data);
        }
    }

    public TransportLayerPacket mk_pkt(int seqNum, byte[] data){

        Checksum checksum = new Checksum(data);//initialise Checksum passing data into Checksum
        String checksumValue = checksum.createCheckSum(); //generate checksum

        //use constructor to build new packet
        return new TransportLayerPacket(seqNum,0,data,checksumValue);

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        doSend = true;
        int ack = pkt.getAckNum();


        if(ack == 0){
            simulator.stopTimer(this);
            if(!dataQueue.isEmpty()){
                doSend = false;
                rdt_send(dataQueue.poll());
            }
        }
        else {
            /// Do nothing...

        }

    }


    @Override
    public void timerInterrupt() {

        rdt_send(sndPkt.getData());
        simulator.startTimer(this,100);


    }
}
