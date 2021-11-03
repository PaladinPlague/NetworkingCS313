public class Main {

    public static void main(String[] args) {

        //Change number of messages based on how many packets you want to send
        //lossProb is between 0 and 1
        //NOTE: corruptProb is between 0 and 100
        //Don't need to change lambda and bidirectional
        NetworkSimulator sim = new NetworkSimulator(20, 0.0, 0.2, 10.0, false, 4);


        // TODO: Set the sender   (sim.setSender)
        Sender sender = new Sender("sender",sim);
        sim.setSender(sender);

        // TODO: Set the receiver (sim.setReceiver)
        Receiver receiver = new Receiver("receiver",sim);
        sim.setReceiver(receiver);

        sim.runSimulation();

    }

}
