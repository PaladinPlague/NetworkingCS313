import java.util.Arrays;

public class Receiver extends TransportLayer{

    TransportLayerPacket rcvPkt;//the received packet segment we are current processing
    int prev_SeqNum; //the last seq number we have processed

    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init(){

        //initialise all variable
        System.out.println("RECEIVER: " + getName() + " has been initialised");
        rcvPkt = null;//starting with null segment
        //since the first packet we will receive is also pkt 0 then it is safe to assume the prev_SeqNum is 1
        prev_SeqNum = 1;

    }

    @Override
    public void rdt_send(byte[] data) {

        System.out.println("______________________________");
        System.out.println("RECEIVER: sending ACK to sender for packet with seqNum of " + rcvPkt.getSeqNum());

        //if everything is being checked and being process make a pkt to send ACK back to sender
        TransportLayerPacket sendingPkt = mk_pkt(rcvPkt.getSeqNum());

        //call sim function to perform udt_send() send to networkLayer
        // This line is sending daa to Network stimulator with this data
        simulator.sendToNetworkLayer(this,sendingPkt);
        prev_SeqNum = rcvPkt.getSeqNum();
        System.out.println("______________________________");
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        System.out.println("______________________________");
        //get the received packet and turn into a usable packet
        rcvPkt = new TransportLayerPacket(pkt);

        //check is the packet payload has been corrupted
        if(corruption()){
            System.out.println("RECEIVER: Received the packet "+rcvPkt.getSeqNum()+".");
            //if it corrupted, ignore the current packet, waiting for timeout on sender side and be prepared to receive the next incoming packet
            System.out.println("RECEIVER: Received pkt but problem found, waiting for sender to resend.");


        }else{
            // if no problem found during error check, extract data from the packet
            System.out.println("RECEIVER: Packet received, No problem found sending to Application layer.");
            //send the data to applicationLayer via sim function
            simulator.sendToApplicationLayer(this,rcvPkt.getData());
            //ready to receive the next packet.

            //we finished with this packet set the current seqNum to be the prevSeqNum
            prev_SeqNum = rcvPkt.getSeqNum();

            //for acknowledgement packet segment we don't need to pass back the original data so just keep a dummy value
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



        return false;
    }

    public TransportLayerPacket mk_pkt(int seqNum){

        //use constructor to build new packet
        return new TransportLayerPacket(seqNum,1,new byte[1],"");

    }

    @Override
    public void timerInterrupt() {

    }
}
