import java.util.Arrays;
import java.util.ArrayList;

public class Receiver extends TransportLayer{

    TransportLayerPacket rcvPkt;//the received packet segment we are current processing
    int rcvBase; //the sequence number whose packet we are hoping to see next, where the window of the packet sequence starts.
    ArrayList<TransportLayerPacket> buffer; //Holds a list of packets that are currently buffered
    int prev_SeqNum; //the last seq number we have processed

    int windowSize; //Holds the size of the window, as sequenced packets should not be able to be delivered after this.

    ArrayList<Integer> totalSeqNums; //Holds the total list of sequence numbers - used for displaying results of program.

    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init(){

        //initialise all variable
        System.out.println("RECEIVER: " + getName() + " has been initialised");
        //Initialise buffered packets as new list of packets.
        buffer = new ArrayList<>();
        rcvPkt = null;//starting with null segment
        rcvBase = 0; //Starting with 0 as receiver base
        prev_SeqNum = 1; //since the first packet we will receive is also pkt 0 then it is safe to assume the prev_SeqNum is 1
        windowSize = 10; //Assume that window size of receiver packets is 10

        totalSeqNums = new ArrayList<>(); //construct list of sequence numbers

    }

    @Override
    public void rdt_send(byte[] data) {

        System.out.println("______________________________");
        System.out.println("RECEIVER: sending ACK to sender for packet with seqNum of " + rcvPkt.getSeqNum());

        //if everything is being checked and being process make a pkt to send ACK back to sender
        TransportLayerPacket sendingPkt = mk_pkt(rcvPkt.getSeqNum());

        //call sim function to perform udt_send() send to networkLayer
        // This line is sending daa to Network stimulator with this data
        simulator.sendToNetworkLayer(this,sendingPkt);
        prev_SeqNum = rcvPkt.getSeqNum();

        System.out.println("______________________________");
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        System.out.println("______________________________");
        //get the received packet and turn into a usable packet
        rcvPkt = new TransportLayerPacket(pkt);

        if (corruption()) {
            System.out.println("RECEIVER: Received the packet "+rcvPkt.getSeqNum()+".");
            //if it corrupted, ignore the current packet, waiting for timeout on sender side and be prepared to receive the next incoming packet
            System.out.println("RECEIVER: Received pkt but problem found, waiting for sender to resend.");

        } else {

            //If we get a packet that is not part of the current receiver window, reject this packet.
            if (rcvPkt.getSeqNum() < rcvBase || rcvPkt.getSeqNum() > rcvBase + windowSize) {

                System.out.println("RECEIVER: Packet "+rcvPkt.getSeqNum()+" received, but is out of receiver window and is discarded.");

            //If this is the next number we expect, we deliver the content, move the base of the window up and checke for any packets in buffer
            } else if (rcvPkt.getSeqNum() == rcvBase) {

                    //send the data to applicationLayer via sim function
                    System.out.println("RECEIVER: Packet "+rcvPkt.getSeqNum()+" received, No problem found sending to Application layer.");
                    simulator.sendToApplicationLayer(this,rcvPkt.getData());

                    //Add this sequence number to list of delivered sequences now that it is added.
                    totalSeqNums.add(rcvPkt.getSeqNum());

                    //Set receiver base to next packet.
                    rcvBase += 1;
                    //If items are in buffer, use function to go through buffered items and check which packets need to be removed.
                    if (!buffer.isEmpty())
                        recheckBuffer();

            //otherwise, put the packet in the buffer
            } else {
                    System.out.println("RECEIVER: Packet "+rcvPkt.getSeqNum()+" received, but is out of order and put on buffer.");
                    buffer.add(pkt);
            }

            //we finished with this packet set the current seqNum to be the prevSeqNum
            prev_SeqNum = rcvPkt.getSeqNum();

            //for acknowledgement packet segment we don't need to pass back the original data so just keep a dummy value
            rdt_send(new byte[1]);

            //Display output of results for all sequence numbers of packets that have been delivered.
            System.out.print("ALL SEQUENCE NUMBERS DELIVERED: ");
            for (int i = 0; i < totalSeqNums.size(); i++) {
                System.out.print(totalSeqNums.get(i) + " ");
            }
            System.out.println();

        }
        System.out.println("______________________________");

    }


