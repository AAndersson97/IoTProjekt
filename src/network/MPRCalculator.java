package network;

import java.util.*;

public class MPRCalculator {
    private ArrayList<NeighborTuple> neighborSet;
    private ArrayList<TwoHopTuple> twoHopNeighbors;
    private final short[] receiverAddress;
    private Set<short[]> mprSet;

    public MPRCalculator(Collection<NeighborTuple> neighborSet, Collection<TwoHopTuple> twoHopNeighbors, short[] receiverAddress) {
        this.neighborSet = new ArrayList<>(neighborSet);
        this.twoHopNeighbors = new ArrayList<>(twoHopNeighbors);
        this.receiverAddress = receiverAddress;
        this.mprSet = new HashSet<>();
    }

    /**
     * Räknar ut graden av en 1-hoppsgranne n, n är en nod från datasamlingen neighborSet. Definieras som antalet symmetriska grannar till nod n exkluderat
     * grannar till noden som skapade detta objekt och noden själv.
     */
    public int degreeOfN(NeighborTuple neighborTuple) {
        int degree = 0;
        for (TwoHopTuple twoHopTuple : twoHopNeighbors) {
            if (Arrays.equals(twoHopTuple.n_neighbor_main_addr, neighborTuple.n_neighbor_main_addr)
                    && !neighborSet.contains(neighborTuple)) {
                    degree++;
            }
        }
        return degree;
    }

    public short[][] populateAndReturnMPRSet() {
        HashMap<short[],NeighborTuple> symmetricNeighbors = new HashMap<>();
        ArrayList<TwoHopTuple> twoHopNeighbors = new ArrayList<>();
        for (NeighborTuple tuple : neighborSet) {
            if (tuple.status == NeighborTuple.N_status.SYM
                    && tuple.n_willingness != Constants.Protocol.Willingness.WILL_NEVER) {
                symmetricNeighbors.put(tuple.n_neighbor_main_addr,tuple);
            }
        }
        for (TwoHopTuple twoHopTuple : this.twoHopNeighbors) {
            // Inkludera ej mottagarnodens address
            if (!Arrays.equals(twoHopTuple.n_2hop_addr, receiverAddress)) {
                if (!symmetricNeighbors.containsKey(twoHopTuple.n_2hop_addr)) {
                    twoHopNeighbors.add(twoHopTuple);
                }
            }
        }
        Set<short[]> coveredTwoHopNeighbors = new HashSet<>();
        for (TwoHopTuple twoHopTuple : twoHopNeighbors) {
            boolean onlyOne = true;
            for (TwoHopTuple tuple : twoHopNeighbors) {
                if (Arrays.equals(tuple.n_2hop_addr, twoHopTuple.n_2hop_addr)
                        && !Arrays.equals(tuple.n_neighbor_main_addr, twoHopTuple.n_neighbor_main_addr)) {
                    onlyOne = false;
                    break;
                }
            }
            if (onlyOne) {
                mprSet.add(twoHopTuple.n_neighbor_main_addr);
                for (TwoHopTuple thirdTuple : twoHopNeighbors) {
                    if (Arrays.equals(thirdTuple.n_neighbor_main_addr, twoHopTuple.n_neighbor_main_addr)) {
                        coveredTwoHopNeighbors.add(thirdTuple.n_2hop_addr);
                    }
                }
            }
        }
        Iterator<TwoHopTuple> iterator;
        for (iterator = twoHopNeighbors.iterator(); iterator.hasNext();) {
            TwoHopTuple twoHopTuple = iterator.next();
            if (coveredTwoHopNeighbors.contains(twoHopTuple.n_2hop_addr)) {
                iterator.remove();
            }
        }
        /**
         * Om det kvarstår 2-hoppsgrannar som inte har en symmetrisk länk till en MPR-adress i mprSet ska kontroll ske om
         * det finns 1-hoppsgrannar som har en symmetrisk länk till någon av 2-hoppsgrannarna
         */
        if (!twoHopNeighbors.isEmpty()) {
            HashMap<Integer, NeighborTuple> reachability = new HashMap<>(); // nyckeln är antalet noder den aktuella noden kan nå
            for (NeighborTuple neighborTuple : neighborSet) {
                int r = 0;
                for (TwoHopTuple twoHopTuple : twoHopNeighbors) {
                    if (Arrays.equals(neighborTuple.n_neighbor_main_addr, twoHopTuple.n_neighbor_main_addr)) {
                        r++;
                    }
                }
                reachability.put(r, neighborTuple);
            }
            NeighborTuple highest = null;
            int highestR = 0; // highest reachability
            for (Map.Entry<Integer, NeighborTuple> entry : reachability.entrySet()) {
                int key = entry.getKey();
                NeighborTuple value = entry.getValue();
                // Vi är ej intresserad av noder som inte kan nå några andra noder
                if (key != 0) {
                    if (highest == null || key > highestR) {
                        highest = value;
                        highestR = key;
                    }
                    else if (key == highestR) {
                        if (degreeOfN(value) > degreeOfN(highest)) {
                            highest = value;
                            highestR = key;
                        }
                    }
                }
            }
            if (highest != null) {
                mprSet.add(highest.n_neighbor_main_addr);
                NeighborTuple finalHighest = highest;
                twoHopNeighbors.removeIf(twoHopTuple -> Arrays.equals(twoHopTuple.n_neighbor_main_addr, finalHighest.n_neighbor_main_addr));
            }
        }
        short[][] mprArray = new short[mprSet.size()][];
        mprSet.toArray(mprArray);
        return mprArray;
    }
}
