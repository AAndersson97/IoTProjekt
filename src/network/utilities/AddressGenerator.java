package network.utilities;
import java.util.Stack;

import static network.Constants.Network.FIRST_ADDRESS;
import static network.Constants.Protocol.ADDRESS_LENGTH;
import static network.Constants.Node.MAX_NODES;

public class AddressGenerator {
    private static short[][] addresses;
    private static int numOfAddresses;
    private static Stack<short[]> returnedAddresses;

    private AddressGenerator() {
    }

    public static short[] generateAddress() {
        if (numOfAddresses == 0) {
            addresses = new short[MAX_NODES + 500][ADDRESS_LENGTH];
            addresses[numOfAddresses++] = FIRST_ADDRESS;
            returnedAddresses = new Stack<>();
            return FIRST_ADDRESS;
        }
        if (!returnedAddresses.empty())
            return returnedAddresses.pop();
        short[] address = new short[ADDRESS_LENGTH];
        address[0] = addresses[0][0];
        address[1] = addresses[0][1];
        address[2] = addresses[0][2];
        address[3] = (short) (addresses[numOfAddresses-1][3]+1);
        addresses[numOfAddresses++] = address;
        return address;
    }

    public static void returnAddress(short[] addr) {
        returnedAddresses.push(addr);
    }
}
