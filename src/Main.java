public class Main {

    public static void main(String[] args) {
        NetworkSimulator sim = new NetworkSimulator(10, 0.0, 0.0, 10.0, false, 3);

        sim.printEventQueue();

        // TODO: Set the sender   (sim.setSender)
        Sender sender = new Sender("sender",sim);
        sim.setSender(sender);

        // TODO: Set the receiver (sim.setReceiver)
        Receiver receiver = new Receiver("receiver",sim);
        sim.setReceiver(receiver);

        sim.runSimulation();
        byte[] a = {106,116,121,56};
        Checksum test = new Checksum(a);
        System.out.println(test.createCheckSum());
    }

}