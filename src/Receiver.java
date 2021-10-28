public class Receiver extends TransportLayer{

    TransportLayerPacket rcvPkt ;
    Receiver receiver;

    int prev_SeqNUm;


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        //initialise all variable
        System.out.println("RECEIVER: " + getName() + " has been initialised");
        rcvPkt = null;
        receiver = new Receiver("Receiver", simulator);
        prev_SeqNUm = -99999; //keep track of the seq num we received

    }

    @Override
    public void rdt_send(byte[] data) {
        System.out.println("RECEIVER: sending ACK to sender for packet with seqNum of " + rcvPkt.getSeqNum());

        //if everything is being checked and being process make a pkt to send ACK back to sender
        TransportLayerPacket sndPkt = mk_pkt(rcvPkt.getSeqNum());
        //call sim function to perform udt_send() send to networkLayer
        simulator.sendToNetworkLayer(receiver,sndPkt);
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        //get the received packet and turn into an usable packet
        rcvPkt = new TransportLayerPacket(pkt);

        //check if we have received the same packet twice
        if(prev_SeqNUm == rcvPkt.getSeqNum()){

            //duplicate package just ignore
            System.out.println("Duplicated packet "+ rcvPkt.getSeqNum() +" received. ignored!");
            prev_SeqNUm = rcvPkt.getSeqNum();

            //else check if the packet is corrupt
        }else if(corruption()){
            //if it corrupted, ignore the current packet, waiting for timeout on sender side and be prepared to receive the next incoming packet
            System.out.println("received pkt but problem found, waiting for sender to resend.");
        }else{
            // if no problem found during error check, extract data from the packet
            System.out.println("Packet received, No problem found sending to Application layer.");
            //send the data to applicationLayer via sim function
            simulator.sendToApplicationLayer(receiver,rcvPkt.getData());
            //ready to receive the next packet.
            rdt_send(rcvPkt.getData());
        }

    }


    /*
     * corruption uses check sum to check for any error in the packet
     * if error found then return true (it is corruption)
     * if error not found return false (it is !corruption)
     */
    public boolean corruption(){

        //if the packet is null (not received) then just return true (corrupted )
        if(rcvPkt == null){
            return true;
        }
        //extract data from packet
        byte [] rcv_data = rcvPkt.getData();
        //get the checksum from the packet used to check for errors
        String rcv_Checksum = rcvPkt.getChecksum();

        //get new checksum for the dat awe just received
        Checksum checksum = new Checksum(rcv_data);

        String proof_Checksum = checksum.createCheckSum();

        //add the two checksum values together (in bits)
        String added_Checksum = checksum.bitAddition(proof_Checksum, rcv_Checksum);

        //checking bit by bit if all bits equal to 1 then no error found return false
        //else if any bit equal 0, then an error exists return true.
        for(int i = 0; i < added_Checksum.length(); i++){

            if(added_Checksum.charAt(i) != 1){
                //if any of the bits not equal 1 than return corruption equal ture
                return true;
            }
        }
        return false;
    }

    public TransportLayerPacket mk_pkt(int seq){


        TransportLayerPacket pkt = new TransportLayerPacket(seq,1,null,"");

        return pkt;
    }



    @Override
    public void timerInterrupt() {

    }
}
