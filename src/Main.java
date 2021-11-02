public class Main {

    public static void main(String[] args) {
        NetworkSimulator sim = new NetworkSimulator(2, 0.0, 0.0, 10.0, false, 4);


        //TODO: Set the sender   (sim.setSender)
        Sender sender = new Sender("sender",sim);
        sim.setSender(sender);

        byte[] a = {99, 106, 98, 101, 97, 109, 120, 103, 109, 102, 109, 120, 108, 98, 118, 102, 122, 117, 118, 106}; // correct data
        byte[] b = {99, 106, 98, 115, 97, 109, 98, 103, 117, 102, 109, 120, 108, 98, 118, 102, 122, 117, 118, 106}; //invalid correct
        Checksum test = new Checksum(a);
        Checksum test2 = new Checksum(b);


        String total = test.createCheckSum();
        System.out.println(total);
        String c = test2.createTotal();
        System.out.println(c);
        System.out.println(test.bitAddition(total,c));


        // TODO: Set the receiver (sim.setReceiver)
        Receiver receiver = new Receiver("receiver",sim);
        sim.setReceiver(receiver);
        sim.runSimulation();
    }

}
