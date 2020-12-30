import java.util.*;
import java.io.IOException;

public class Player {
    private static final int MAX_DEPTH = 9;

    // Time threshold of 0.01 seconds
    private static long TIME_THRESHOLD_NANOSECONDS = 100000000;

    private boolean isThisPlayerWhite;
    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue){
        isThisPlayerWhite = pState.getNextPlayer() == Constants.CELL_WHITE;

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        //alphabeta(pState, 0, alpha, beta, true);

        
        int bestMoveIndex = 0;
        int i = 0;
        int alpha = -9999, beta = 9999;
        for(GameState nextState : lNextStates) {
            // Check if it is the best heuristic that we have so far
            int heuristic = alphabeta(nextState, 1, alpha, beta, false);
            if( heuristic > alpha) {
                alpha = heuristic;
                bestMoveIndex = i;
            }
            // Check time
            if(pDue.timeUntil() <= TIME_THRESHOLD_NANOSECONDS)
                return lNextStates.elementAt(bestMoveIndex);
            ++i;
        }

        return lNextStates.elementAt(bestMoveIndex);
    }

    private int alphabeta(GameState rootState, int depth, int alpha, int beta, boolean turnOfMax) {
        // Get the heuristic of each branch
        Vector<GameState> lNextStates = new Vector<GameState>();
        rootState.findPossibleMoves(lNextStates);
        // If it's a leaf, then we compute the heuristic of this move
        if( rootState.isEOG() ) {
            if( isThisPlayerWhite ) {
                if(rootState.isWhiteWin())
                    return Integer.MAX_VALUE;
                else
                    return Integer.MIN_VALUE;
            }
            else {
                if(rootState.isWhiteWin())
                    return Integer.MIN_VALUE;
                else
                    return Integer.MAX_VALUE;   
            }
        }
        if( depth == MAX_DEPTH || lNextStates.size()==0 ) {
            return computeHeuristicOfLeaf(rootState, depth);
        }
        if( turnOfMax) {
            for(GameState nextState : lNextStates) {
                alpha = max(alpha, alphabeta(nextState, depth+1, alpha, beta, false));
                if( beta <= alpha) {
                    return alpha;
                }
            }
            return alpha;
        }
        else {
            for(GameState nextState : lNextStates) {
                beta = min(beta, alphabeta(nextState, depth+1, alpha, beta, true));
                if( beta <= alpha) {
                    return beta;
                }
            }
            return beta;
        }
    }

    private int computeHeuristicOfLeaf(GameState rootState, int depth) {        
        // Simple heuristic v2: number of pieces that I have (kings count as two)
        int red_pieces = 0;
        int white_pieces = 0;
        for (int i = 0; i < GameState.NUMBER_OF_SQUARES; i++) {
            int cell = rootState.get(i);
            if (0 != (cell & Constants.CELL_RED)) {
                if(cell == Constants.CELL_KING)
                    red_pieces += 5;
                else
                    red_pieces += 3;
            } else if (0 != (cell & Constants.CELL_WHITE)) {
                if(cell == Constants.CELL_KING)
                    white_pieces += 5;
                else
                    white_pieces += 3;
            }
        }
        return isThisPlayerWhite ? white_pieces-red_pieces : red_pieces - white_pieces;
    }

    private static void printSpaces(int depth) {
        for(int i = 0; i < depth; i++) {
            System.err.print("   ");
        }
    }

    private static int max(int n1, int n2) {
        return n1 >= n2 ? n1 : n2;
    }

    private static int min(int n1, int n2) {
        return n1 < n2 ? n1 : n2;
    }

    public static class Temporary {
        public int alpha, beta, index;
    }
}
