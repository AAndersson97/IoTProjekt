package network;
import static network.Constants.Node.ADDRESS_LENGTH;

public class AddressGenerator {
    private short[] highestAddress;
    private final short[] firstAddress;
    private final int submask;

    /**
     *
     * @param firstAddress Första nodens/routerns adress
     * @param submask Ska vara 8, 16 eller 24.
     */
    AddressGenerator(short[] firstAddress, int submask) {
        this.firstAddress = firstAddress;
        this.submask = submask;
    }

    /**
     * Varje router inom samma area har i verkligheten en egen submask dock är denna information ej nödvändig att bevara
     * i detta fall
     * @return En array med short-variabler där varje variabel representerar en del av den fullständiga IPv4-address
     */
    public short[] generateAddress() {
        short[] address = new short[ADDRESS_LENGTH];
        address[0] = firstAddress[0];
        // Om submask är 8 eller lägre ska denna del av adressen varieras, annars inte
        address[1] = 0;
        address[2] = 0;
        address[3] = (short) (highestAddress == null ? 0 : highestAddress[3] + 1);
        highestAddress = address;
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
