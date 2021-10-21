public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqNum;
    private int ackNum;
    private int checksum;

    byte[] data;

    // You may need extra methods

    public TransportLayerPacket(TransportLayerPacket pkt) {
        // complete this method
        this.seqNum = pkt.seqNum;
        this.ackNum = pkt.ackNum;
        this.data = pkt.getData();
        this.checksum = pkt.checksum;
    }

    public TransportLayerPacket(int seqNum, int ackNum, byte[] data, int checksum){
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

    public void setData (byte[] data){
        this.data = data;
    }

    public int getChecksum(){
        return checksum;
    }

    public byte[] getData() {
        return data;
    }

}
