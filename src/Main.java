public class Main {

    public static void main(String[] args) {

        // TODO: Ask how does loss and corruption probability works.
        //  when test for packet loss scenarios even with lossProb being set to 1.0 it still goes to an infinite loop of resend and then loss
        NetworkSimulator sim = new NetworkSimulator(2, 0.0, 50.0, 10.0, false, 4);


        // TODO: Set the sender   (sim.setSender)
        Sender sender = new Sender("sender",sim);
        sim.setSender(sender);

        // TODO: Set the receiver (sim.setReceiver)
        Receiver receiver = new Receiver("receiver",sim);
        sim.setReceiver(receiver);

        sim.runSimulation();

    }

}
