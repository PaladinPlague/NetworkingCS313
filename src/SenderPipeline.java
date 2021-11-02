import java.util.*;

public class SenderPipeline extends TransportLayer {


    ArrayList<TransportLayerPacket> sndPkt;
    ArrayList<TransportLayerPacket> rcvPkt;
    int prevSeqNum;
    //QUESTION FOR MICHAEL: my implementation assumes this variable is representing the sequence number of the minimum packet that hasn't been sent yet. Is this correct?
    int seqNumSending;
    static byte[] sendingData;
    String status;
    Queue<byte[]> dataQueue;

    //Holds the size of the window
    int windowSize;

    //Holds list of all known acknowledged packets based on numbers
    ArrayList<Integer> acked;

    public SenderPipeline(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        //initialise all variable
        System.out.println("SENDER: " + getName() + " has been initialised");
        sndPkt = new ArrayList<>();
        rcvPkt = new ArrayList<>();
        prevSeqNum = 1;
        seqNumSending = 0;
        status = "Ready";
        dataQueue = new LinkedList<>();

        //PLACEHOLDER: unsure about length of window size.
        windowSize = 10;
    }

    @Override
    public void rdt_send(byte[] data) {

        System.out.println();
        System.out.println("______________________________");
        if(Objects.equals(status, "Ready")||Objects.equals(status, "Resend")){
            System.out.print("SENDER: The data we got: ");
            System.out.println(Arrays.toString(data)); //to check what data we have been passed


            System.out.println("SENDER: making packet "+seqNumSending+" for data we got");
            //The next packet in the sequence is gotten from the data in this variable, with its sequence number equalling the sequence number of this
            //QUESTION FOR MICHAEL: where is the process of the packet data within this segment supposed to be carried out.
            sndPkt.add(mk_pkt(seqNumSending, data)); //make the packet using mk_pkt()

            //PLACEHOLDER OPERATION to send packets to reciever. May need more details about when we get enough packets to have a full window of non-null packets ready to send.
            sendWindow();

            sendingData = new byte[data.length];
            System.arraycopy(data, 0, sendingData, 0, sendingData.length);

            status="Ready";
            timerInterrupt();
        }else{
            System.out.println("WAIT");
            dataQueue.add(data);
            //System.out.println("data Queue contains: "+ Arrays.toString(dataQueue.poll()));
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

        rcvPkt.add(new TransportLayerPacket(pkt));
        System.out.println("______________________________");
        System.out.println("SENDER: receiving ACK packet");
        System.out.println("SENDER: checking received ACK packet");
        //Operation works on the newly added receive packet.
        if (!isACK(rcvPkt.get(rcvPkt.size() - 1))){
            //if pkt is corrupted or the ACK num is not the right one then
            //wait until timer runs out
            System.out.println("SENDER: ACK packet received is corrupted or not ACK, waiting for time out.");

            //Due to the fact that the previous recieved packet is corrupted, remove it from our list of received packets.
            rcvPkt.remove(rcvPkt.size() - 1);

            status = "Resend";
            timerInterrupt();

        }else{
            //if everything is fine then stop timer waiting to be called from above
            System.out.println("SENDER: ACKed for packet " + rcvPkt.get(rcvPkt.size() - 1).getSeqNum());

            //Add acknowledgement number of this new packet to our list of acknowledgement numbers found.
            acked.add(rcvPkt.get(rcvPkt.size() - 1).getAckNum());

            //If the acknowledgement corresponds to the base packet from the window, move window up
            if (rcvPkt.get(rcvPkt.size() - 1).getAckNum() == seqNumSending) {
                //Show that the sequnence number base has been moved up, and what the previous number bases were.
                System.out.print("SENDER: sequence base moved past following packets: " + seqNumSending);

                prevSeqNum = seqNumSending;
                seqNumSending = (seqNumSending + 1);

                //Move base through all packets after base that have also been acknowledged. Show these moved past bases in the output
                while (acked.contains(seqNumSending)) {
                    System.out.print(", " + seqNumSending);
                    prevSeqNum = seqNumSending;
                    seqNumSending = (seqNumSending + 1);
                }
                System.out.println();
            }



            status = "Ready";
            simulator.stopTimer(this);
            System.out.println("SENDER: Timer stopped");
            if(!dataQueue.isEmpty()){
                rdt_send(dataQueue.poll());
            }

        }
        System.out.println("______________________________");
        System.out.println();

    }


    public boolean isACK(TransportLayerPacket rcvPkt){
        if (rcvPkt != null && rcvPkt.getSeqNum() == seqNumSending){
            //check if the ACK is the correct ACK
            // when we wait for ACK 0, and we got ACK 1 then we know it is not the right one
            //if we received the correct ACK num then return true else false
            return rcvPkt.getAckNum() == 1;
        }
        return false;
    }

    @Override
    public void timerInterrupt() {
        System.out.println("______________________________");
        System.out.println("SENDER: timerInterrupt");

        if(Objects.equals(status, "Ready")){

            //Assume that the most recent packet from the array is the one being sent to the server
            System.out.println("SENDER: packet "+sndPkt.get(sndPkt.size() - 1).getSeqNum()+" sent to Network layer");

            //resend all packets from window.
            //NOTE: using this method assumes that we resend packets even if they have been sent but not yet ACKED, which should be caught by duplication case in receiver class.
            sendWindow();

            System.out.println("SENDER: timer started");
            simulator.startTimer(this,10); //call sim function start the timer (timer kept time taken between send a packet and receives ACK)
            System.out.println("______________________________");
            System.out.println();
            status = "Sent&Wait";

        }else if(Objects.equals(status,"Resend")){
            simulator.stopTimer(this);
            System.out.println("SENDER: timer stopped, time out!");
            System.out.println("SENDER: The data we are trying to Resend " + Arrays.toString(sendingData));
            System.out.println("SENDER: Resending the packet");
            System.out.println("______________________________");
            rdt_send(sendingData);
        }else{
            System.out.println("SENDER: Please Wait for the ACK of prev packet.");
        }
    }

    /**
     * Function called to send all packets from window in list of sender packets.
     */
    public void sendWindow () {
        //System.out.print statements are used to debug output of receiver packet removals. When this starts, a boolean variable will change
        boolean noOutput = true;
        //Loop over all packets in window, assuring that we do not go out of the index of the arrayList
        for (int i = seqNumSending; i < seqNumSending + windowSize && i < sndPkt.size(); i++) {
            //If the list of acknowledged packets doesn't contain an acknowledgement for this packet, then send it.
            //Be sure we do not send a null packet as well.
            if (!acked.contains(sndPkt.get(i).getSeqNum()) && sndPkt.get(i) != null) {
                //Output strings of removed packets by their related sequence numbers.
                if (noOutput) {
                    System.out.print("SENDER: packets from window are sent to receiver: " + sndPkt.get(i).getSeqNum());
                    noOutput = false;
                } else {
                    System.out.print(", " + sndPkt.get(i).getSeqNum());
                }
                simulator.sendToNetworkLayer(this, sndPkt.get(i));
            }
         }
        //If we have output, print to next line.
        if (!noOutput)
            System.out.println();
    }
}
