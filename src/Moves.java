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
    // Files A to H
    static long[] FILES = {72340172838076673L, 144680345676153346L, 289360691352306692L, 578721382704613384L,
                           1157442765409226768L, 2314885530818453536L, 4629771061636907072L, -9187201950435737472L};
    // Ranks 1 to 8
    static long[] RANKS = {-72057594037927936L, 71776119061217280L, 280375465082880L, 1095216660480L, 4278190080L,
                           16711680L, 65280L, 255L};

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

        String history = ""; // History is empty for now.

        String allWhiteMoves = possibleWPMoves(WP, BP, history) + possibleWNMoves(WN);

        return allWhiteMoves;

    }



    public static String possibleWPMoves(long WP, long BP, String history){
        String allWPMoves = "";

        // All legal moves for a white pawn capturing to the right.
        long PAWN_RIGHT = (WP>>7)&(BLACK_PIECES)&~(RANK8)&~(FILEA);

        // Obtaining the bitboard containing only the first move. Note: (PAWN_RIGHT-1) is the bitboard with only the
        // first move as 0 while the rest of the moves are 1.
        // So ~(PAWN_RIGHT-1) is the bitboard with only the first move as 1 with the rest of the moves as 0.
        // Then PAWN_RIGHT&(~PAWN_RIGHT-1) would be only the first move.

        // EXAMPLE: m = 0010100      m - 1 = 0010011     ~(m-1)= 1101100     m&~(m-1) = 000100 only the first move is 1.

        long aMove = PAWN_RIGHT&(~PAWN_RIGHT-1);

        while(aMove != 0){

            // Getting the square of the move.
            int i = Long.numberOfTrailingZeros(aMove);
            // Recording the move with the starting and ending coordinates.
            allWPMoves += (i/8 + 1) + (i%8 - 1) + (i/8) + (i%8);

            // Removing the move from the total move bitboard.
            PAWN_RIGHT = PAWN_RIGHT&~aMove;

            // Doing the same thing as before but we move onto the next move.
            aMove = PAWN_RIGHT&(~PAWN_RIGHT-1);
        }
        // All legal moves for a white pawn capturing to the left.
        long PAWN_LEFT = (WP>>9)&(BLACK_PIECES)&~(RANK8)&~(FILEH);
        aMove = PAWN_LEFT&(~PAWN_LEFT-1);
        while(aMove != 0){
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8 + 1) + (i/8) + (i % 8);
            PAWN_LEFT = PAWN_LEFT&~aMove;
            aMove = PAWN_LEFT&(~PAWN_LEFT-1);
        }
        // All legal moves for pushing a white pawn 1 square.
        long PAWN_PUSH = (WP>>8)&(EMPTY)&~(RANK8);
        aMove = PAWN_PUSH&(~PAWN_PUSH-1);

        while(aMove != 0){
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8) + (i/8) + (i % 8);
            PAWN_PUSH = PAWN_PUSH&~aMove;
            aMove = PAWN_PUSH&(~PAWN_PUSH-1);
        }
        // All legal moves for pushing a white pawn 2 squares. (this can only happen when the pawn is on the 2nd rank)
        long PAWN_LEAP = (WP>>16)&(EMPTY)&(EMPTY>>8)&(RANK4);
        aMove = PAWN_LEAP&(~PAWN_LEAP-1);

        while(aMove != 0){
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 2) + (i%8) + (i/8) + (i % 8);
            PAWN_LEAP = PAWN_LEAP&~aMove;
            aMove = PAWN_LEAP&(~PAWN_LEAP-1);
        }

        // Promotions:
        // For now promotion is indicated with a "P" at the end

        // All legal moves for pushing a white pawn 1 square with promotion.
        long PAWN_PROMOTION_FORWARD = (WP>>8)&(RANK8)&(EMPTY);
        aMove = PAWN_PROMOTION_FORWARD&(~PAWN_PROMOTION_FORWARD-1);

        while(aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8) + "P";
            PAWN_PROMOTION_FORWARD = PAWN_PROMOTION_FORWARD & ~aMove;
            aMove = PAWN_PROMOTION_FORWARD & (~PAWN_PROMOTION_FORWARD - 1);
        }
        // All legal moves for capturing to the right with promotion.
        long PAWN_PROMOTION_RIGHT = (WP>>7)&(RANK8)&(BLACK_PIECES)&~(FILEA);
        aMove = PAWN_PROMOTION_RIGHT&(~PAWN_PROMOTION_RIGHT-1);

        while(aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8 - 1) + (i/8) + (i % 8) + "P";
            PAWN_PROMOTION_RIGHT = PAWN_PROMOTION_RIGHT & ~aMove;
            aMove = PAWN_PROMOTION_RIGHT & (~PAWN_PROMOTION_RIGHT - 1);
        }
        // All legal moves for capturing to the left with promotion.
        long PAWN_PROMOTION_LEFT = (WP>>9)&(RANK8)&(BLACK_PIECES)&~(FILEH);
        aMove = PAWN_PROMOTION_LEFT&(~PAWN_PROMOTION_LEFT-1);

        while(aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8 + 1) + (i/8) + (i % 8) + "P";
            PAWN_PROMOTION_LEFT = PAWN_PROMOTION_LEFT & ~aMove;
            aMove = PAWN_PROMOTION_LEFT & (~PAWN_PROMOTION_LEFT - 1);
        }

        // En Passants:

        if (history.length() >= 16){ // At least 4 plys have been made (There are no positions before 4 plys have been
                                     // made where an en passant is possible.)
            if ((history.charAt(history.length()-1) == history.charAt(history.length()-3) &&
                    Math.abs(history.charAt(history.length()-2)-history.charAt(history.length()-4))==2)){
                // Checks that the last move was a 2 square move on the same file

                // The file of the last move is used to check that only the pawn that moved on the last move is
                // available to capture. See En_Passant_case.png
                int file = history.charAt(history.length()-1);

                long EN_PASSANT_RIGHT = (WP<<1)&BP&~FILEA&(FILES[file]); // Bitboard of the pawn to be captured

                // Since only one en passant is ever possible we don't loop over EN_PASSANT_RIGHT but just check if
                // there is a move.
                if (EN_PASSANT_RIGHT != 0){
                    int i = Long.numberOfLeadingZeros(EN_PASSANT_RIGHT);
                    allWPMoves += (i/8) + (i%8 - 1) + (i/8 - 1) + (i%8) + "E";

                }

                long EN_PASSANT_LEFT = (WP>>1)&BP&~FILEH&(FILES[file]);
                if (EN_PASSANT_LEFT != 0){
                    int i = Long.numberOfLeadingZeros(EN_PASSANT_LEFT);
                    allWPMoves += (i/8) + (i%8 + 1) + (i/8 - 1) + (i%8) + "E";
                }
            }
        }
        return allWPMoves;
    }

    public static String possibleWNMoves(long WN){

        return "";
    }

}
