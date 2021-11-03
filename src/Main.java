public class Main {

    public static void main(String[] args) {

        NetworkSimulator sim = new NetworkSimulator(2, 0.3, 0.25, 10.0, false, 4);


        Sender sender = new Sender("sender",sim);
        sim.setSender(sender);


        Receiver receiver = new Receiver("receiver",sim);
        sim.setReceiver(receiver);

        sim.runSimulation();

    }

}
