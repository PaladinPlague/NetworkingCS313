public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqNum;
    private int ackNum;
    private final String checksum;

    byte[] data;

    // You may need extra methods

    public TransportLayerPacket(TransportLayerPacket pkt) {
        // complete this method
        this.seqNum = pkt.seqNum;
        this.ackNum = pkt.ackNum;
        this.data = pkt.getData();
        this.checksum = pkt.checksum;
    }

    public TransportLayerPacket(int seqNum, int ackNum, byte[] data, String checksum){
        this.seqNum = seqNum;
        this.ackNum = ackNum;
        this.data = data;
        this.checksum = checksum;
    }


    public void setSeqnum(int seqNum) {
        this.seqNum = seqNum;
    }

    public int getSeqNum(){ return seqNum; }

    public void setAcknum(int ackNum) {
        this.ackNum = ackNum;
    }

    public int getAckNum(){ return ackNum;}

    public String getChecksum(){
        return checksum;
    }

    public byte[] getData() {
        return data;
    }

}
