package network.utilities;

/**
 * Syftet med klassen är att generera ett unikt ID-nummer till varje paket i programmet i syfte att enkelt ta reda på
 * om två paket är lika.
 */
public class PacketIdGenerator {
    private static int highestId;
    public static int getPacketId() {
        if (highestId >= Short.MAX_VALUE)
            highestId = 0;
        return highestId++;
    }
}
