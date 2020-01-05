package network;

import java.util.*;

public class MPRCalculator {
    // N - den aktuella noden, noden som använder en instans av denna klass
    private ArrayList<NeighborTuple> membersOfN;
    private HashMap<short[], Integer> degreeOfY;
    private ArrayList<TwoHopTuple> twoHopNeighbors;
    private final short[] currentNode;
    private Set<short[]> mprSet;

    public MPRCalculator(Collection<NeighborTuple> membersOfN, ArrayList<TwoHopTuple> twoHopNeighbors, short[] currentNode) {
        this.membersOfN = new ArrayList<>(membersOfN);
        this.twoHopNeighbors = twoHopNeighbors;
        this.currentNode = currentNode;
        mprSet = new HashSet<>();
        degreeOfY = new HashMap<>();
        for (NeighborTuple tuple : membersOfN)
            degreeOfY.put(tuple.n_neighbor_main_addr,0);
        filterTwoHopNeighbors();
        populateDegreeOfY();
    }



    /**
     * Dy är graden av en 1-hoppsgranne y, y är en nod från datasamlingen membersOfN. Definieras som antalet symmetriska grannar till nod y exkluderat
     * grannar till nod N och noden som utför beräkningen (den aktuella noden).
     */
    public void populateDegreeOfY() {
        for (TwoHopTuple tuple : twoHopNeighbors) {
            if (!Arrays.equals(tuple.n_neighbor_main_addr, currentNode)
                    && !degreeOfY.containsKey(tuple.n_neighbor_main_addr)) {
                degreeOfY.put(tuple.n_neighbor_main_addr, degreeOfY.get(tuple.n_neighbor_main_addr)+1);
            }

        }
    }

    public short[][] populateAndReturnMPRSet() {
        Iterator<TwoHopTuple> iterator = twoHopNeighbors.iterator();
        while (iterator.hasNext()) {
            TwoHopTuple tuple = iterator.next();
            if (tuple.n_neighbor_main_addr != null)
                mprSet.add(tuple.n_neighbor_main_addr);
            iterator.remove();
        }
        int numOfNotCoveredNodes = 0;
        /**
         * Om det kvarstår 2-hoppsgrannar som inte har en symmetrisk länk till en MPR-adress i mprSet ska kontroll ske om
         * det finns 1-hoppsgrannar som har en symmetrisk länk till någon av 2-hoppsgrannarna
         */
        if (!twoHopNeighbors.isEmpty()) {
            for (TwoHopTuple twoHopTuple : twoHopNeighbors) {
                if (!mprSet.contains(twoHopTuple.n_neighbor_main_addr)) {
                    for (NeighborTuple tuple : membersOfN){
                        if (Arrays.equals(twoHopTuple.n_neighbor_main_addr, tuple.n_neighbor_main_addr))
                            mprSet.add(tuple.n_neighbor_main_addr);
                    }
                }

            }
        }

        short[][] mprArray = new short[mprSet.size()][];
        mprSet.toArray(mprArray);
        return mprArray;
    }

    /**
     * Exkludera följande från twoHopNeighbors-samlingen:
     * 1. Noder som enbart är nåbara av medlemmar till N med villighet satt till WILL_NEVER (ej relevant i detta fall)
     * 2. Noden som utför beräkningen, alltså den aktuella noden
     * 3. All symmetriska grannar: noder för vilket det existerar en symmetrisk länk till denna nod på något gränssnitt (i detta program används enbart ett gränssnitt)
     */
    private void filterTwoHopNeighbors() {
        twoHopNeighbors.removeIf((address) -> Arrays.equals(currentNode, address.n_2hop_addr) || degreeOfY.containsKey(address));
    }
}
