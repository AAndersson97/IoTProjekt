package network;
import static network.Constants.Network.FIRST_ADDRESS;
import static network.Constants.Node.ADDRESS_LENGTH;
import static network.Constants.Node.MAX_NODES;

public class AddressGenerator {
    private static short[][] addresses;
    private static int numOfAddresses;

    private AddressGenerator() {
    }

    /**
     * Varje router inom samma area har i verkligheten en egen submask dock är denna information ej nödvändig att bevara
     * i detta fall
     * @return En array med short-variabler där varje variabel representerar en del av den fullständiga IPv4-address
     */
    public static short[] generateAddress() {
        if (numOfAddresses == 0) {
            addresses = new short[MAX_NODES][ADDRESS_LENGTH];
            addresses[numOfAddresses++] = FIRST_ADDRESS;
            return FIRST_ADDRESS;
        }
        short[] address = new short[ADDRESS_LENGTH];
        address[0] = addresses[0][0];
        // Om submask är 8 eller lägre ska denna del av adressen varieras, annars inte
        address[1] = addresses[0][1];
        address[2] = addresses[0][2];
        address[3] = (short) (addresses[numOfAddresses-1][3]+1);
        addresses[numOfAddresses++] = address;
        return address;
    }

    // Addressen sparas som array med bytes, behövs konverteras innan kontroll kan ske
    /*private void checkAddress(byte[] address) {
        if (submask >= 24 && address[3] > 255)
            throw new IllegalStateException("The fourth part of the address must not exceed 255 when the submask is of number 24 or higher");
        else if (submask <= 16 && address[3] >= 255) {
            address[3] = 0;
            address[2]++;
        }
        else if (submask <= 8 && address[2] >= 255) {
            address[2] = 0;
            address[1]++;
        }
    }*/


}
