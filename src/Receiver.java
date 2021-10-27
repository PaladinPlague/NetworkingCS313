public class Receiver extends TransportLayer{

    TransportLayerPacket rcvPkt ;
    Receiver receiver;

    int prev_SeqNUm;


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        System.out.println("RECEIVER: " + getName() + " has been initialised");
        rcvPkt = null;
        receiver = new Receiver("Receiver", simulator);
        prev_SeqNUm = -99999;

    }

    @Override
    public void rdt_send(byte[] data) {

        System.out.println("Hello, do i exist?");
        System.out.println("RECEIVER: sending ACK to sender for packet with seqNum of " + rcvPkt.getSeqNum());
        TransportLayerPacket sndPkt = mk_pkt(rcvPkt.getSeqNum());
        simulator.sendToNetworkLayer(receiver,sndPkt);
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {




        System.out.println("Hello, do i exist?");

        rcvPkt = new TransportLayerPacket(pkt);

        if(prev_SeqNUm == rcvPkt.getSeqNum()){
            //duplicate package just ignore
            System.out.println("Duplicated packet "+ rcvPkt.getSeqNum() +" received. ignored!");
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
        String rcv_Checksum = rcvPkt.getChecksum();

        Checksum checksum = new Checksum(rcv_data);

        String proof_Checksum = checksum.createCheckSum();
        String added_Checksum = checksum.bitAddition(proof_Checksum, rcv_Checksum);

        for(int i = 0; i < added_Checksum.length(); i++){

            if(added_Checksum.charAt(i) != 1){
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

        System.out.println("WHATS UP BRO");

    }
}
