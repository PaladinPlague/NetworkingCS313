public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqnum;
    private int acknum;
    private int sourcePort;
    private int destinationPort;

    byte[] data;

    // You may need extra methods

    public TransportLayerPacket(int length) {
        seqnum = 0;
        acknum = 0;
        sourcePort = 0;
        destinationPort = 0;
        data = new byte[length];
    }

    public TransportLayerPacket(TransportLayerPacket pkt) {
            if (pkt == null) {
                seqnum = 0;
                acknum = 0;
                data = new byte[32];
            } else {
                this.seqnum = pkt.getSeqnum();
                this.acknum = pkt.getAcknum();
                this.sourcePort = pkt.getSourcePort();
                this.destinationPort = pkt.getDestinationPort();
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

    public void setSourcePort(int source) {
        this.sourcePort = source;
    }

    public void setDestinationPort(int destination) {
        this.destinationPort = destination;
    }

    public int getSeqnum() {
        return this.seqnum;
    }

    public int getAcknum() {
        return this.acknum;
    }

    public int getSourcePort() {
        return this.sourcePort;
    }

    public int getDestinationPort() {
        return this.destinationPort;
    }

    public byte[] getData() {
        return data;
    }

    public int getByteLength() {
        return data.length;
    }

}
