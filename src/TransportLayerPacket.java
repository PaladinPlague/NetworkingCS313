public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqNum;
    private int ackNum;

    byte[] data;

    // You may need extra methods

    public TransportLayerPacket(TransportLayerPacket pkt) {
        // complete this method
        this.seqNum = pkt.seqNum;
        this.ackNum = pkt.ackNum;
        this.data = pkt.getData();
    }

    public TransportLayerPacket(){

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

    public byte[] getData() {
        return data;
    }

}
