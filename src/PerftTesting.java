package src;

import java.util.ArrayList;
import java.util.HashMap;

public class PerftTesting {

    static int MAX_DEPTH = 5;
    static boolean whiteToMove = true;
    static int perftCounter = 0;

    public static void perft(long WP,long WB,long WN,long WR,long WQ,long WK,long BP,long BB,long BN,long BR,long BQ,
                             long BK, int depth, boolean wkcastle, boolean wqcastle, boolean bkcastle, boolean bqcastle, boolean whiteToMove){

        if (depth < MAX_DEPTH){
            String moves;
            boolean tempWKC = wkcastle, tempWQC = wqcastle, tempBKC = bkcastle, tempBQC = bqcastle;
            long[] allBitBoards = new long[]{WP, WB, WN, WR, WQ, WK, BP, BB, BN, BR, BQ, BK};
            // Getting all white or black moves depending on who's turn it is
            if (whiteToMove){
                moves = Moves.possibleWhiteMoves(allBitBoards);
            }else{
                moves = Moves.possibleBlackMoves(allBitBoards);
            }
            // Looping over each move and making the move on each bitboard. We must use temporary variables so that
            // changes to a board in one path does not change the board in other paths
            for (int i = 0; i < moves.length(); i+=4){
                long tempWP = Moves.makeMove(WP,moves.substring(i, i + 4), 'P'), tempWB = Moves.makeMove(WB,moves.substring(i, i + 4), 'B'),
                        tempWN = Moves.makeMove(WN,moves.substring(i, i + 4), 'N'),tempWR = Moves.makeMove(WR,moves.substring(i, i + 4), 'R'),
                        tempWQ = Moves.makeMove(WQ,moves.substring(i, i + 4), 'Q'),tempWK = Moves.makeMove(WK,moves.substring(i, i + 4), 'K'),
                        tempBP = Moves.makeMove(BP,moves.substring(i, i + 4), 'p'), tempBB = Moves.makeMove(BB,moves.substring(i, i + 4), 'b'),
                        tempBN = Moves.makeMove(BN,moves.substring(i, i + 4), 'n'),tempBR = Moves.makeMove(BR,moves.substring(i, i + 4), 'r'),
                        tempBQ = Moves.makeMove(BQ,moves.substring(i, i + 4), 'q'),tempBK = Moves.makeMove(BK,moves.substring(i, i + 4), 'k');
                // Handling castling logic
                long[] newBitBoards = new long[]{tempWP, tempWB, tempWN, tempWR, tempWQ, tempWK, tempBP, tempBB, tempBN, tempBR, tempBQ, tempBK};
                if (Character.isDigit(moves.charAt(i + 3))){
                    // Getting the square number of the initial square
                    int start = (Character.getNumericValue(moves.charAt(i))*8) + (Character.getNumericValue(moves.charAt(i+1)));

                    // Checking if the white rook at h1 moved
                    if (((1L<<start)&WR&(1L<<63))!=0){
                        tempWKC = false;
                    }
                    // Checking if the white rook at a1 moved
                    if (((1L<<start)&WR&(1L<<56))!=0){
                        tempWQC = false;
                    }
                    // Checking if the black rook at h8 moved
                    if (((1L<<start)&BR&(1L<<7))!=0){
                        tempBKC = false;
                    }
                    // Checking if the black rook at a8 moved
                    if (((1L<<start)&BR&(1L))!=0){
                        tempBQC = false;
                    }
                    // Checking if the white king moved
                    if (((1L<<start)&WK)!=0){
                        tempWKC = false;
                        tempWQC = false;
                    }
                    // Checking if the black king moved
                    if (((1L<<start)&BK)!=0){
                        tempBKC = false;
                        tempBQC = false;
                    }
                    // Making sure the move is legal. (The move does not put the king in check or keep the king in check)
                    if (((tempWK&Moves.coveredByBlack(newBitBoards))==0&&whiteToMove) ||
                            ((tempBK&Moves.coveredByWhite(newBitBoards))==0&&!whiteToMove)){
                            // Perft testing only counts the number of leaf nodes at a certain depth
                            if (depth + 1==MAX_DEPTH){
                                perftCounter++;
                            }
                            // Recursively call perft() with the updated bitboards and booleans until we reach the max depth
                            perft(tempWP,tempWB,tempWN,tempWR,tempWQ,tempWK,tempBP,tempBB,tempBN,tempBR,tempBQ,tempBK,
                                    depth + 1,tempWKC,tempWQC,tempBKC,tempBQC, !whiteToMove);

                        }




                }


            }







        }



    }

}


