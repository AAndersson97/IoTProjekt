package network;

import java.util.ArrayList;

public class MisbehaviourVoting {
    private static int votingNum;
    /**
     *
     * @param misbehaving nod som ska röstas bort från nätverket p.g.a dåligt uppförande
     * @return resultatet från omröstningen
     */
    public VotingResult startVoting(Node misbehaving) {
        ArrayList<Node> nodes = Network.getNodeList();
        int numOfAgree = 0;
        for (Node node : nodes) {
            if (node == misbehaving)
                continue;
            if (node.requestVote() == Vote.AGREE)
                numOfAgree++;
        }
        // Följande sträng skrivs till fil: omröstningsnummer,antalet agree-röster, antalet disagree-röster, noden som röstningen handlar om
        LogWriter.getInstance().writeVotingResult(++votingNum + ";" + numOfAgree + ";" + (nodes.size() - (numOfAgree + 1)) + ";" + misbehaving.getAddressString());

        // Antalet disagree-röster är lika med alla noder som ej röstade agree och inte heller noden som anklagas för dåligt uppförande
        return new VotingResult(numOfAgree, nodes.size() - (numOfAgree + 1));
    }

    public enum Vote {
        AGREE, DISAGREE;
    }

    public class VotingResult {
        public final int numOfAgree;
        public final int numOfDisagree;
        public VotingResult(int numOfAgree, int numOfDisagree) {
            this.numOfAgree = numOfAgree;
            this.numOfDisagree = numOfDisagree;
        }
    }

}
