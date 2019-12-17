package network;

import java.util.HashMap;

public class Area {
    private final int num;
    private final AddressGenerator addressGenerator;
    private static HashMap<short[],Node> nodeList;
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

    public void addNode(Node node) {
        nodeList.put(node.getAddress(),node);
        Network.newNodeAdded();
    }

    public HashMap<short[],Node> getNodeList() {
        return new HashMap<>(nodeList);
    }

    public Node getNode(short[] address) {
        return nodeList.get(address);
    }

    public void removeNode(short[] address) {
        if (address == null)
            throw new IllegalArgumentException();
        nodeList.remove(address);
    }

    public int getNumOfNodes() {
        return nodeList.size();
    }


}