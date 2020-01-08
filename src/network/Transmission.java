package network;

public class Transmission {
    private SignalStrength signalStrength;
    private int transmissionRadius;

    Transmission(SignalStrength strength) {
        signalStrength = strength;
        calculateTA();
    }

    private void calculateTA() {
        if (signalStrength == SignalStrength.EXCELLENT)
            transmissionRadius = 200;
        else if (signalStrength == SignalStrength.VERYGOOD)
            transmissionRadius = 100;
        else if (signalStrength == SignalStrength.ACCEPTABLE)
            transmissionRadius = 50;
        else
            transmissionRadius = 25;
    }

    public void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
        calculateTA();
    }

    /**
     * @param transmissionRadius Radien på cirkeln som definierar överföringsområdets omfattning
     * @param sender Sändarens plats i x- och y-koordinat
     * @param receiver Mottagarens plats i x- och y-koordinater
     * @return
     */
    public static boolean isInsideTransmissionArea(int transmissionRadius, Location sender, Location receiver) {
        if (sender == null || receiver == null) {
            System.out.println("Sender is " + (sender == null ? "null" : "not null") + " receiver is: " + (receiver == null ? "null" : "not null"));
            return false;
        }
        double a = Math.pow(Math.abs(receiver.getX()-sender.getX()), 2);
        double b = Math.pow(Math.abs(receiver.getY()-sender.getY()),2);
        if (Constants.LOG_ACTIVE && Math.sqrt(a + b) <= transmissionRadius)
            System.out.println("isInsideTransmissionArea " +  "Sender x: " + sender.getX() + " Sender y: " + sender.getY() + " Receiver x: " + receiver.getX() + " Receiver y: " + receiver.getY());
        return Math.sqrt(a + b) <= transmissionRadius;
    }

    public int transmissionRadius() {
        return transmissionRadius;
    }

    public enum SignalStrength {
        EXCELLENT(-30), VERYGOOD(-67), ACCEPTABLE(-80), WEAK(-100);
        int value;
        SignalStrength(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
}
