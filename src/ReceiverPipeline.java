import java.util.Arrays;
import java.util.ArrayList;

public class ReceiverPipeline extends TransportLayer{

    TransportLayerPacket rcvPkt ;
    int prev_SeqNUm;
    int ackNum;

    //Holds the size of the window
    int windowSize;

    //Holds the current base of the receiving window
    int rcvBase;

    //Holds a list of packets that are currently buffered.
    ArrayList<TransportLayerPacket> buffer;

    public ReceiverPipeline(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init(){

        //initialise all variable

        System.out.println("RECEIVER: " + getName() + " has been initialised");
        rcvPkt = null;
        //Initialise buffered packets as new list of packets.
        buffer = new ArrayList<>();
        //PLACEHOLDER: unsure about length of window size.
        windowSize = 10;
        //First recieve base packet is at index 0.
        rcvBase = 0;
        //receiver = new Receiver("Receiver", simulator);
        prev_SeqNUm = -9999; //keep track of the seq num we received

    }

    @Override
    public void rdt_send(byte[] data) {
        System.out.println("______________________________");
        System.out.println("RECEIVER: sending ACK to sender for packet with seqNum of " + rcvPkt.getSeqNum());

        //if everything is being checked and being process make a pkt to send ACK back to sender
        TransportLayerPacket sendingPkt = mk_pkt(rcvPkt.getSeqNum());
        //call sim function to perform udt_send() send to networkLayer
        simulator.sendToNetworkLayer(this,sendingPkt); // This line is sending daa to Network stimulator with this data
        System.out.println("______________________________");
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        System.out.println("______________________________");
        //get the received packet and turn into a usable packet
        rcvPkt = new TransportLayerPacket(pkt);

        //check if we have received the same packet twice
        if(prev_SeqNUm == rcvPkt.getSeqNum()){

            //duplicate package just ignore
            System.out.println("RECEIVER: Duplicated packet "+ rcvPkt.getSeqNum() +" received. ignored!");
            prev_SeqNUm = rcvPkt.getSeqNum();

            //else check if the packet is corrupt
        }else if(corruption()){
            System.out.println("RECEIVER: Received the packet "+rcvPkt.getSeqNum()+".");
            //if it corrupted, ignore the current packet, waiting for timeout on sender side and be prepared to receive the next incoming packet
            System.out.println("RECEIVER: Received pkt but problem found, waiting for sender to resend.");
            ackNum = 0;
            rdt_send(new byte[1]);

        }else{
            //if this is the next packet we expect, send it
            if (rcvPkt.getSeqNum() == rcvBase) {

                //send the data to applicationLayer via sim function
                System.out.println("RECEIVER: Packet "+rcvPkt.getSeqNum()+" received, No problem found sending to Application layer.");
                simulator.sendToApplicationLayer(this,rcvPkt.getData());
                //Set receiver base to next packet.
                rcvBase += 1;
                //If items are in buffer, use function to go through buffered items and check which packets need to be removed.
                if (!buffer.isEmpty())
                    recheckBuffer();

            //otherwise, put the packet in the buffer
            } else {
                // if no problem found during error check, extract data from the packet
                System.out.println("RECEIVER: Packet "+rcvPkt.getSeqNum()+" received, but is out of order and put on buffer.");
                buffer.add(pkt);
            }

            //ready to receive the next packet.
            ackNum = 1;
            rdt_send(new byte[1]);
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
        return new TransportLayerPacket(seqNum,ackNum,new byte[1],"");

    }


    /**
     * Function called when extracting items from buffer list to deliver them to application
     */
    public void recheckBuffer () {
        //System.out.print statements are used to debug output of receiver packet removals. When this starts, a boolean variable will change
        boolean noOutput = true;
        System.out.print("RECEIVER: packets that have been removed from buffer are: ");
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
                    } else {
                        System.out.print(", " + buffer.get(i).getSeqNum());
                    }
                    simulator.sendToApplicationLayer(this,buffer.get(i).getData());
                    buffer.remove(i);
                    //Set receiver base to next packet.
                    rcvBase += 1;
                    //due to possibilty of rechecking previous packets, loop again after this loop has finished.
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
