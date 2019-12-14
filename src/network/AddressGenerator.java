package network;

import java.util.ArrayList;

public class AddressGenerator implements Constants {
    private static AddressGenerator instance;
    private static ArrayList<Short[]> generated;

    static {
        generated = new ArrayList<>();
        instance = new AddressGenerator();
    }

    public Short[] generateAddress() {
        Short[] address = new Short[ADDRESS_LENGTH];
        address[0] = 140;
        address[1] = 1;
        address[2] = 1;
        if (generated.isEmpty())
            address[3] = 0;
        else
            address[3] = (short)(generated.get(generated.size()-1)[3]+1);
        generated.add(address);
        return address;
    }

    public static AddressGenerator getInstance() {
        return instance;
    }

}
