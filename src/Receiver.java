public class Receiver extends TransportLayer{

    TransportLayerPacket rcvPkt ;
    Receiver receiver;

    int prev_SeqNUm;


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        System.out.println("client: " + getName() + " has been initialised");
        rcvPkt = null;
        receiver = new Receiver("Receiver", simulator);
        prev_SeqNUm = -99999;

    }

    @Override
    public void rdt_send(byte[] data) {
        System.out.println("sending ACK to sender for packet with seqNum of " + rcvPkt.getSeqNum());
        TransportLayerPacket sndPkt = mk_pkt(rcvPkt.getSeqNum());
        simulator.sendToNetworkLayer(receiver,sndPkt);
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        rcvPkt = new TransportLayerPacket(pkt);

        if(prev_SeqNUm == rcvPkt.getSeqNum()){
            //duplicate package just ignore
            System.out.println("Duplicated package ignored");
            prev_SeqNUm = rcvPkt.getSeqNum();

        }else if(!corruption()){
            System.out.println("received pkt but problem found, waiting for sender to resend.");
        }else{

            System.out.println("Packet received, No problem found sending to Application layer.");
            simulator.sendToApplicationLayer(receiver,rcvPkt.getData());
            rdt_send(rcvPkt.getData());
        }

    }

    public boolean corruption(){

        if(rcvPkt == null){
            return true;
        }
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

    public TransportLayerPacket mk_pkt(int seq){
        TransportLayerPacket pkt = new TransportLayerPacket(seq,1,null,-9999);
        return pkt;
    }



    @Override
    public void timerInterrupt() {

    }
}
