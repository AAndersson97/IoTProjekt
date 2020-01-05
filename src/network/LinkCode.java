package network;

public class LinkCode {
    public final LinkTypes linkType;
    public final NeighborTypes neighborType;

    public LinkCode(LinkTypes linkType, NeighborTypes neighborType) {
        this.linkType = linkType;
        this.neighborType = neighborType;
        if (!validCombination())
            throw new IllegalStateException("There can't exist a link with a non neighbor");
    }

    private boolean validCombination() {
        return (linkType != LinkTypes.SYM_LINK && linkType != LinkTypes.ASYM_LINK) || neighborType != NeighborTypes.NOT_NEIGH;
    }

    /**
     * UNSPEC_LINK - ingen specifik info om länken finns
     * ASYM_LINK - länkarna är asymmetriska, alltså envägs
     * SYM_LINK - länkarna är symmetriska
     * LOST_LINK - länkarna har gått förlorade
     */
    public enum LinkTypes {
        UNSPEC_LINK(0), ASYM_LINK(1), SYM_LINK(2), LOST_LINK(3);
        int value;
        LinkTypes(int value) {
            this.value = value;
        }
    }

    /**
     * SYM_NEIGH - det finns minst en symmetrisk länk mellan grannarna och denna nod
     * MPR_NEIGH - grannarna har minst en symmetrisk länk och har valts som MPR-nod av avsändaren
     * NOT_NEIGH - noderna är antingen inte längre grannar eller har inte än blivit symmetriska grannar
     */
    public enum NeighborTypes {
        NOT_NEIGH(0), SYM_NEIGH(1), MPR_NEIGH(2);
        int value;
        NeighborTypes(int value) {
            this.value = value;
        }
    }
}
