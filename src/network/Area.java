package network;

import java.net.InetAddress;
import java.util.HashMap;

public class Area {
    private final int num;
    private final AddressGenerator addressGenerator;
    private short[] ABR;
    private static HashMap<short[], Router> nodeList;
    /**
     *
     * @param num Områdets nummer, område 0 är ett backbone area, alla områden har en uppkoppling till område 0
     * @param firstAddress Adressen för första routern i area, avgör vilka adresser nya noder i arean tilldelas
     * @param subnetMask En subnätsmask som används för att särskilja mellan nätverksadress och värdadress, submasken gäller för routrar inom området
     */
    Area(int num, short[] firstAddress, int subnetMask) {
        nodeList = new HashMap<>();
        this.num = num;
        addressGenerator = new AddressGenerator(firstAddress, subnetMask);
    }

    public void addNode(Router router) {
        if (nodeList.isEmpty() && num != 0) {
            router.setIsABR(true);
            ABR = router.getAddress();
        }
        nodeList.put(router.getAddress(), router);
        router.setAddress(addressGenerator.generateAddress());
        Network.newNodeAdded();
    }

    private void reallocate(Router router) {
        switch (num) {

        }
    }

    public void removeNode(short[] address) {
        if (address == null)
            throw new IllegalArgumentException();
        nodeList.remove(address);
    }

    public int getNumOfNodes() {
        return nodeList.size();
    }

    public short[] getABRAddress() {
        return ABR;
    }

}