import java.util.Arrays;

public class Sender extends TransportLayer {


    TransportLayerPacket sndPkt;
    //TransportLayerPacket rcvPkt;
    Sender sender;



    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        //initialise all variable
        System.out.println("SENDER: " + getName() + " has been initialised");
        sndPkt = null;
        sender = new Sender("sender", simulator);

    }

    @Override
    public void rdt_send(byte[] data) {

        System.out.println();
        System.out.println("______________________________");
        System.out.print("SENDER: The data we got: ");
        System.out.println(Arrays.toString(data)); //to check what data we have been passed


        System.out.println("SENDER: making packet for data we got");
        sndPkt = mk_pkt(0, data); //make the packet using mk_pkt()


        System.out.println("SENDER: packet sent to Network layer");
        simulator.sendToNetworkLayer(sender,sndPkt); //call sim function to perform udt_send() send to NetworkLayer

        System.out.println("SENDER: timer started");
        simulator.startTimer(sender,1); //call sim function start the timer (timer kept time taken between send a packet and receives ACK)
        System.out.println("______________________________");
        System.out.println();

    }

    public TransportLayerPacket mk_pkt( int seqNum, byte[] data){

        Checksum checksum = new Checksum(data);//initialise Checksum passing data into Checksum
        String checksumValue = checksum.createCheckSum(); //generate checksum

        //use constructor to build new packet
        TransportLayerPacket pkt = new TransportLayerPacket(seqNum,0,data,checksumValue);
        return pkt;
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        TransportLayerPacket rcvPkt = new TransportLayerPacket(pkt);
        System.out.println("______________________________");
        System.out.println("SENDER: receiving ACK packet");
        System.out.println("SENDER: checking received ACK packet");
        if (corruption(rcvPkt) || !isACK(rcvPkt)){
            //if pkt is corrupted or the ACK num is not the right one then
            //wait until timer runs out
            System.out.println("SENDER: ACK packet received is corrupted or not ACK, waiting for time out.");
            timerInterrupt();
            System.out.println("SENDER: timer stopped, time out!");


        }else if(!corruption(rcvPkt) && isACK(rcvPkt)){
            //if everything is fine then stop timer waiting to be called from above

            System.out.println("SENDER: ACKed");
            timerInterrupt();
        }
        System.out.println("______________________________");
        System.out.println();

    }

    public boolean corruption (TransportLayerPacket rcvPkt){
        //check if packet received is null return true (packet is corrupted) if packet is empty
        if (rcvPkt == null){
            return true;
        }

        byte [] rcv_Data =  rcvPkt.getData(); //extract data from the packet
        //get a new checksum for the data extracted
        Checksum checksum = new Checksum (rcv_Data);
        String proofChecksum = checksum.createCheckSum();

        //do binary addition to get a total value of two check sum (which should be all ones)
        String addedChecksum =  checksum.bitAddition(proofChecksum, rcvPkt.getChecksum());

        //loop through all bits to check if all of them equal 1
        for (int i  = 0; i < addedChecksum.length(); i++){
            //if any of the bits not equal 1 then error exists corruption return true
            if (addedChecksum.charAt(i) != 1){
                return true;
            }
        }
        //else everything is correct then corruption equal false.
        return false;
    }

    public boolean isACK(TransportLayerPacket rcvPkt){
        if (rcvPkt != null){
            //check if the ACK is the correct ACK
            // when we wait for ACK 0, and we got ACK 1 then we know it is not the right one
            //if we received the correct ACK num then return true else false

            return rcvPkt.getAckNum() == 1;
        }
        return false;
    }

    @Override
    public void timerInterrupt() {
        // stop timer

        simulator.stopTimer(sender);

        //resend the pkt???
        //start timer???

    }
}