    /*
     * corruption uses check sum to check for any error in the packet
     * if error found then return true (it is corruption)
     * if error not found return false (it is !corruption)
     */
    public boolean corruption(){

        System.out.println("{____________________}");
        System.out.println("RECEIVER：Corruption test starts");
        //if the packet is null (not received) then just return true (corrupted )
        if(rcvPkt == null){
            System.out.println("RECEIVER：TEST failed Packet is empty");
            System.out.println("{______________________}");
            return true;
        }
        //extract data from packet
        byte [] rcv_data = rcvPkt.getData();
        System.out.println("RECEIVER：Packet's data received: " + Arrays.toString(rcv_data));

        //get the checksum from the packet used to check for errors
        String rcv_Checksum = rcvPkt.getChecksum();
        System.out.println("RECEIVER：Packet's checksum: " + rcv_Checksum);

        //get new checksum for the dat awe just received
        Checksum checksum = new Checksum(rcv_data);

        String total_data = checksum.createTotal();
        System.out.println("RECEIVER：checksum for Data received: " + total_data);

        //add the two checksum values together (in bits)
        String added_Checksum = checksum.bitAddition(total_data, rcv_Checksum);
        System.out.println("RECEIVER：Total Checksum : " + added_Checksum);

        //checking bit by bit if all bits equal to 1 then no error found return false
        //else if any bit equal 0, then an error exists return true.
        for(int i = 0; i < added_Checksum.length(); i++){

            if(added_Checksum.charAt(i) != '1'){
                System.out.println("RECEIVER：Error occurred at bit position: " + i +" .");
                System.out.println("{____________________}");
                //if any of the bits not equal 1 than return corruption equal ture
                return true;
            }
        }

        System.out.println("{____________________}");
        return false;
    }

    public TransportLayerPacket mk_pkt(int seqNum){

        //use constructor to build new packet
        return new TransportLayerPacket(seqNum,1,new byte[1],"");

    }

    /**
     * Function called when extracting items from buffer list to deliver them to application
     */
    public void recheckBuffer () {
        //System.out.print statements are used to debug output of receiver packet removals. When this starts, a boolean variable will change
        boolean noOutput = true;
        boolean finished = false;
        //Once we know we cannot obtain any more packets, finish looping
        while (!finished) {
            int i = 0;
            //Finish complete loop when no items are delivered in a loop, meaning we know we cannot find a packet with the next expected sequence number
            finished = true;
            //Loop through to end of field once until we have gone through all items. While loop is used for case of items removed in list.
            while (i < buffer.size()) {
                //If this packet is next packet from list, deliver it and remove it from buffer
                if (buffer.get(i).getSeqNum() == rcvBase) {
                    //Output strings of removed packets by their related sequence numbers.
                    if (noOutput) {
                        System.out.print("RECEIVER: buffer packets removed: " + buffer.get(i).getSeqNum());
                        noOutput = false;
                    } else {
                        System.out.print(", " + buffer.get(i).getSeqNum());
                    }

                    //Deliver the buffered packet, add its contents to list of output and then display all output.
                    simulator.sendToApplicationLayer(this,buffer.get(i).getData());
                    totalSeqNums.add(buffer.get(i).getSeqNum());
                    System.out.print("RECEIVER: ALL SEQUENCE NUMBERS DELIVERED: ");
                    for (int j = 0; j < totalSeqNums.size(); j++) {
                        System.out.print(totalSeqNums.get(j) + " ");
                    }
                    System.out.println();

                    //Remove this packet from buffer, shifting all future elements back by 1.
                    buffer.remove(i);
                    //Set receiver base to next packet.
                    rcvBase += 1;
                    //due to possibility of rechecking previous packets, loop again after this loop has finished.
                    finished = false;
                    //otherwise, move onto next element.
                } else {
                    i += 1;
                }
            }
        }
        //If we have output, print to next line.
        if (!noOutput)
            System.out.println();
    }



    @Override
    public void timerInterrupt() {

    }
}
