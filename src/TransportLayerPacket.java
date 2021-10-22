public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqnum;
    private int acknum;

    byte[] data;
    int headerLength;
    int tailLength;

    // You may need extra methods

    public TransportLayerPacket(int length) {
        seqnum = 0;
        acknum = 0;
        //Values used as placeholders
        data = new byte[length];
        headerLength = 30;
        tailLength = 30;
    }

    public TransportLayerPacket(TransportLayerPacket pkt) {
            if (pkt == null) {
                seqnum = 0;
                acknum = 0;
                //Values used as placeholders
                data = new byte[32];
                headerLength = 30;
                tailLength = 30;
            } else {
                this.seqnum = pkt.getSeqnum();
                this.acknum = pkt.getAcknum();
                this.data = pkt.getData();
            }
    }

    public void makePkt(int seqNum, byte[] data){
        seqnum = seqNum;
        this.data = data;

    }

    public void setSeqnum(int seqnum) {
        this.seqnum = seqnum;
    }

    public void setAcknum(int acknum) {
        this.acknum = acknum;
    }

    public int getSeqnum() {
        return this.seqnum;
    }

    public int getAcknum() {
        return this.acknum;
    }

    public byte[] getData() {
        return data;
    }

    public int getByteLength() {
        return data.length;
    }

    public int getHeaderLength() {
        return this.headerLength;
    }

    public int getTailLength() {
        return this.tailLength;
    }

    public byte[] getHeader() {
        byte[] result = new byte[this.headerLength];
        for (int i = 0; i < this.headerLength; i++) {
            result[i] = data[i];
        }
        return result;
    }

    public byte[] getTail() {
        byte[] result = new byte[this.tailLength];
        for (int i = 0; i < this.tailLength; i++) {
            result[i] = data[data.length - (i - 1)];
        }
        return result;
    }

}
