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

        System.out.println("client: " + getName() + " has been initialised");
        sndPkt = null;
        //rcvPkt  = null;
        sender = new Sender("sender", simulator);

    }

    @Override
    public void rdt_send(byte[] data) {
        sndPkt = make_pkt(0, data);
        System.out.println(Arrays.toString(data));

        simulator.sendToNetworkLayer(sender,sndPkt);
        simulator.startTimer(sender,1);
    }

    public TransportLayerPacket make_pkt( int seqNum, byte[] data){

        Checksum checksum = new Checksum();

        TransportLayerPacket pkt = new TransportLayerPacket(seqNum,-999999,data,-9999);
        return pkt;
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        TransportLayerPacket rcvPkt = new TransportLayerPacket(pkt);
        if (corrupt(rcvPkt) || !isACK(rcvPkt)){
            //if pkt is corrupted or the ACK num is not the right one then
            //wait until timer runs out
            System.out.println("resend");
        }else if(!corrupt(rcvPkt) && isACK(rcvPkt)){
            //if everything is fine then stop timer waiting to be called from above

            System.out.println("ACKed");
            timerInterrupt();
        }
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

            return true;
        }else {
            return false;
        }
    }

    @Override
    public void timerInterrupt() {
        // stop timer

        simulator.stopTimer(sender);
        //resend the pkt???
        //start timer???

    }
}
