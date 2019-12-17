package network;

public class AddressGenerator implements Constants {
    private short[] highestAddress;
    private final short[] firstAddress;
    private final int submask;

    /**
     *
     * @param firstAddress Första nodens/routerns adress
     * @param submask Ska vara 8, 16, 24 eller 32.
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
        if (highestAddress == null)
            highestAddress = address;
        address[0] = firstAddress[0];
        // Om submask är 8 eller lägre ska denna del av adressen varieras, annars inte
        address[1] = (short) (highestAddress[1] + submask <= 8? 1 : 0);
        address[2] = (short) (highestAddress[2] + submask <= 16? 1 : 0);
        address[3] = (short)(highestAddress[3] + submask <= 32? 1 : 0);
        highestAddress = address;
        return address;
    }


}
