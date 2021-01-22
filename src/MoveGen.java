package src;

public class MoveGen {


    public static int minimax(int depth, int alpha, int beta, long[] allBitboards, boolean white){
        String moves;

        //Generating possible moves
        if (white){
            moves = Moves.possibleWhiteMoves(allBitboards, Board.history);
        }else{
            moves = Moves.possibleBlackMoves(allBitboards, Board.history);
        }
        // Depth reached or checkmate
        if (depth == 0 || moves.length() == 0){
            return evaluate(allBitboards);
        }
        long WP = allBitboards[0], WB = allBitboards[1], WN = allBitboards[2], WR = allBitboards[3],
                WQ = allBitboards[4], WK = allBitboards[5], BP = allBitboards[6], BB = allBitboards[7],
                BN = allBitboards[8], BR = allBitboards[9], BQ = allBitboards[10], BK = allBitboards[11];

        if (white){
            double maxEval = Double.NEGATIVE_INFINITY;
            // Looping through every move
            for (int i = 0; i < moves.length(); i+=4){
                // Getting the evaluation for a move
                int eval = minimax(depth - 1, alpha, beta, allBitboards, false);
                // White is maximizing
                maxEval = Math.max(maxEval, eval);
                // Setting alpha as the maximum eval so far
                alpha = Math.max(alpha, eval);

                // Since alpha is greater than beta the maximizing player(white) already found a better move earlier so we prune this branch
                if (beta <= alpha){
                    break;
                }
            }
            return (int) maxEval;


        }else{
            double minEval = Double.POSITIVE_INFINITY;
            for (int i = 0; i < moves.length(); i+=4){
                int eval = minimax(depth - 1, alpha, beta, allBitboards, false);
                // Black is minimizing
                minEval = Math.min(minEval, eval);
                // Setting beta as the lowest eval so far
                beta = Math.min(alpha, eval);

                if (beta <= alpha){
                    break;
                }
            }
            return (int) minEval;


        }
    }


    public static int evaluate(long[] allBitboards){



        return 0;
    }
}
