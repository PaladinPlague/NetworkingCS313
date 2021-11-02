import java.util.Arrays;

public class Receiver extends TransportLayer{

    int prev_SeqNUm;
    int ackNum;


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init(){

        //initialise all variable

        //receiver = new Receiver("Receiver", simulator);
        prev_SeqNUm = -9999;//keep track of the seq num we received
        ackNum = 0;

    }

    public TransportLayerPacket mk_pkt(int seqNum, byte[] data){

        Checksum checksum = new Checksum(data);//initialise Checksum passing data into Checksum
        String checksumValue = checksum.createCheckSum(); //generate checksum

        //use constructor to build new packet
        int oldAck = ackNum;

        return new TransportLayerPacket(seqNum,oldAck,data,checksumValue);

    }

    @Override
    public void rdt_send(byte[] data) {


        TransportLayerPacket sndpkt = mk_pkt(1,data);
        simulator.sendToNetworkLayer(this,sndpkt);
        //simulator.startTimer(this,100);



    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        int ack = pkt.getAckNum();

        if (corruption(pkt) || ack == 1){
            //Do nothing...

        }
        else if(ack == 0){
            rdt_send(pkt.getData());
        }

    }


    /*
    * corruption uses check sum to check for any error in the packet
    * if error found then return true (it is corruption)
    * if error not found return false (it is !corruption)
    */
    public boolean corruption(TransportLayerPacket pkt){

        System.out.println("{____________________}");
        System.out.println("RECEIVER：Corruption test starts");
        //if the packet is null (not received) then just return true (corrupted )
        if(pkt == null){
            System.out.println("RECEIVER：TEST failed Packet is empty");
            System.out.println("{______________________}");
            return true;
        }
        //extract data from packet
        byte [] rcv_data = pkt.getData();
        System.out.println("RECEIVER：Packet's data received: " + Arrays.toString(rcv_data));

        //get the checksum from the packet used to check for errors
        String rcv_Checksum = pkt.getChecksum();
        System.out.println("RECEIVER：Packet's checksum: " + rcv_Checksum);

        //get new checksum for the dat awe just received
        Checksum checksum = new Checksum(rcv_data);

        String total_data = checksum.createTotal();
        System.out.println("RECEIVER：checksum for Data received: " + total_data);

        //add the two checksum values together (in bits)
        String added_Checksum = checksum.bitAddition(total_data, rcv_Checksum);
        System.out.println("RECEIVER：Total Checksum : " + added_Checksum);



        //checking bit by bit if all bits equal to 1 then no error found return false
        //else if any bit equal 0, then an error exists return true.
        for(int i = 0; i < added_Checksum.length(); i++){

            if(added_Checksum.charAt(i) != '1'){
                System.out.println("RECEIVER：Error occurred at bit position: " + i +" .");
                System.out.println("{____________________}");
                //if any of the bits not equal 1 than return corruption equal ture
                return true;
            }
        }

        System.out.println("{____________________}");
        return false;
    }

    public TransportLayerPacket mk_pkt(int seqNum){

        //use constructor to build new packet
        return new TransportLayerPacket(seqNum,ackNum,new byte[1],"");

    }



    @Override
    public void timerInterrupt() {

    }
}
