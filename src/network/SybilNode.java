package network;

public class SybilNode extends Node {

    private AttackNode master;
    private final Boolean isActive;

    public SybilNode(AttackNode node) {
        super();
        master = node;
        isActive = Boolean.TRUE;
    }

    @Override
    public void run() {
        synchronized (isActive) {
            try {
                isActive.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void receivePacket(Packet packet) {
        master.receivePacket(packet);
    }

    @Override
    public void turnOff() {
        synchronized (isActive) {
            isActive.notifyAll();
        }
    }
}
