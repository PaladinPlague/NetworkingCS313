import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class SenderBackup extends TransportLayer {


    TransportLayerPacket sndPkt; //the sending packet segment we are currently processing
    TransportLayerPacket rcvPkt; //the acknowledgment packet segment we are current processing
    int prevSeqNum; //the last seq number we have processed
    int seqNumSending; //the current seq number we are processing
    static byte[] sendingData; //save a copy of original data used for resending
    String status; //register the status of sender currently at
    Queue<byte[]> dataQueue; //to keep track of the all the  data we need to send and process based on the order of queue

    public SenderBackup(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        //initialise all variable
        System.out.println("SENDER: " + getName() + " has been initialised");
        sndPkt = null; //starting with null segment
        rcvPkt = null;//starting with null segment
        prevSeqNum = 1;//if the current seqNum start from 1 then the previous must be 1(S&W only uses 0&1)
        seqNumSending = 0;//at very start seqNum is 0
        status = "Ready";//status set Ready, so it can take data to pack and process
        dataQueue = new LinkedList<>(); //set up the new queue use LinkedList since it can take as many as it asked
    }

    @Override
    public void rdt_send(byte[] data) {

        System.out.println();
        System.out.println("______________________________");

        /*
         * check if the status of sender is either in Ready (ready to send) or in Resend (ready to resend)
         * only pack and send any message if sender is ready or need to resend
         * else add the message data into the queue to be processed later once Sender is ready
         */
        if(Objects.equals(status, "Ready")||Objects.equals(status, "Resend")){

            //to check what data we have been passed
            System.out.println("SENDER: The data we got: " + Arrays.toString(data));

            //make the packet using mk_pkt()
            System.out.println("SENDER: making packet "+seqNumSending+" for data we got");
            sndPkt = mk_pkt(seqNumSending, data);

            //copy the data we got into sendingData to keep a copy for resending.
            sendingData = new byte[data.length];
            System.arraycopy(data, 0, sendingData, 0, sendingData.length);

            //call sim function to perform udt_send() send to NetworkLayer
            System.out.println("SENDER: packet "+sndPkt.getSeqNum()+" sent to Network layer");
            simulator.sendToNetworkLayer(this,sndPkt);

            //call sim function start the timer (timer kept time taken between send a packet and receives ACK)
            System.out.println("SENDER: timer started");
            simulator.startTimer(this,100);

            /*
             * set the status of sender to Sent&Wait (make the Sender not available)
             * in this status Sender don't send anything, any more data come in will be added to queue
             */
            status = "Sent&Wait";

            System.out.println("______________________________");
            System.out.println();

        }else{
            //Sender not available to process the data, save it into queue. Process it later.
            System.out.println("SENDER: WAIT");
            dataQueue.add(data);
        }
    }

    /*
     * This method make the packet to be sent
     */
    public TransportLayerPacket mk_pkt(int seqNum, byte[] data){

        Checksum checksum = new Checksum(data);//initialise Checksum passing data into Checksum
        String checksumValue = checksum.createCheckSum(); //generate checksum

        //use constructor to build new packet
        return new TransportLayerPacket(seqNum,0,data,checksumValue);

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        //copy the packet we received (pkt) into field rcvPkt, so we can reference it easier
        rcvPkt = new TransportLayerPacket(pkt);

        System.out.println("______________________________");
        System.out.println("SENDER: receiving ACK packet");
        System.out.println("SENDER: checking received ACK packet");

        /*
         * check the Acknowledgement packet we got is ACKed and nothing corrupted (i.e. if ackNum has been corrupted)
         * if pkt is corrupted or the ACK num is not the right one then
         * do nothing wait for timeout to happen
         */
        if (!isACK(rcvPkt)){

            System.out.println("SENDER: ACK packet received is corrupted or not ACK, waiting for time out.");

        }else{

            //if everything is fine then stop timer waiting to be called from above
            System.out.println("SENDER: ACKed for packet " + rcvPkt.getSeqNum());

            //we finished with this packet set the current seqNum to be the prevSeqNum
            prevSeqNum = seqNumSending;
            //flip the current seqNum
            seqNumSending = (seqNumSending^1);

            //set status of Sender to Ready, since we are ready to process the next message data
            status = "Ready";

            //call sim function to stop the timer, indicate we are done with the current packet
            simulator.stopTimer(this);
            System.out.println("SENDER: Timer stopped");

            /*
             * check if the dataQueue still has any unprocessed message data
             * there is then process them one after another
             * else JOB DONE terminate there
             */
            if(!dataQueue.isEmpty()){
                //process the message data at the top of the queue
                rdt_send(dataQueue.poll());
            }

        }
        System.out.println("______________________________");
        System.out.println();

    }


    /*
     * check if the Acknowledgement we got is the correct ACK we are looking for
     * when we wait for ACK of seqNum 0, and we got ACK of seqNum 1 then we know it is not the right one
     * if we received the correct ACK num then return true
     * else false
     */
    public boolean isACK(TransportLayerPacket rcvPkt){
        if (rcvPkt != null && rcvPkt.getSeqNum() == seqNumSending){

            return rcvPkt.getAckNum() == 1;
        }
        return false;
    }

    @Override
    public void timerInterrupt() {

        System.out.println("______________________________");
        System.out.println("SENDER: RESEND");

        System.out.println("SENDER: The data we are trying to Resend " + Arrays.toString(sendingData));
        System.out.println("SENDER: Resending the packet");
        System.out.println("______________________________");

        /*
         * set the status of Sender to Resend and pass the saved original data into rdt_send() to Resend
         */
        status = "Resend";
        rdt_send(sendingData);

    }
}
