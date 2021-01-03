package src;

import java.util.List;

public class Moves {
    static long CAPTURABLE;
    static long BLACK_PIECES;
    static long EMPTY;
    static long OCCUPIED;       // All the pieces on the board
    static long KING_SPAN = 460039L; //"0000000000000000000000000000000000000000000001110000010100000111"
    static long KNIGHT_SPAN = 43234889994L; //"0000000000000000000000000000101000010001000000000001000100001010"
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

    // All diagonals. Top left to bottom right
    static long[] DIAGONALS = {1L, 258L, 66052L, 16909320L, 4328785936L, 1108169199648L, 283691315109952L,
                               72624976668147840L, 145249953336295424L, 290499906672525312L, 580999813328273408L,
                               1161999622361579520L, 2323998145211531264L, 4647714815446351872L, -9223372036854775808L};

    // All anti-diagonals. Top right to bottom left
    static long[] ANTI_DIAGONALS = {128L, 32832L, 8405024L, 2151686160L, 550831656968L, 141012904183812L,
                                    36099303471055874L, -9205322385119247871L, 4620710844295151872L,
                                    2310355422147575808L, 1155177711073755136L, 577588855528488960L,
                                    288794425616760832L, 144396663052566528L, 72057594037927936L};

    static long HVMoves(int s){
        // Getting the bitboard containing only the slider.
        long SLIDER = 1L<<s;
        // See Hyperbola Quintessence in concepts.txt for a full explanation for the following code
        //(o-2r)^(o'-2r')'
        long horizontalMoves = ((OCCUPIED-2*SLIDER))^Long.reverse((Long.reverse(OCCUPIED)-2*(Long.reverse(SLIDER))));
        long verticalMoves = ((OCCUPIED&FILES[s/8])-2*SLIDER)^Long.reverse((Long.reverse(OCCUPIED&FILES[s/8])-2*(Long.reverse(SLIDER))));

        return horizontalMoves^verticalMoves&FILES[s/8];
    }

    static long DADMoves(int s){
        long SLIDER = 1L<<s;
        // s/8 + s%8 and s/8 + 7 - s%8 finds the correct Diagonal and anti-diagonal bitboards respectively.
        long diagonalMoves = ((OCCUPIED&DIAGONALS[s/8 + s%8])-2*SLIDER)^Long.reverse((Long.reverse(OCCUPIED&DIAGONALS[s/8 + s%8])-2*(Long.reverse(SLIDER))));
        long antiDiagonalMoves = ((OCCUPIED&ANTI_DIAGONALS[s/8 + s%8])-2*SLIDER)^Long.reverse((Long.reverse(OCCUPIED&ANTI_DIAGONALS[s/8 + 7 - s%8])-2*(Long.reverse(SLIDER))));

        return diagonalMoves&DIAGONALS[s/8 + s%8]^antiDiagonalMoves&ANTI_DIAGONALS[s/8 + 7 - s%8];
    }


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

        String allWhiteMoves = possibleWPMoves(WP, BP, history) + possibleWBMoves(OCCUPIED, WB) +
                possibleWRMoves(OCCUPIED, WR) + possibleWQMoves(OCCUPIED, WQ);

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

        long aMove = PAWN_RIGHT&~(PAWN_RIGHT-1);

