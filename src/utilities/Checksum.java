package utilities;

public class Checksum {

    public static long calculate(byte[] buffer) {
        int length = buffer.length;
        int iterator = 0;
        long sum = 0;
        long data;

        for (; length > 1; iterator+=2, length-=2) {
            data = (((buffer[iterator] << 8) & 0xFF00) | ((buffer[iterator + 1]) & 0xFF));
            sum += data;

            // Korrigering av första komplementets "carry bit" i 16-bitar (upptäcker tecken-utökning)
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
            }
        }

        // Hantera resterande bytes i buffer med udda längd
        if (length > 0) {
            sum += (buffer[iterator] << 8 & 0xFF00);
            // Korrigering av första komplementets "carry bit" i 16-bitar (upptäcker tecken-utökning)
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum++;
            }
        }
        // Slutligen korrigering av första komplementsvärde till 16 bitar
        sum = ~sum;
        return sum & 0xFFFF;
    }

    public static int generateChecksum(byte[] byteArray) {
        int sum = 0;

        // Om bytearray har udda antal okteter lägg till en nollbyte
        byte[] stream;
        if (byteArray.length % 2 != 0) {
            stream = new byte[byteArray.length+1];
            System.arraycopy(byteArray, 0, stream, 0, byteArray.length);
            stream[byteArray.length] = 0;
        } else {
            stream = new byte[byteArray.length];
            System.arraycopy(byteArray, 0, stream, 0, byteArray.length);
        }

        // Intilligande 8 bitars ord lagras som en short,
        // summera 16 bitars short-variabler och räkna ut första komplementet för kontrollsumma
        for (int c=0; c < stream.length; c=c+2 ) {
            int firstByte = Byte.valueOf(stream[c]).intValue();

            // Konvertera till värde utan tecken
            firstByte = firstByte&255;
            int shifted = (firstByte<<8);
            //int nextbyte = stream[c+1]&255;
            int twoBytesGrouping = (shifted + (stream[c+1]&255));
            sum = sum + twoBytesGrouping;
        }
        // Lägger till överförda bitar till kontrollsumman för att behålla det som ett 16 bitars ord
        while (sum > 65535)
            sum = sum - 65536 + 1;

        // Kalkylera första komplementet av summan
        sum = (~sum&0xFFFF);
    }

}
