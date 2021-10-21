public class Receiver extends TransportLayer{

    TransportLayerPacket rcvPkt ;
    Receiver receiver;


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        System.out.println("client: " + getName() + " has been initialised");
        rcvPkt = null;
        receiver = new Receiver("Receiver", simulator);

    }

    @Override
    public void rdt_send(byte[] data) {

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        rcvPkt = new TransportLayerPacket(pkt);
        if(!corruption()){
            System.out.println("received pkt");
        }





    }

    public boolean corruption(){

        /*if(rcvPkt != null){
            return false;
        }*/
        byte [] rcv_data = rcvPkt.getData();
        int rcv_Checksum = rcvPkt.getChecksum();
        int rcv_seqNum = rcvPkt.getSeqNum();

        Checksum checksum = new Checksum();

        int proof_Checksum; // = generateChecksum(rcv_data);
        String added_Checksum = "1000000100101101"; // = checksumAddition(proof_Checksum , rcv_Checksum);

        for(int i = 0; i < added_Checksum.length(); i++){

            if(added_Checksum.charAt(i) != 1){
                return true;
            }
        }
        return false;
    }

    @Override
    public void timerInterrupt() {

    }
}
