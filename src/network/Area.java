package network;

public class Area {
    private final int num;
    private final AddressGenerator addressGenerator;

    /**
     *
     * @param num Områdets nummer, område 0 är ett backbone area, alla områden har en uppkoppling till område 0
     * @param firstAddress Adressen för första routern i area, avgör vilka adresser nya noder i arean tilldelas
     * @param subnetMask En subnätsmask som används för att särskilja mellan nätverksadress och värdadress, submasken gäller för routrar inom området
     */
    Area(int num, short[] firstAddress, int subnetMask) {
        this.num = num;
        addressGenerator = new AddressGenerator();
    }
}
