import java.util.*;
import java.lang.Math;

public class Player{

    public static final int MINUS_INFINITE = Integer.MIN_VALUE;
    public static final int PLUS_INFINITE = Integer.MAX_VALUE;

    public static final int RED_PLAYER = 1;
    public static final int WHITE_PLAYER = 2;


    public static final int MAXIMIZING_PLAYER = 1;
    public static final int MINIMIZING_PLAYER = -1;

    /*BEGIN HEURISTICS - a apagar caso nao funcione*/
    public static final int MULTIPLE_JUMPS = 10;
    public static final int CATCH_NORMAL_PIECE_VALUE = 5;

    public static final int MAKE_KING_PIECE_VALUE = 10;

    public static final int OPPONENT_CATCH_NORMAL_PIECE_VALUE = -5;
    public static final int OPPONENT_MAKE_KING_PIECE_VALUE = -10;
    public static final int OPPONENT_MULTIPLE_JUMPS = -100;

    public static final int WIN_VALUE = 300;
    public static final int LOSS_VALUE = -300;

    public static final int NORMAL_MOVE = 3;
    public static final int OPPONENT_NORMAL_MOVE = -3;




    private int playerColor;

    public static final int EXPAND_STATE_NOT_POSSIBLE = -10000;

    public GameState play(final GameState pState, final Deadline pDue){

        /*To find colour of player*/
        /*
         * CHANGE: the assignments were the other way
         */
        if(pState.getNextPlayer() == Constants.CELL_RED){
            playerColor = RED_PLAYER;
        }else if(pState.getNextPlayer() == Constants.CELL_WHITE){
            playerColor = WHITE_PLAYER;
        }

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if(lNextStates.size() == 0){
            return new GameState(pState, new Move());
        }
        else{
            int bestStateIndex = 0;
            int bestHeuristic = -1;

            int i = 0;
            for(GameState nextState : lNextStates){
                /*
                 * CHANGE:
                 * On level 0 (pState's level) it's max's turn
                 * On level 1 (nextState's level) it's min's turn
                 */
                int score = miniMax(nextState, 8, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
              //  System.err.println("SCORE: " + score);
                if(score > bestHeuristic){
                    bestHeuristic = Math.max(bestHeuristic, score);
                    bestStateIndex = i;
                    
                }
                i++;
            //if(pDue.timeUntil() <= 100000000) break;
            }
            return lNextStates.elementAt(bestStateIndex); 
        } 

    }

    public int miniMax(GameState gameState, int depth, boolean maximizingPlayer, int alpha, int beta){
        if(depth == 0 || gameState.isEOG()){
            return evaluateBoard(gameState, maximizingPlayer);
        }

        Vector<GameState> possibleStates = new Vector<GameState>();
        gameState.findPossibleMoves(possibleStates);
        int bestValue;

        
        if(maximizingPlayer){
                                                        // bestValue = MINUS_INFINITE;
            for(GameState gs : possibleStates){
                int value = miniMax(gs, depth-1, false, alpha, beta);
                if(value>alpha)
                    alpha = value;
                
                if(alpha>=beta) break;                  //bestValue = Math.max(bestValue, value);
            }return alpha;                              //bestValue;
        }
        else{
                                                        //bestValue = PLUS_INFINITE;
            for(GameState gs : possibleStates){
                int value = miniMax(gs, depth-1, true, alpha, beta);
                if(value<beta)
                    beta = value;
                
                if(alpha>=beta) break;                  //bestValue = Math.min(bestValue, value);
            }return beta;                               //bestValue;
        }
    }

    public int evaluateBoard(GameState gameState, boolean maximizingPlayer){
        Move lastMove = gameState.getMove();
        int score = 0;

        // Jogada para o Branco ganhar
        if(playerColor == WHITE_PLAYER && gameState.isWhiteWin()) return WIN_VALUE;

        //Jogada para o Vermelho ganhar
        if(playerColor == RED_PLAYER && gameState.isRedWin()) return WIN_VALUE;

        //Jogada para o Branco impedir o Vermelho de ganhar
        if(playerColor == WHITE_PLAYER && gameState.isRedWin()) return LOSS_VALUE;

        //Jogada para o Vermelho impedir o Branco de ganhar
        if(playerColor == RED_PLAYER && gameState.isWhiteWin()) return LOSS_VALUE;


        if(maximizingPlayer && lastMove.getType() > 1) score = (CATCH_NORMAL_PIECE_VALUE*lastMove.getType());//MULTIPLE_JUMPS;

        if(!maximizingPlayer && lastMove.getType() > 1) score = - (CATCH_NORMAL_PIECE_VALUE*lastMove.getType());//MULTIPLE_JUMPS ;
        //Atençao a estes dois
        if(maximizingPlayer && lastMove.isJump() && lastMove.getType()==1) 
            if(score < CATCH_NORMAL_PIECE_VALUE )
                score = CATCH_NORMAL_PIECE_VALUE;

        if(!maximizingPlayer && lastMove.isJump() && lastMove.getType()==1){
            if(score > -CATCH_NORMAL_PIECE_VALUE ){
                score = -CATCH_NORMAL_PIECE_VALUE;
            }
        }

        int redPiece = 0;
        int whitePiece = 0;

        StringTokenizer st = new StringTokenizer(gameState.toMessage());

        String board, last_move, next_player;
        int moves_left;

        board = st.nextToken();
        last_move = st.nextToken();
        next_player = st.nextToken();
        moves_left = Integer.parseInt(st.nextToken());

        for (int i = 0; i < GameState.NUMBER_OF_SQUARES; i++) 
        {
            if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_RED]) 
                redPiece++;
            else if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_WHITE])
                whitePiece++;
            else if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_RED | Constants.CELL_KING])
                redPiece+=3;
            else if (board.charAt(i) == Constants.MESSAGE_SYMBOLS[Constants.CELL_WHITE | Constants.CELL_KING])   
                whitePiece+=3;  
        }
        
        if(playerColor == WHITE_PLAYER)    
            return (whitePiece - redPiece > score) ? whitePiece-redPiece : score;
        else    
            return (redPiece - whitePiece > score) ? redPiece-whitePiece : score;
            
    }
}