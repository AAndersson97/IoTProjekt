package network;

public class Transmission {
    private SignalStrength signalStrength;
    private int transmissionRadius;

    Transmission(SignalStrength strength) {
        signalStrength = strength;
    }

    private void calculateTA() {
        if (signalStrength == SignalStrength.EXCELLENT)
            transmissionRadius = 100;
        else if (signalStrength == SignalStrength.VERYGOOD)
            transmissionRadius = 50;
        else if (signalStrength == SignalStrength.ACCEPTABLE)
            transmissionRadius = 25;
        else
            transmissionRadius = 20;
    }

    public void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
        calculateTA();
    }

    /**
     * @param transmissionRadius Radien på cirkeln som definierar överföringsområdets omfattning
     * @param sender Sändarens plats i x- och y-koordinat
     * @param receiver Mottagarens plats i x- och y-koordinat
     * @return
     */
    public static boolean isInsideTransmissionArea(int transmissionRadius, Location sender, Location receiver) {
        if (Constants.LOG_ACTIVE)
            System.out.println("isInsideTransmissionArea " +  "Sender x: " + sender.getX() + " Sender y: " + sender.getY() + " Receiver x: " + receiver.getX() + " Receiver y: " + receiver.getY());
        double a = Math.pow(Math.abs(receiver.getX()-sender.getX()), 2);
        double b = Math.pow(Math.abs(receiver.getY()-sender.getX()),2);
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
