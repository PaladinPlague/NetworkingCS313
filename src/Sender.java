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

        System.out.println("SENDER: " + getName() + " has been initialised");
        sndPkt = null;
        //rcvPkt  = null;
        sender = new Sender("sender", simulator);

    }

    @Override
    public void rdt_send(byte[] data) {
        // I THINK THE SENDER WORKS!
        System.out.println();
        System.out.println("______________________________");
        System.out.print("SENDER: The data we got: ");
        System.out.println(Arrays.toString(data));


        System.out.println("SENDER: making packet for data we got");
        sndPkt = mk_pkt(0, data);


        System.out.println("SENDER: packet sent to Network layer");
        simulator.sendToNetworkLayer(sender,sndPkt);

        System.out.println("SENDER: timer started");
        simulator.startTimer(sender,100);
        System.out.println("______________________________");
        System.out.println();

    }

    public TransportLayerPacket mk_pkt( int seqNum, byte[] data){

        Checksum checksum = new Checksum(data);
        String checksumValue = checksum.createCheckSum();
        TransportLayerPacket pkt = new TransportLayerPacket(seqNum,0,data,checksumValue);
        return pkt;
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        TransportLayerPacket rcvPkt = new TransportLayerPacket(pkt);
        System.out.println("______________________________");
        System.out.println("SENDER: receiving ACK packet");
        System.out.println("SENDER: checking received ACK packet");
        if (corrupt(rcvPkt) || !isACK(rcvPkt)){
            //if pkt is corrupted or the ACK num is not the right one then
            //wait until timer runs out
            System.out.println("SENDER: ACK packet received is corrupted or not ACK, waiting for time out.");

            System.out.println("SENDER: timer stopped, time out!");
            simulator.stopTimer(sender);


        }else if(!corrupt(rcvPkt) && isACK(rcvPkt)){
            //if everything is fine then stop timer waiting to be called from above

            System.out.println("SENDER: ACKed");
            //timerInterrupt();
        }
        System.out.println("______________________________");
        System.out.println();

    }

    public boolean corrupt(TransportLayerPacket rcvPkt){
        if (rcvPkt != null){
            //check if the packet is corrupted
            //if is corrupted then return false else return true

            return true;
        }else{
            return false;
        }
    }

    public boolean isACK(TransportLayerPacket rcvPkt){
        if (rcvPkt != null){
            //check if the ACK is the correct ACK
            // when we wait for ACK 0, and we got ACK 1 then we know it is not the right one
            //if we received the correct ACK num then return true else false

            if(rcvPkt.getAckNum() == 1){
                return true;
            }
        }
        return false;
    }

    @Override
    public void timerInterrupt() {

        System.out.println("something went wrong");
        //sndPkt = mk_pkt(0, data);

    }
}
