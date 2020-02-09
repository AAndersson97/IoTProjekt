package network.utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Klassens syfte är att läsa in logfilen som skapas av klassen LogWriter och aggregera data på ett strukturerat sätt.
 */
public class LogReader {
    private ArrayList<String> addresses;
    private String attackNodeAddr;
    private HashMap<Integer, List<MutableInteger>> intervals;
    private HashMap<String, List<Long>> packetCount;
    private final String trafficFileName = "traffic.csv";
    private final int interval = 10000; // 10 000 millisekunder, 10 sekunder
    private long timeZero; // tidpunkten för första paketet som skickades, en tidpunkt alla andra pakets tidpunkt ska relateras till

    public static void main(String[] args) {
        LogReader logReader = new LogReader();
        System.out.println("Reading file trafficlog.txt...");
        try {
            logReader.readLogFile();
            System.out.println("Creating intervals...");
            logReader.createIntervals();
            System.out.println("Writing to the file " + logReader.trafficFileName + "...");
            logReader.writeTrafficCSVFile();
        } catch (IOException e) {
            System.out.println("Failed to read the file, message: " + e.getMessage());
            return;
        }
        System.out.println("Finished!");
    }

    /**
     * Logfilens format är följande: sendaddr,destaddr,packettype,timestamp.
     */
    public void readLogFile() throws IOException {
        packetCount = new HashMap<>();
        File file = new File("trafficlog.txt");
        if (!file.canRead())
            throw new IOException("The file could not be read");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("trafficlog.txt"))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.length() < 4)
                    continue;
                populateHashMap(currentLine);
            }
            BufferedReader fileReader = new BufferedReader(new FileReader("attackNode.txt"));
            System.out.println("Reading file attacknode.txt...");
            while ((currentLine = fileReader.readLine()) != null)
                attackNodeAddr = currentLine;
            fileReader.close();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void populateHashMap(String str) {
        String[] strings = str.split(",");
        if (strings.length < 2)
            return;
        if (timeZero == 0)
            timeZero = Long.parseLong(strings[strings.length-1]);
        packetCount.putIfAbsent(strings[1], new ArrayList<>());
        packetCount.get(strings[1]).add(Long.parseLong(strings[2]) - timeZero);
    }

    public void writeTrafficCSVFile() {
        try (PrintWriter writer = new PrintWriter(trafficFileName)) {
            for (String addr : addresses) {
                if (addr.equalsIgnoreCase(attackNodeAddr))
                    writer.print(";Attacker Node");
                else
                    writer.print(";" + addr);
            }
            writer.println();
            for (Integer interval : intervals.keySet()) {
                writer.print(interval);
                for (MutableInteger num : intervals.get(interval))
                    writer.print(";" + num);
                writer.println();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createIntervals() {
        intervals = new HashMap<>();
        addresses = new ArrayList<>();
        for (String key : packetCount.keySet()) {
            addresses.add(key);
            for (Long time : packetCount.get(key)) {
                int index = 1;
                for (long i = interval; time > i; i += interval, index++);
                intervals.putIfAbsent(index*interval, new ArrayList<>());
                while (intervals.get(index*interval).size() < addresses.size())
                    intervals.get(index*interval).add(new MutableInteger(0));
                intervals.get(index * interval).get(addresses.size() - 1).increment();
            }
        }
    }

    public static class MutableInteger {
        private int integer;

        public MutableInteger(int integer) {
            this.integer = integer;
        }

        public void increment() {
            integer++;
        }

        public int getInteger() {
            return integer;
        }

        @Override
        public String toString() {
            return integer+"";
        }
    }

}
