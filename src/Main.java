public class Main {

    public static void main(String[] args) {
        NetworkSimulator sim = new NetworkSimulator(1, 0.0, 100.0, 10.0, false, 4);


        // TODO: Set the sender   (sim.setSender)
        Sender sender = new Sender("sender",sim);
        sim.setSender(sender);

        // TODO: Set the receiver (sim.setReceiver)
        Receiver receiver = new Receiver("receiver",sim);
        sim.setReceiver(receiver);

        sim.runSimulation();
    }

}
