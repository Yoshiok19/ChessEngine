package src;

import java.util.List;

public class Moves {
    static long CAPTURABLE;
    static long BLACK_PIECES;
    static long EMPTY;
    // Creating constants for parts of the board for easy checking down the line.
    static long RANK1 = -72057594037927936L; //"1111111100000000000000000000000000000000000000000000000000000000"
    static long RANK8 = 255L; //"0000000000000000000000000000000000000000000000000000000011111111"
    static long RANK4 = 1095216660480L; //"0000000000000000000000001111111100000000000000000000000000000000"
    static long RANK5 = 4278190080L; //"0000000000000000000000000000000011111111000000000000000000000000"
    static long FILEA = 72340172838076673L; //"0000000100000001000000010000000100000001000000010000000100000001"
    static long FILEH = -9187201950435737472L; //"1000000010000000100000001000000010000000100000001000000010000000"


    public static String possibleWhiteMoves(long[] allBitBoards){
        long WP = allBitBoards[0], WB = allBitBoards[1], WN = allBitBoards[2], WR = allBitBoards[3],
                WQ = allBitBoards[4], WK = allBitBoards[5], BP =allBitBoards[6], BB =allBitBoards[7],
                BN = allBitBoards[8], BR = allBitBoards[9], BQ = allBitBoards[10], BK = allBitBoards[11];

        // Bitboard of pieces that white can capture or occupy.
        CAPTURABLE = ~(WP|WB|WN|WR|WQ|WK|BK);
        // Bitboard of black pieces
        BLACK_PIECES = ~(BP|BB|BN|BR|BQ);
        // Bitboard of empty squares;
        EMPTY = ~(WP|WB|WN|WR|WQ|WK|BP|BB|BN|BR|BQ|BK);

        // Note: Moves are in this format: startingx startingy endingx endingy (no spaces, commas, etc.)

        String allWhiteMoves = possibleWPMoves(WP) + possibleWNMoves(WN);

        return allWhiteMoves;

    }

    public static String possibleWPMoves(long WP){
        String allWPMoves = "";

        // All legal moves for a white pawn capturing to the right.
        long PAWN_RIGHT = (WP>>7)&(BLACK_PIECES)&~(RANK8)&~(FILEA);

        // For every square between the first legal move to the last legal move record the move. Excluding leading 0s
        // and trailing 0s reduces the number of squares to check without missing possible moves. (Especially for when
        // there is only one piece of a certain type)

        for (int i = Long.numberOfLeadingZeros(PAWN_RIGHT); i < 64-Long.numberOfTrailingZeros(PAWN_RIGHT); i++){
            if (((PAWN_RIGHT>>i)&1) == 1){
                // i/8 and i %8 represents the ending coordinates so we must change the starting coordinate accordingly.
                allWPMoves += (i/8 + 1) + (i%8 - 1) + (i/8) + (i % 8);
            }
        }
        // All legal moves for a white pawn capturing to the left.
        long PAWN_LEFT = (WP>>9)&(BLACK_PIECES)&~(RANK8)&~(FILEH);
        for (int i = Long.numberOfLeadingZeros(PAWN_LEFT); i < 64-Long.numberOfTrailingZeros(PAWN_LEFT); i++){
            if (((PAWN_RIGHT>>i)&1) == 1){
                allWPMoves += (i/8 + 1) + (i%8 + 1) + (i/8) + (i % 8);
            }
        }

        // All legal moves for pushing a white pawn 1 square.
        long PAWN_PUSH = (WP>>8)&(EMPTY)&~(RANK8);
        for (int i = Long.numberOfLeadingZeros(PAWN_PUSH); i < 64-Long.numberOfTrailingZeros(PAWN_PUSH); i++){
            if (((PAWN_RIGHT>>i)&1) == 1){
                allWPMoves += (i/8 + 1) + (i%8) + (i/8) + (i % 8);
            }
        }

        // All legal moves for pushing a white pawn 2 squares. (this can only happen when the pawn is on the 2nd rank)
        long PAWN_LEAP = (WP>>16)&(EMPTY)&(EMPTY>>8)&(RANK4);
        for (int i = Long.numberOfLeadingZeros(PAWN_LEAP); i < 64-Long.numberOfTrailingZeros(PAWN_LEAP); i++){
            if (((PAWN_RIGHT>>i)&1) == 1){
                allWPMoves += (i/8 + 2) + (i%8) + (i/8) + (i % 8);
            }
        }

        // Promotions:
        // For now promotion is indicated with a "P" at the end

        // All legal moves for pushing a white pawn 1 square with promotion.
        long PAWN_PROMOTION_FORWARD = (WP>>8)&(RANK8)&(EMPTY);
        for (int i = Long.numberOfLeadingZeros(PAWN_PROMOTION_FORWARD); i < 64-Long.numberOfTrailingZeros(PAWN_PROMOTION_FORWARD); i++){
            if (((PAWN_RIGHT>>i)&1) == 1){
                allWPMoves += (i/8 + 1) + (i%8) + (i/8) + (i % 8) + "P";
            }
        }
        // All legal moves for capturing to the right with promotion.
        long PAWN_PROMOTION_RIGHT = (WP>>7)&(RANK8)&(BLACK_PIECES)&~(FILEA);
        for (int i = Long.numberOfLeadingZeros(PAWN_PROMOTION_RIGHT); i < 64-Long.numberOfTrailingZeros(PAWN_PROMOTION_RIGHT); i++){
            if (((PAWN_RIGHT>>i)&1) == 1){
                allWPMoves += (i/8 + 1) + (i%8 - 1) + (i/8) + (i % 8) + "P";
            }
        }
        // All legal moves for capturing to the left with promotion.
        long PAWN_PROMOTION_LEFT = (WP>>9)&(RANK8)&(BLACK_PIECES)&~(FILEH);
        for (int i = Long.numberOfLeadingZeros(PAWN_PROMOTION_LEFT); i < 64-Long.numberOfTrailingZeros(PAWN_PROMOTION_LEFT); i++){
            if (((PAWN_RIGHT>>i)&1) == 1){
                allWPMoves += (i/8 + 1) + (i%8 + 1) + (i/8) + (i % 8) + "P";
            }
        }
        return allWPMoves;


    }

    public static String possibleWNMoves(long WN){

        return "";
    }

}
