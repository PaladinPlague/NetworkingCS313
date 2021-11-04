import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Sender extends TransportLayer {


    TransportLayerPacket sndPkt; //the sending packet segment we are currently processing
    TransportLayerPacket rcvPkt; //the acknowledgment packet segment we are current processing
    int prevSeqNum; //the last seq number we have processed
    int seqNumSending; //the current seq number we are processing
    static byte[] sendingData; //save a copy of original data used for resending
    String status; //register the status of sender currently at

    ArrayList<byte[]> dataList; //set up ArrayList where any bytes stored can be evaluated at any time
    int sendBase; //Holds the base position of the window
    int windowSize; //Holds the size of the window. Assume that the size is 10 packets
    ArrayList<Integer> acked; //Holds list of all known acknowledged packet numbers for current window

    ArrayList<Integer> totalAcked; //Holds list of all acknowledged packet numbers for debugging purposes
    /*NOTE: there are two lists based on holding acknowledgement numbers, but "acked" represents all numbers
    Currently compared against packets. "TotalAcked" represents the actual packets used for measuring output in terminal window.*/

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
        windowSize = 10; //Assume that window size of sender packets is 10
        acked = new ArrayList<>(); //starting with empty list of acknowledged packet numbers

        totalAcked = new ArrayList<>(); //set up list of all total acknowledgement packets, used for debugging/outputting results

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

            //If this is our first packet, it will be sent before there is any data from the list, so we add it initially as part of the list.
            if (dataList.isEmpty())
                dataList.add(data);

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
            System.out.println("SENDER: WAIT - data " + Arrays.toString(data) + " held in list.");
            //Archive data in list of later data.
            dataList.add(data);
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

            totalAcked.add(rcvPkt.getSeqNum());

            System.out.print("ALL ACKNOWLEDGEMENT PACKETS RECEIVED: ");
            for (int i = 0; i < totalAcked.size(); i++) {
                System.out.print(totalAcked.get(i) + " ");
            }
            System.out.println();

            //If the sequence number of the recieved packet is that of the base, perform the operation to move up.
            if (rcvPkt.getSeqNum() == sendBase) {

                //Move up the base packets until we reach one that currently has not been acknowledged
                while (acked.contains(sendBase)) {
                    //Remove initial packet, moving all other elements back by 1.
                    dataList.remove(0);
                    //New sendBase packet corresponds to previous base + 1
                    sendBase += 1;
                    //Execute new packets available from updated send base.
                    //If the acknowledged packet is the first one, continue for the rest of the packets in the new window.
                    if (sendBase <= 1) {

                        sendWindow("Ready");

                        //Otherwise, check if we can send the packet at the end of the window, and if so, send it.
                    } else if (sendBase + windowSize < dataList.size() && dataList.get(sendBase + windowSize) != null) {

                        //call sim function to stop the timer, indicate we are done with the current packet
                        simulator.stopTimer(this);
                        System.out.println("SENDER: Timer stopped");

                        //set status of Sender to Ready, since we are ready to process the next message data
                        status = "Ready";
                        //Send the packet at the index of the window, with the sequence number sent with it being adjusted properly.
                        seqNumSending = sendBase + windowSize;
                        rdt_send(dataList.get(sendBase + windowSize));

                    }

                }



                //Perform operation to remove all acked numbers before sendBase to remove no longer needed data held in memory for program
                ackedRemovePrevious();


            }

            //we finished with this packet set the current seqNum to be the prevSeqNum
            prevSeqNum = seqNumSending;
            //flip the current seqNum
            seqNumSending = (seqNumSending^1);



        }
        System.out.println("______________________________");
        System.out.println();

    }


    /*
     * check if the Acknowledgement we got is the correct ACK we are looking for
     * Valid Ack numbers are one where the packet isn't null and has an acknowledgement number of 1, is within the sender window, and hasn't already been acknowledged.
     */
    public boolean isACK(TransportLayerPacket rcvPkt){
        if (rcvPkt != null && !acked.contains(rcvPkt.getSeqNum()) && rcvPkt.getSeqNum() >= sendBase && rcvPkt.getSeqNum() <= sendBase + windowSize) {
            return rcvPkt.getAckNum() == 1;
        }
        return false;
    }



    /**
     * Function called to send all packets from window in list of sender packets.
     * statusSet is "Resend" if it is sent from a timer interrupt and "Ready" otherwise to emulate the effects of ensuring the sender is working in any case
     */
    public void sendWindow (String statusSet) {
        System.out.println("______________________________");
        System.out.println("SENDER: sending packets in window to reciever");
        //System.out.print statements are used to debug output of receiver packet removals. If no packets are sent, send a unique message.
        boolean noOutput = true;
        //Loop over all packets in window (first N elements of array where N = windowSize), assuring that we do not go out of the index of the arrayList
        for (int i = 0; i < windowSize && i < dataList.size(); i++) {

            //The sequence number of the packet should equal the sendbase plus the index in the current arraylist.
            int thisSeqNo = i + sendBase;

            //If the list of acknowledged packets doesn't contain an acknowledgement for this packet, then send it.
            //Be sure we do not send a null packet as well.

            if (!acked.contains(thisSeqNo) && dataList.get(i) != null) {

                //Show that there has been at least one input from the window sending process if this is the case, or none otherwise.
                if (noOutput) {
                    noOutput = false;
                }
                //Output strings of removed packets by their related sequence numbers.
                System.out.print("SENDER: packet from window sent to receiever: " + thisSeqNo);

                //set status of Sender to what allows us to resend each packet using the send method.
                status = statusSet;
                //Send the packet at the index of the window, with the sequence number sent with it being adjusted properly.
                seqNumSending = thisSeqNo;
                rdt_send(dataList.get(i));
            }
        }

        //Now that sender process is over, set sending status back to sent & wait
        status = "Sent&Wait";

        //If we have no output, show this to terminal
        if (noOutput)
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
        sendWindow("Resend");

    }
}
