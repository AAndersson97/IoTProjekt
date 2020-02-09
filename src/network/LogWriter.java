package network;

import java.io.*;

public class LogWriter {
    private static LogWriter instance;
    private String[] trafficBuffer;
    private int bufferPointer1;

    LogWriter() {
        File file = new File("log.txt");
        if (file.exists())
            file.delete();
        trafficBuffer = new String[1024];
    }

    public void writeTrafficData(String str) {
        Simulator.scheduleTask(() -> {
            trafficBuffer[bufferPointer1] = str;
            if (++bufferPointer1 == trafficBuffer.length) {
                writeToLog();
                bufferPointer1 = 0;
            }
        });
    }

    public void writeVotingResult(String str) {
        Simulator.scheduleTask(() -> {
            File file;
            if (!(file = new File("votingresult.csv")).exists()) {
                try (PrintWriter printWriter = new PrintWriter(file)){
                    printWriter.println("Voting Number;Votes for;Votes against;Current Node");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(
                    new File("votingresult.csv"), true))) {
                printWriter.println(str);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public void writeToFile(String text, String fileName) {
        try (PrintWriter printWriter = new PrintWriter(new File(fileName))) {
            printWriter.println(text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void writeToLog() {
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(
                new File("trafficlog.txt"), true))) {
            for (int i = 0; i < bufferPointer1; i++)
                printWriter.println(trafficBuffer[i]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static LogWriter getInstance() {
        if (instance == null)
            instance = new LogWriter();
        return instance;
    }

    public static String getPacketLogString(short[] sendAddr, short[] destAddr) {
        StringBuilder send = new StringBuilder(), dest = new StringBuilder();
        for (int i = 0; i < sendAddr.length; i++) {
            dest.append(i != 0 ? "." : "").append(destAddr[i]);
            send.append(i != 0 ? "." : "").append(sendAddr[i]);
        }
        return send + "," + dest + "," + System.currentTimeMillis();
    }
}
