/* Code prepared by Arman Haghighi & Sugandh Sinha*/
import java.util.*;

public class Player {

    public int alpha_val = Integer.MAX_VALUE; // value of alpha set to as close
                                                // to +infinity
    public int beta_val = Integer.MIN_VALUE; // value of beta set to as close to
                                                // +infinity

    public int our_color;
    int max_depth = 8;
    String temp;
    int hitten;

    /**
     * Performs a move
     * 
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    public GameState play(final GameState pState, final Deadline pDue) {
        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }
        our_color = pState.getNextPlayer();
        // System.err.println(pState.toMessage().charAt(33));
        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */

        GameState Final_move = null;
        GameState state;
        int maximum_score = Integer.MIN_VALUE;
        for (int i = 0; i < lNextStates.size(); i++) {
            state = lNextStates.elementAt(i);
            int score = alphabeta(beta_val, alpha_val, max_depth, state, false);
            /*
             * System.err.println("here are the possibles of " + i + "=" +
             * lNextStates.elementAt(i).toString(0));
             * System.err.println("the score is =" + score);
             */
          //  if (score >= maximum_score) {
                if (score > maximum_score) {
                    maximum_score = score;
                    Final_move = state;
                } else {
                    if (Math.random() < 0.5) {
                        maximum_score = score;
                        Final_move = state;
                    }
                }
         //   }
        }
        return Final_move;

    }

    public int Score_Evaluation_v1(GameState x, int team) {
        int Red_king_count = 0;
        int White_king_count = 0;
        int Red_normal_count = 0;
        int White_normal_count = 0;
        int total_count_score = 0;
        if (x.isEOG()) {
            if (team == 1 && x.isRedWin()) {
                total_count_score = 1000;
                return total_count_score;
            } else if (team == 1 && x.isWhiteWin()) {
                total_count_score = -1000;
                return total_count_score;
            } else if (team == 2 && x.isRedWin()) {
                total_count_score = -1000;
                return total_count_score;
            } else if (team == 2 && x.isWhiteWin()) {
                total_count_score = 1000;
                return total_count_score;
            } else {
                total_count_score = -1000;
                return total_count_score;
            }
        } else {

            temp = "" + x.toMessage().charAt(33);
            hitten = Integer.parseInt(temp);
            if (hitten > 0) {
                total_count_score += 3 * hitten;
            }

            for (int i = 0; i < x.NUMBER_OF_SQUARES; i++) {
                if (0 != (x.get(i) & Constants.CELL_RED)) {
                    if (team == 1) {
                        if (x.cellToCol(i) == 0 || x.cellToCol(i) == 7
                                || x.cellToRow(i) == 0 || x.cellToRow(i) == 7) {
                            total_count_score += 2;
                        } else if (x.cellToCol(i) == 1 || x.cellToCol(i) == 6) {
                            total_count_score += 1;
                        }
                    } else {
                        if (x.cellToCol(i) == 0 || x.cellToCol(i) == 7
                                || x.cellToRow(i) == 0 || x.cellToRow(i) == 7) {
                            total_count_score -= 2;
                        } else if (x.cellToCol(i) == 1 || x.cellToCol(i) == 6) {
                            total_count_score -= 1;
                        }
                    }
                    Red_normal_count += 4;
                    if (0 != (x.get(i) & Constants.CELL_KING)) {
                        Red_king_count += 3;
                    } else {
                        if (team == 1) {
                            if (x.cellToRow(i) == 6 || x.cellToRow(i) == 5) {
                                total_count_score += 1;
                            }
                        } else {
                            if (x.cellToRow(i) == 6 || x.cellToRow(i) == 5) {
                                total_count_score -= 1;
                            }
                        }
                    }
                } else if (0 != (x.get(i) & Constants.CELL_WHITE)) {
                    if (team == 2) {
                        if (x.cellToCol(i) == 0 || x.cellToCol(i) == 7
                                || x.cellToRow(i) == 0 || x.cellToRow(i) == 7) {
                            total_count_score += 2;
                        } else if (x.cellToCol(i) == 1 || x.cellToCol(i) == 6) {
                            total_count_score += 1;
                        }
                    } else {
                        if (x.cellToCol(i) == 0 || x.cellToCol(i) == 7
                                || x.cellToRow(i) == 0 || x.cellToRow(i) == 7) {
                            total_count_score -= 2;
                        } else if (x.cellToCol(i) == 1 || x.cellToCol(i) == 6) {
                            total_count_score -= 1;
                        }
                    }
                    White_normal_count += 4;
                    if (0 != (x.get(i) & Constants.CELL_KING)) {
                        White_king_count += 3;
                    } else {
                        if (team == 2) {
                            if (x.cellToRow(i) == 1 || x.cellToRow(i) == 2) {
                                total_count_score += 1;
                            }
                        } else {
                            if (x.cellToRow(i) == 1 || x.cellToRow(i) == 2) {
                                total_count_score -= 1;
                            }
                        }
                    }
                }
            }
            if (team == 1) {
                total_count_score += Red_king_count;
                total_count_score += Red_normal_count;
                total_count_score -= White_king_count;
                total_count_score -= White_normal_count;
                return total_count_score;
            } else {
                total_count_score += White_king_count;
                total_count_score += White_normal_count;
                total_count_score -= Red_king_count;
                total_count_score -= Red_normal_count;

                return total_count_score;
            }
        }

    }

    public int alphabeta(int alpha, int beta, int depth, GameState x,
            Boolean maxplayer) {
        // System.err.println("player color is " + x.getNextPlayer());
        if ((depth <= 0) || x.isEOG()) {
            return Score_Evaluation_v1(x, our_color);
        }

        Vector<GameState> Possible_next_states = new Vector<GameState>();
        x.findPossibleMoves(Possible_next_states);

        int alphabeta_return_value;
        // int v = 0;

        // Maximizing player
        if (maxplayer) {
            alphabeta_return_value = beta_val;

            for (GameState i : Possible_next_states) {

                alphabeta_return_value = Math.max(alphabeta_return_value,
                        alphabeta(alpha, beta, depth - 1, i, false));
                alpha = Math.max(alpha, alphabeta_return_value);
                if (beta <= alpha) {
                    break;
                }
            }
            return alphabeta_return_value;
            // System.err.println("MAX returns "+v);
        }
        // Minimizing player
        else {
            alphabeta_return_value = alpha_val;
            for (GameState i : Possible_next_states) {
                alphabeta_return_value = Math.min(alphabeta_return_value,
                        (alphabeta(alpha, beta, depth - 1, i, true)));
                beta = Math.min(beta, alphabeta_return_value);
                if (beta <= alpha) {
                    break;
                }
            }
            return alphabeta_return_value;
            // System.err.println("MIN returns "+v+" ");
        }
        // return v;

    }
}