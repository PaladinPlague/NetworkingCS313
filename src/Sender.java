import java.util.ArrayList;
import java.util.Arrays;

public class Sender extends TransportLayer {


    TransportLayerPacket sndPkt;
    TransportLayerPacket rcvPkt;
    int seqNumSent;
    int seqNumSending;
    static byte[] sendingData;
    ArrayList<byte[]> data = new ArrayList();


    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

        //initialise all variable
        System.out.println("SENDER: " + getName() + " has been initialised");
        sndPkt = null;
        rcvPkt = null;
        sendingData = null;
        seqNumSent = -9999;
        seqNumSending = 0;

    }

    @Override
    public void rdt_send(byte[] data) {

        System.out.println();
        System.out.println("______________________________");
        System.out.print("SENDER: The data we got: ");
        System.out.println(Arrays.toString(data)); //to check what data we have been passed

        sendingData = data;
        System.out.println("SENDER: making packet "+seqNumSending+" for data we got");
        sndPkt = mk_pkt(seqNumSending, data); //make the packet using mk_pkt()

        System.out.println("SENDER: packet "+sndPkt.getSeqNum()+" sent to Network layer");
        simulator.sendToNetworkLayer(this,sndPkt); //call sim function to perform udt_send() send to NetworkLayer

        System.out.println("SENDER: timer started");
        simulator.startTimer(this,10); //call sim function start the timer (timer kept time taken between send a packet and receives ACK)
        System.out.println("______________________________");
        System.out.println();




    }

    public TransportLayerPacket mk_pkt( int seqNum, byte[] data){

        Checksum checksum = new Checksum(data);//initialise Checksum passing data into Checksum
        String checksumValue = checksum.createCheckSum(); //generate checksum

        //use constructor to build new packet
        return new TransportLayerPacket(seqNum,0,data,checksumValue);

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

        rcvPkt = new TransportLayerPacket(pkt);
        System.out.println("______________________________");
        System.out.println("SENDER: receiving ACK packet");
        System.out.println("SENDER: checking received ACK packet");
        if (corruption(rcvPkt) || !isACK(rcvPkt)){
            //if pkt is corrupted or the ACK num is not the right one then
            //wait until timer runs out
            System.out.println("SENDER: ACK packet received is corrupted or not ACK, waiting for time out.");
            simulator.stopTimer(this);
            System.out.println("SENDER: timer stopped, time out!");
            timerInterrupt();



        }else{
            //if everything is fine then stop timer waiting to be called from above

            System.out.println("SENDER: ACKed");
            simulator.stopTimer(this);
        }
        System.out.println("______________________________");
        System.out.println();

    }

    public boolean corruption (TransportLayerPacket rcvPkt){

        System.out.println("{--------CORRUPTION----------}");
        System.out.println("SENDER：Corruption test starts");
        //if the packet is null (not received) then just return true (corrupted )
        if(rcvPkt == null){
            System.out.println("SENDER：TEST failed Packet is empty");
            System.out.println("{----------CORRUPTION_TEST--------}");
            return true;
        }
        //extract data from packet
        byte [] rcv_data = rcvPkt.getData();
        System.out.println("SENDER：Packet's data received: " + Arrays.toString(rcv_data));

        //get the checksum from the packet used to check for errors
        String rcv_Checksum = rcvPkt.getChecksum();
        System.out.println("SENDER：Packet's checksum: " + rcv_Checksum);

        //get new checksum for the dat awe just received
        Checksum checksum = new Checksum(rcv_data);

        checksum.createCheckSum();
        String total_data = checksum.createTotal();
        System.out.println("SENDER：checksum for Data received: " + total_data);

        //add the two checksum values together (in bits)
        String added_Checksum = checksum.bitAddition(total_data, rcv_Checksum);
        System.out.println("SENDER：Total Checksum : " + added_Checksum);



        //checking bit by bit if all bits equal to 1 then no error found return false
        //else if any bit equal 0, then an error exists return true.
        for(int i = 0; i < added_Checksum.length(); i++){

            if(added_Checksum.charAt(i) != '1'){
                System.out.println("SENDER：Error occurred at bit position: " + i +" .");
                System.out.println("{--------CORRUPTION_TEST----------}");
                //if any of the bits not equal 1 than return corruption equal ture
                return true;
            }
        }

        System.out.println("{--------CORRUPTION_TEST----------}");
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
        System.out.println("______________________________");
        System.out.println("SENDER: testing the functionality for Time Interrupt");

        System.out.println("SENDER: The data we are trying to Resend " + Arrays.toString(sendingData));

        rdt_send(sendingData);
        System.out.println("SENDER: Resending the packet");
        System.out.println("______________________________");
    }
}