        while(aMove != 0){

            // Getting the square of the move.
            int i = Long.numberOfTrailingZeros(aMove);
            // Recording the move with the starting and ending coordinates.
            allWPMoves += (i/8 + 1) + (i%8 - 1) + (i/8) + (i%8);

            // Removing the move from the total move bitboard.
            PAWN_RIGHT = PAWN_RIGHT&~aMove;

            // Doing the same thing as before but we move onto the next move.
            aMove = PAWN_RIGHT&~(PAWN_RIGHT-1);
        }
        // All legal moves for a white pawn capturing to the left.
        long PAWN_LEFT = (WP>>9)&(BLACK_PIECES)&~(RANK8)&~(FILEH);
        aMove = PAWN_LEFT&~(PAWN_LEFT-1);
        while(aMove != 0){
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8 + 1) + (i/8) + (i % 8);
            PAWN_LEFT = PAWN_LEFT&~aMove;
            aMove = PAWN_LEFT&~(PAWN_LEFT-1);
        }
        // All legal moves for pushing a white pawn 1 square.
        long PAWN_PUSH = (WP>>8)&(EMPTY)&~(RANK8);
        aMove = PAWN_PUSH&~(PAWN_PUSH-1);

        while(aMove != 0){
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8) + (i/8) + (i % 8);
            PAWN_PUSH = PAWN_PUSH&~aMove;
            aMove = PAWN_PUSH&~(PAWN_PUSH-1);
        }
        // All legal moves for pushing a white pawn 2 squares. (this can only happen when the pawn is on the 2nd rank)
        long PAWN_LEAP = (WP>>16)&(EMPTY)&(EMPTY>>8)&(RANK4);
        aMove = PAWN_LEAP&~(PAWN_LEAP-1);

        while(aMove != 0){
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 2) + (i%8) + (i/8) + (i % 8);
            PAWN_LEAP = PAWN_LEAP&~aMove;
            aMove = PAWN_LEAP&~(PAWN_LEAP-1);
        }

        // Promotions:
        // For now promotion is indicated with a "P" at the end

        // All legal moves for pushing a white pawn 1 square with promotion.
        long PAWN_PROMOTION_FORWARD = (WP>>8)&(RANK8)&(EMPTY);
        aMove = PAWN_PROMOTION_FORWARD&~(PAWN_PROMOTION_FORWARD-1);

        while(aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8) + "P";
            PAWN_PROMOTION_FORWARD = PAWN_PROMOTION_FORWARD & ~aMove;
            aMove = PAWN_PROMOTION_FORWARD & ~(PAWN_PROMOTION_FORWARD - 1);
        }
        // All legal moves for capturing to the right with promotion.
        long PAWN_PROMOTION_RIGHT = (WP>>7)&(RANK8)&(BLACK_PIECES)&~(FILEA);
        aMove = PAWN_PROMOTION_RIGHT&~(PAWN_PROMOTION_RIGHT-1);

        while(aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8 - 1) + (i/8) + (i % 8) + "P";
            PAWN_PROMOTION_RIGHT = PAWN_PROMOTION_RIGHT & ~aMove;
            aMove = PAWN_PROMOTION_RIGHT & ~(PAWN_PROMOTION_RIGHT - 1);
        }
        // All legal moves for capturing to the left with promotion.
        long PAWN_PROMOTION_LEFT = (WP>>9)&(RANK8)&(BLACK_PIECES)&~(FILEH);
        aMove = PAWN_PROMOTION_LEFT&~(PAWN_PROMOTION_LEFT-1);

        while(aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += (i/8 + 1) + (i%8 + 1) + (i/8) + (i % 8) + "P";
            PAWN_PROMOTION_LEFT = PAWN_PROMOTION_LEFT & ~aMove;
            aMove = PAWN_PROMOTION_LEFT & ~(PAWN_PROMOTION_LEFT - 1);
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

    public static String possibleWBMoves(long OCCUPIED, long WB){
        String allWBMoves = "";
        // Getting the bitboard of one bishop
        long aBishop = WB&~(WB-1);
        long bishopMoves;

        // Looping through every bishop on the board
        while (aBishop != 0){
            int bishopLocation = Long.numberOfTrailingZeros(aBishop);
            // Getting all legal bishop moves. DADMoves() returns a bitboard of moves including every piece so we must
            // & with the bitboard of capturable pieces
            bishopMoves = DADMoves(bishopLocation)&CAPTURABLE;
            // Getting the first bishop move
            long aMove = bishopMoves&~(bishopMoves-1);
            // Looping through every bishop move
            while (aMove != 0){
                // Getting the square of the move
                int i = Long.numberOfTrailingZeros(aMove);
                // Adding the move to the list.
                allWBMoves += (bishopLocation/8) + (bishopLocation%8) + (i/8) + (i%8);
                // Removing the move from the bitboard of all moves
                bishopMoves = bishopMoves&(~aMove);
                // Getting the next move
                aMove = bishopMoves&~(bishopMoves-1);
            }
            // Removing the bishop. Since long is a primitive type this won't affect WB outside of this function.
            WB = WB&~(aBishop);
            // Getting the next bishop.
            aBishop = WB&~(WB-1);
        }
        return allWBMoves;
    }

    public static String possibleWRMoves(long OCCUPIED, long WR){
        String allWRMoves = "";
        long aRook = WR&~(WR-1);
        long rookMoves;

        while (aRook != 0){
            int rookLocation = Long.numberOfTrailingZeros(aRook);
            // Same as bishop but with HVMoves() instead of DADMoves()
            rookMoves = HVMoves(rookLocation)&CAPTURABLE;
            long aMove = rookMoves&~(rookMoves-1);
            while (aMove != 0){
                int i = Long.numberOfTrailingZeros(aMove);
                allWRMoves += (rookLocation/8) + (rookLocation%8) + (i/8) + (i%8);
                rookMoves = rookMoves&(~aMove);
                aMove = rookMoves&~(rookMoves-1);
            }
            WR = WR&~(aRook);
            aRook = WR&~(WR-1);
        }
        return allWRMoves;
    }

    public static String possibleWQMoves(long OCCUPIED, long WQ){
        String allWQMoves = "";
        long aQueen = WQ&~(WQ-1);
        long queenMoves;

        while (aQueen != 0){
            int queenLocation = Long.numberOfTrailingZeros(aQueen);
            // We now use both HVMoves() and DADMoves()
            queenMoves = (HVMoves(queenLocation)|DADMoves(queenLocation))&CAPTURABLE;
            long aMove = queenMoves&~(queenMoves-1);
            while (aMove != 0){
                int i = Long.numberOfTrailingZeros(aMove);
                allWQMoves += (queenLocation/8) + (queenLocation%8) + (i/8) + (i%8);
                queenMoves = queenMoves&(~aMove);
                aMove = queenMoves&~(queenMoves-1);
            }
            WQ = WQ&~(aQueen);
            aQueen = WQ&~(WQ-1);
        }
        return allWQMoves;
    }


    public static String possibleWNMoves(long OCCUPIED, long WN){
        String allWNMoves = "";
        long aKnight = WN&~(WN-1);
        long knightMoves= 0L;

        while (aKnight != 0){
            int knightLocation = Long.numberOfTrailingZeros(aKnight);
            // Note: KNIGHT_SPAN is the span of a knight on square 18. 18 is chosen because it is the smallest square
            // that the knight can be on while still showing all 8 moves.
            if (knightLocation > 18){
                // This means the knight is "in front of the span knight so we must left shift by the difference between
                // the span knight and the actual knight
                knightMoves = KNIGHT_SPAN<<(knightLocation-18);
            }else{
                // This means the knight is "behind" the span knight so we right shift by 18 - location
                knightMoves = KNIGHT_SPAN>>(18 - knightLocation);
            }

            // Depending on where the knight is, shifting KNIGHT_SPAN may result in invalid moves. This is to remove those invalid moves
            if (knightLocation%8 < 4){
                // When the knight is on rank 1-4 all invalid moves from shifting will be on the GH files.
                knightMoves = knightMoves&CAPTURABLE&~FILES[6]&~FILEH;
            }else{
                // When the knight is on rank 4-8 all invalid moves from shifting will be on the AB files.
                knightMoves = knightMoves&CAPTURABLE&~FILEA&~FILES[1];
            }
            // Same as every other piece
            long aMove = knightMoves&~(knightMoves-1);
            while (aMove != 0){
                int i = Long.numberOfTrailingZeros(aMove);
                allWNMoves += (knightLocation/8) + (knightLocation%8) + (i/8) + (i%8);
                knightMoves = knightMoves&(~aMove);
                aMove = knightMoves&~(knightMoves-1);
            }
            WN = WN&~(aKnight);
            aKnight = WN&~(WN-1);
        }
        return allWNMoves;

    }

    public static String possibleWKMoves(long OCCUPIED, long WK){
        String allWKMoves = "";
        long aKing = WK&~(WK-1);
        long kingMoves= 0L;

        while (aKing != 0){
            int kingLocation = Long.numberOfTrailingZeros(aKing);
            // Note: KING_SPAN is the span of a king on square 9. 9 is chosen because it is the smallest square
            // that the king can be on while still showing all 8 moves.

            // Exactly the same as possibleWNMoves() but with KING_SPAN instead.
            if (kingLocation > 18){
                kingMoves = KING_SPAN<<(kingLocation-9);
            }else{
                kingMoves = KING_SPAN>>(9 - kingLocation);
            }
            if (kingLocation%8 < 4){
                kingMoves = kingMoves&CAPTURABLE&~FILES[6]&~FILEH;
            }else{
                kingMoves = kingMoves&CAPTURABLE&~FILEA&~FILES[1];
            }

            long aMove = kingMoves&~(kingMoves-1);
            while (aMove != 0){
                int i = Long.numberOfTrailingZeros(aMove);
                allWKMoves += (kingLocation/8) + (kingLocation%8) + (i/8) + (i%8);
                kingMoves = kingMoves&(~aMove);
                aMove = kingMoves&~(kingMoves-1);
            }
            WK = WK&~(aKing);
            aKing = WK&~(WK-1);
        }
        return allWKMoves;

    }

    public static long unsafeForWhite(long[] allBitBoards){
        long WP = allBitBoards[0], WB = allBitBoards[1], WN = allBitBoards[2], WR = allBitBoards[3],
                WQ = allBitBoards[4], WK = allBitBoards[5], BP =allBitBoards[6], BB =allBitBoards[7],
                BN = allBitBoards[8], BR = allBitBoards[9], BQ = allBitBoards[10], BK = allBitBoards[11];

        



    }




}



