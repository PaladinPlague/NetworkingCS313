import java.util.*;

public class Sender extends TransportLayer {


    TransportLayerPacket sndPkt; //the sending packet segment we are currently processing
    TransportLayerPacket rcvPkt; //the acknowledgment packet segment we are current processing
    int prevSeqNum; //the last seq number we have processed
    int seqNumSending; //the current seq number we are processing
    static byte[] sendingData; //save a copy of original data used for resending
    String status; //register the status of sender currently at

    ArrayList<byte[]> dataList; //set up ArrayList where any bytes stored can be evaluated at any time
    int sendBase; //Holds the base position of the window
    int windowSize; //Holds the size of the window
    ArrayList<Integer> acked; //Holds list of all known acknowledged packet numbers

    public Sender(String name, NetworkSimulator simulator) {
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

        dataList = new ArrayList<>(); //set up the new ArrayList

        sendBase = 0; //At start, sendBase of sequence is 0
        int windowSize = 10; //PLACEHOLDER: unsure about length of window size.
        acked = new ArrayList<>(); //starting with empty list of acknowledged packet numbers

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
            //Sender not available to process the data, present message.
            System.out.println("SENDER: WAIT");
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

            //Add sequence number of this number to our list of acknowledged packets based on their sequence numbers.
            acked.add(rcvPkt.getSeqNum());

            //If the sequence number of the recieved packet is that of the base, perform the operation to move up.
            if (rcvPkt.getSeqNum() == sendBase) {

                //Move up the base packets until we reach one that currently has not been acknowledged
                while (acked.contains(sendBase)) {
                    //Remove initial packet, moving all other elements back by 1.
                    dataList.remove(0);
                    //New sendBase packet corresponds to previous base + 1
                    sendBase += 1;
                }

                //Perform operation to remove all acked numbers before sendBase to remove data held in memory for program
                ackedRemovePrevious();
                //As new packets should now be accessible, perform send operation of packets from window again
                sendWindow();

            }

            //we finished with this packet set the current seqNum to be the prevSeqNum
            prevSeqNum = seqNumSending;
            //flip the current seqNum
            seqNumSending = (seqNumSending^1);

            //set status of Sender to Ready, since we are ready to process the next message data
            status = "Ready";

            //call sim function to stop the timer, indicate we are done with the current packet
            simulator.stopTimer(this);
            System.out.println("SENDER: Timer stopped");


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
        if (rcvPkt != null && rcvPkt.getSeqNum() == seqNumSending) {

            return rcvPkt.getAckNum() == 1;
        }
        return false;
    }



    /**
     * Function called to send all packets from window in list of sender packets.
     */
    public void sendWindow () {
        System.out.println("______________________________");
        System.out.println("SENDER: sending packets in window to reciever");
        //System.out.print statements are used to debug output of receiver packet removals. When this starts, a boolean variable will change
        boolean noOutput = true;
        //Loop over all packets in window (first N elements of array where N = windowSize), assuring that we do not go out of the index of the arrayList
        for (int i = 0; i < windowSize && i < dataList.size(); i++) {

            //The sequence number of the packet should equal the sendbase plus the index in the current arraylist.
            int thisSeqNo = i + sendBase;

            //If the list of acknowledged packets doesn't contain an acknowledgement for this packet, then send it.
            //Be sure we do not send a null packet as well.

            if (!acked.contains(thisSeqNo) && dataList.get(i) != null) {
                //Output strings of removed packets by their related sequence numbers.
                if (noOutput) {
                    System.out.print("SENDER: packets from window are sent to receiver: " + thisSeqNo);
                    noOutput = false;
                } else {
                    System.out.print(", " + thisSeqNo);
                }
                //Send the packet at the index of the window, with the sequence number sent with it being adjusted properly.
                seqNumSending = thisSeqNo;
                rdt_send(dataList.get(i));
            }
        }
        //If we have no output, show this to terminal
        if (!noOutput)
            System.out.println("SENDER: no packets in window were found to be sent");
        System.out.println("______________________________");
        System.out.println();
    }


    /**
     * When the sendBase is pushed up, remove all numbers from list of acked packets that are smaller than new sendBase to reduce size
     */
    public void ackedRemovePrevious() {
        //Use while loop, as we may need to examine one index again if an element is removed and the next one appears in its place
        int i = 0;
        while (i < acked.size()) {
            //If the sequence number here is less than the base, remove the item.
            if (acked.get(i) < sendBase) {
                acked.remove(i);
                //Otherwise, move onto next item in existing list.
            } else {
                i += 1;
            }
        }
    }

    @Override
    public void timerInterrupt() {

        System.out.println("______________________________");
        System.out.println("SENDER: RESEND");

        System.out.println("SENDER: The data we are trying to Resend " + Arrays.toString(sendingData));
        System.out.println("SENDER: Resending the packet");
        System.out.println("______________________________");

        /*
         * set the status of Sender to Resend and send the data from the window again
         */
        status = "Resend";
        sendWindow();

    }
}
