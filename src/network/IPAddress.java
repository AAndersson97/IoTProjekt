package network;

public class IPAddress {
    private String[] address;
    private byte[] byteAddress;

    public IPAddress(String[] address) {
        if (address == null)
            throw new IllegalArgumentException("Address must not be null");
        this.address = address;
        fillBytesArray();
    }

    public IPAddress (String address){
        if (address == null || !address.contains("."))
            throw new IllegalArgumentException("Null address or unknown address format");
        this.address = address.split(".");
        fillBytesArray();
    }

    private void fillBytesArray() {
        byteAddress = new byte[address.length];
        for (int i = 0; i < address.length; i++) {
            byteAddress[i] = Byte.decode(address[i]);
        }
    }

    public String[] getAddress() {
        return address;
    }

    public byte[] getByteAddress() {
        return byteAddress;
    }
}
