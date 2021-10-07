public class Main {

    public static void main(String[] args) {
        NetworkSimulator sim = new NetworkSimulator(10, 0.0, 0.0, 10.0, false, 1);

        // TODO: Set the sender   (sim.setSender)

        TransportLayer sender = new TransportLayer("sender",sim) {
            @Override
            public void init() {

            }

            @Override
            public void rdt_send(byte[] data) {

            }

            @Override
            public void rdt_receive(TransportLayerPacket pkt) {

            }

            @Override
            public void timerInterrupt() {

            }
        };
        sim.setSender(sender);

        // TODO: Set the receiver (sim.setReceiver)
        TransportLayer receiver = new TransportLayer("receiver", sim) {
            @Override
            public void init() {

            }

            @Override
            public void rdt_send(byte[] data) {

            }

            @Override
            public void rdt_receive(TransportLayerPacket pkt) {

            }

            @Override
            public void timerInterrupt() {

            }
        };
        sim.setReceiver(receiver);

        sim.runSimulation();
    }

}
