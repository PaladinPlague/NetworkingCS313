public class Sender extends TransportLayer {


    TransportLayerPacket sndPkt;
    TransportLayerPacket rcvPkt;
    Sender sender;



    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        System.out.println("client: " + getName() + " has been initialised");
        sndPkt = new TransportLayerPacket();
        rcvPkt  = new TransportLayerPacket();
        sender = new Sender("sender", simulator);

    }

    @Override
    public void rdt_send(byte[] data) {
        sndPkt = make_pkt(0, data);
        System.out.println(data.toString());

        simulator.sendToNetworkLayer(sender,sndPkt);
        simulator.startTimer(sender,1);
    }

    public TransportLayerPacket make_pkt( int seqNum, byte[] data){
        TransportLayerPacket pkt = new TransportLayerPacket();
        pkt.setSeqnum(seqNum);
        pkt.setData(data);
        return pkt;
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        rcvPkt = new TransportLayerPacket(pkt);
        if (corrupt() || !isACK()){
            //if pkt is corrupted or the ACK num is not the right one then
            //wait until timer runs out
            System.out.println("pkt not right or ACK num not right");
        }else if(!corrupt() && isACK()){
            //if everything is fine then stop timer waiting to be called from above

            System.out.println("pkt is right or ACK num is right");
            timerInterrupt();
        }
    }

    public boolean corrupt(){
        if (rcvPkt != null){
            //check if the packet is corrupted
            //if is corrupted then return false else return true

            return true;
        }else{
            return false;
        }
    }

    public boolean isACK(){
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
