import java.util.concurrent.TransferQueue;

public class Receiver extends TransportLayer{

    TransportLayerPacket rcvPkt;
    TransportLayerPacket sndPkt;



    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {
        System.out.println("client: " + getName() + " has been initialised");
        rcvPkt =null;
        sndPkt =null;
    }

    @Override
    public void rdt_send(byte[] data) {

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        rcvPkt =  new TransportLayerPacket(pkt);
        if (rcvPkt == null){
            System.out.println("receiver error: pkt received contains nothing");
        }else{
            System.out.println("receiver received pkt need to check if it is the right one and is it corrupted");
        }

    }

    public boolean corrupt(){
        return true;

    }

    public TransportLayerPacket make_pkt(int seq){
        TransportLayerPacket sndPkt = new TransportLayerPacket();

        return sndPkt;
    }

    @Override
    public void timerInterrupt() {

    }
}
