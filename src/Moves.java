package src;

import java.util.Optional;

public class Moves {
    static boolean wkcastle = true, wqcastle = true, bkcastle = true, bqcastle = true; // King side/queen side castling booleans
    static long CAPTURABLE;
    static long WHITE_PIECES;
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

    // Ranks 8 to 1
    static long[] RANKS = {255L, 65280L, 16711680L, 4278190080L, 1095216660480L, 280375465082880L, 71776119061217280L,-72057594037927936L};

    // All diagonals. Top left to bottom right
    static long[] DIAGONALS = {1L, 258L, 66052L, 16909320L, 4328785936L, 1108169199648L, 283691315109952L,
            72624976668147840L, 145249953336295424L, 290499906672525312L, 580999813328273408L,
            1161999622361579520L, 2323998145211531264L, 4647714815446351872L, -9223372036854775808L};

    // All anti-diagonals. Top right to bottom left
    static long[] ANTI_DIAGONALS = {128L, 32832L, 8405024L, 2151686160L, 550831656968L, 141012904183812L,
            36099303471055874L, -9205322385119247871L, 4620710844295151872L,
            2310355422147575808L, 1155177711073755136L, 577588855528488960L,
            288794425616760832L, 144396663052566528L, 72057594037927936L};

    /** Makes the move for the specified piece.
     *
     *
     * @param board Bitboard of piece
     * @param move 4 character chess move
     * @param piece Character representing a type of piece
     */
    public static long makeMove(long board, String move, char piece){

        if (move.charAt(3) == 'P'){
            // Promotion
            int start, end;
            // Different cases for white and black promotion. Recall: Promotion move format: initial y + final y + Piece to promote to + P
            if (Character.isUpperCase(move.charAt(2))){
                // White pawn promotion

                // Getting the square number of the initial and final squares
                start = Long.numberOfTrailingZeros(FILES[move.charAt(0)]&RANKS[1]);
                end = Long.numberOfTrailingZeros(FILES[move.charAt(1)]&RANKS[0]);
            } else{
                // Black pawn promotion

                // Getting the square number of the initial and final squares
                start = Long.numberOfTrailingZeros(FILES[move.charAt(0)]&RANKS[6]);
                end = Long.numberOfTrailingZeros(FILES[move.charAt(1)]&RANKS[7]);
            }
            // Check if the move is for the piece that board represents.
            if (((board>>start)&1) == 1){
                // Removing the piece at the initial square from the board
                board = board & ~(1L<<start);
            } else {
                // Removing the captured piece from the final square if there is one.
                board = board & ~(1L<<end);
            }
            // Adding the promoted piece to the board at the final square
            if (move.charAt(2) == piece) {
                board = board | (1L << end);
            }
        } else if (move.charAt(3) == 'E'){
            // En Passant
            // Recall: En Passant move format: initial y of piece capturing + y of piece being captured + W + E
                    int start, end, captured;
                    if (move.charAt(2) == 'W'){
                        // White en passant
                        // Getting the square number of the initial square and final square for the pawn capturing
                        start = Long.numberOfTrailingZeros(FILES[Character.getNumericValue(move.charAt(0))]&RANKS[3]);
                        end = Long.numberOfTrailingZeros(FILES[Character.getNumericValue(move.charAt(1))]&RANKS[2]);
                        // Getting the square number of the captured piece
                        captured = Long.numberOfTrailingZeros(FILES[Character.getNumericValue(move.charAt(1))]&RANKS[3]);
                    } else{
                        start = Long.numberOfTrailingZeros(FILES[Character.getNumericValue(move.charAt(0))]&RANKS[4]);
                        end = Long.numberOfTrailingZeros(FILES[Character.getNumericValue(move.charAt(1))]&RANKS[5]);
                        captured = Long.numberOfTrailingZeros(FILES[Character.getNumericValue(move.charAt(1))]&RANKS[4]);
                    }
                    if (((board>>start)&1) == 1){
                        // Removing the piece at the initial position from the board
                        board = board & ~(1L<<start);
                        // Adding the piece to the board at the final position.
                        board = board |(1L<<end);
                    } else{
                        board = board & ~(1L<<captured);
                    }
        } else{
            // Regular Move

            // Getting the square number of the initial and final squares
            int start = (Character.getNumericValue(move.charAt(0))*8) + (Character.getNumericValue(move.charAt(1)));
            int end = (Character.getNumericValue(move.charAt(2))*8) + (Character.getNumericValue(move.charAt(3)));

            // Check if the move is for the piece that board represents. (In other words check whether there is a piece
            // on board at square start)
            if (((board>>start)&1) == 1){
                // Removing the piece at the initial square from the board
                board = board & ~(1L<<start);
                // Adding the piece to the board at the final square. Note: When we are capturing(There is a piece
                // on the end square) we will not remove it now but when we call makeMove() on the piece getting captured
                board = board |(1L<<end);
            } else{
                // Here we know that the move is not being made by the piece that board represents. However we need to
                // check if this piece is being captured. To do this we check if the end square for the board has a 1 then remove it using &~
                board = board & ~(1L<<end);
            }

        }
        return board;
    }

    /** Returns the horizontal and vertical moves of the piece at square s taking into account blocking pieces.
     *
     * @param s the position of the piece on the board(square number)
     * @return bitboard of horizontal and vertical moves
     */
    static long HVMoves(int s) {
        // Getting the bitboard containing only the slider.
        long SLIDER = 1L << s;
        // See Hyperbola Quintessence in concepts.txt for a full explanation for the following code
        //(o-2r)^(o'-2r')'
        long horizontalMoves = ((OCCUPIED - 2 * SLIDER)) ^ Long.reverse((Long.reverse(OCCUPIED) - 2 * (Long.reverse(SLIDER))));
        long verticalMoves = ((OCCUPIED & FILES[s % 8]) - 2 * SLIDER) ^ Long.reverse((Long.reverse(OCCUPIED & FILES[s % 8]) - 2 * (Long.reverse(SLIDER))));

        return (horizontalMoves&RANKS[s/8]) | (verticalMoves & FILES[s % 8]);
    }

    /** Returns the diagonal an anti-diagonal moves of the piece at square s taking into account blocking pieces.
     *
     * @param s the position of the piece on the board(square number)
     * @return bitboard of diagonal and anti-diagonal moves
     */
    static long DADMoves(int s) {
        long SLIDER = 1L << s;
        // s/8 + s%8 and s/8 + 7 - s%8 finds the correct Diagonal and anti-diagonal bitboards respectively.
        long diagonalMoves = ((OCCUPIED & DIAGONALS[s / 8 + s % 8]) - 2 * SLIDER) ^ Long.reverse((Long.reverse(OCCUPIED & DIAGONALS[s / 8 + s % 8]) - 2 * (Long.reverse(SLIDER))));
        long antiDiagonalMoves = ((OCCUPIED & ANTI_DIAGONALS[s / 8 + 7 - s % 8]) - 2 * SLIDER) ^ Long.reverse((Long.reverse(OCCUPIED & ANTI_DIAGONALS[s / 8 + 7 - s % 8]) - 2 * (Long.reverse(SLIDER))));

        return (diagonalMoves & DIAGONALS[s / 8 + s % 8]) | (antiDiagonalMoves & ANTI_DIAGONALS[s / 8 + 7 - s % 8]);
    }

    /** Returns all possible moves for white for the current board state. Moves are always 4 characters long.
     *
     * @param allBitBoards List of bitboards for each piece
     * @return String of all possible moves for white
     */
    public static String possibleWhiteMoves(long[] allBitBoards, String history) {
        long WP = allBitBoards[0], WB = allBitBoards[1], WN = allBitBoards[2], WR = allBitBoards[3],
                WQ = allBitBoards[4], WK = allBitBoards[5], BP = allBitBoards[6], BB = allBitBoards[7],
                BN = allBitBoards[8], BR = allBitBoards[9], BQ = allBitBoards[10], BK = allBitBoards[11];

        // Bitboard of pieces that white can capture.
        CAPTURABLE = ~(WP | WB | WN | WR | WQ | WK | BK);
        // Bitboard of white pieces
        WHITE_PIECES = WP | WB | WN | WR | WQ;
        // Bitboard of black pieces
        BLACK_PIECES = BP | BB | BN | BR | BQ;
        // Bitboard of occupied squares
        OCCUPIED = WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK;
        // Bitboard of empty squares;
        EMPTY = ~(OCCUPIED);

        // Note: Moves are in this format: startingx startingy endingx endingy (no spaces, commas, etc.)


        String allWhiteMoves = possibleWPMoves(WP, BP, history) + possibleBishopMoves(OCCUPIED, WB) + possibleKnightMoves(OCCUPIED, WN) +
                possibleRookMoves(OCCUPIED, WR) + possibleQueenMoves(OCCUPIED, WQ) + possibleKingMoves(OCCUPIED, WK) + whiteCastling();

        return allWhiteMoves;

    }

    /** Returns all possible moves for black for the current board state. Moves are always 4 characters long.
     *
     * @param allBitBoards List of bitboards for each piece
     * @return String of all possible moves for black
     */
    public static String possibleBlackMoves(long[] allBitBoards, String history) {
        long WP = allBitBoards[0], WB = allBitBoards[1], WN = allBitBoards[2], WR = allBitBoards[3],
                WQ = allBitBoards[4], WK = allBitBoards[5], BP = allBitBoards[6], BB = allBitBoards[7],
                BN = allBitBoards[8], BR = allBitBoards[9], BQ = allBitBoards[10], BK = allBitBoards[11];

        // Bitboard of pieces that black can capture.
        CAPTURABLE = ~(BP | BB | BN | BR | BQ | BK | WK);
        // Bitboard of white pieces
        WHITE_PIECES = WP | WB | WN | WR | WQ;
        // Bitboard of black pieces
        BLACK_PIECES = BP | BB | BN | BR | BQ;
        // Bitboard of occupied squares
        OCCUPIED = WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK;
        // Bitboard of empty squares;
        EMPTY = ~(OCCUPIED);

        String allBlackMoves = possibleBPMoves(BP, WP, history) + possibleBishopMoves(OCCUPIED, BB) + possibleKnightMoves(OCCUPIED, BN) +
                possibleRookMoves(OCCUPIED, BR) + possibleQueenMoves(OCCUPIED, BQ) + possibleKingMoves(OCCUPIED, BK) + blackCastling();

        return allBlackMoves;
    }

    /** Returns all possible moves for white pawns.
     *
     * @param WP Bitboard of white pawns
     * @param BP Bitboard of black pawns
     * @param history All past moves for the current game
     * @return String of all possible moves for white pawns
     */
    public static String possibleWPMoves(long WP, long BP, String history) {
        String allWPMoves = "";

        // All legal moves for a white pawn capturing to the right.
        long PAWN_RIGHT = (WP >> 7) & (BLACK_PIECES) & ~(RANK8) & ~(FILEA);

        // Obtaining the bitboard containing only the first move. Note: (PAWN_RIGHT-1) is the bitboard with only the
        // first move as 0 while the rest of the moves are 1.
        // So ~(PAWN_RIGHT-1) is the bitboard with only the first move as 1 with the rest of the moves as 0.
        // Then PAWN_RIGHT&(~PAWN_RIGHT-1) would be only the first move.

        // EXAMPLE: m = 0010100      m - 1 = 0010011     ~(m-1)= 1101100     m&~(m-1) = 000100 only the first move is 1.

        long aMove = PAWN_RIGHT & ~(PAWN_RIGHT - 1);

        while (aMove != 0) {

            // Getting the square of the move.
            int i = Long.numberOfTrailingZeros(aMove);
            // Recording the move with the starting and ending coordinates.
            allWPMoves += "" + (i / 8 + 1) + (i % 8 - 1) + (i / 8) + (i % 8);

            // Removing the move from the total move bitboard.
            PAWN_RIGHT = PAWN_RIGHT & ~aMove;

            // Doing the same thing as before but we move onto the next move.
            aMove = PAWN_RIGHT & ~(PAWN_RIGHT - 1);
        }
        // All legal moves for a white pawn capturing to the left.
        long PAWN_LEFT = (WP >> 9) & (BLACK_PIECES) & ~(RANK8) & ~(FILEH);
        aMove = PAWN_LEFT & ~(PAWN_LEFT - 1);
        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += "" + (i / 8 + 1) + (i % 8 + 1) + (i / 8) + (i % 8);
            PAWN_LEFT = PAWN_LEFT & ~aMove;
            aMove = PAWN_LEFT & ~(PAWN_LEFT - 1);
        }
        // All legal moves for pushing a white pawn 1 square.
        long PAWN_PUSH = (WP >> 8) & (EMPTY) & ~(RANK8);
        aMove = PAWN_PUSH & ~(PAWN_PUSH - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += "" + (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8);
            PAWN_PUSH = PAWN_PUSH & ~aMove;
            aMove = PAWN_PUSH & ~(PAWN_PUSH - 1);
        }
        // All legal moves for pushing a white pawn 2 squares. (this can only happen when the pawn is on the 2nd rank)
        long PAWN_LEAP = (WP >> 16) & (EMPTY) & (EMPTY >> 8) & (RANK4);
        aMove = PAWN_LEAP & ~(PAWN_LEAP - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += "" + (i / 8 + 2) + (i % 8) + (i / 8) + (i % 8);
            PAWN_LEAP = PAWN_LEAP & ~aMove;
            aMove = PAWN_LEAP & ~(PAWN_LEAP - 1);
        }

        // Promotions:
        // The move format for promotions are: initial y + final y + Piece to promote to + P

        // All legal moves for pushing a white pawn 1 square with promotion.
        long PAWN_PROMOTION_FORWARD = (WP >> 8) & (RANK8) & (EMPTY);
        aMove = PAWN_PROMOTION_FORWARD & ~(PAWN_PROMOTION_FORWARD - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += "" + (i % 8) + (i % 8) + "BP" + (i % 8) + (i % 8) + "NP" + (i % 8) + (i % 8) + "RP" + (i % 8) + (i % 8) + "QP";
            PAWN_PROMOTION_FORWARD = PAWN_PROMOTION_FORWARD & ~aMove;
            aMove = PAWN_PROMOTION_FORWARD & ~(PAWN_PROMOTION_FORWARD - 1);
        }
        // All legal moves for capturing to the right with promotion.
        long PAWN_PROMOTION_RIGHT = (WP >> 7) & (RANK8) & (BLACK_PIECES) & ~(FILEA);
        aMove = PAWN_PROMOTION_RIGHT & ~(PAWN_PROMOTION_RIGHT - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += "" + (i % 8 - 1) + (i % 8) + "BP" + (i % 8 - 1) + (i % 8) + "NP" + (i % 8 - 1) + (i % 8) + "RP" + (i % 8 - 1) + (i % 8) + "QP";
            PAWN_PROMOTION_RIGHT = PAWN_PROMOTION_RIGHT & ~aMove;
            aMove = PAWN_PROMOTION_RIGHT & ~(PAWN_PROMOTION_RIGHT - 1);
        }
        // All legal moves for capturing to the left with promotion.
        long PAWN_PROMOTION_LEFT = (WP >> 9) & (RANK8) & (BLACK_PIECES) & ~(FILEH);
        aMove = PAWN_PROMOTION_LEFT & ~(PAWN_PROMOTION_LEFT - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allWPMoves += "" + (i % 8 + 1) + (i % 8) + "BP" + (i % 8 + 1) + (i % 8) + "NP" + (i % 8 + 1) + (i % 8) + "RP" + (i % 8 + 1) + (i % 8) + "QP";
            PAWN_PROMOTION_LEFT = PAWN_PROMOTION_LEFT & ~aMove;
            aMove = PAWN_PROMOTION_LEFT & ~(PAWN_PROMOTION_LEFT - 1);
        }

        // En Passants:
        // The move format for En Passants: initial y of piece capturing + y of piece being captured + W + E

        if (history.length() >= 4) { // A move must have been made previously

            char initialx = history.charAt(history.length() - 4);
            char initialy = history.charAt(history.length() - 3);
            char finalx = history.charAt(history.length() - 2);
            char finaly = history.charAt(history.length() - 1);

            // Checks that the last move was a 2 square move on the same file
            if (initialy == finaly && Math.abs(finalx - initialx) == 2) {

                //Getting the square number of the ending square of the last move
                int end = Character.getNumericValue(finalx) * 8 + Character.getNumericValue(finaly);
                // Checking that the last move was by a black pawn
                if (((BP >> end) & 1) == 1) {
                    // The file of the last move is used to check that only the pawn that moved on the last move is
                    // available to capture. See En_Passant_case.png
                    int file = Character.getNumericValue(history.charAt(history.length() - 1));

                    long EN_PASSANT_RIGHT = (WP << 1) & BP & ~FILEA & FILES[file] & RANK5; // Bitboard of the pawn to be captured

                    // Since only one en passant is ever possible we don't loop over EN_PASSANT_RIGHT but just check if
                    // there is a move.
                    if (EN_PASSANT_RIGHT != 0) {
                        int i = Long.numberOfTrailingZeros(EN_PASSANT_RIGHT);
                        allWPMoves += "" + (i % 8 - 1) + (i % 8) + "WE";

                    }

                    long EN_PASSANT_LEFT = (WP >> 1) & BP & ~FILEH & (FILES[file]) & RANK5;
                    if (EN_PASSANT_LEFT != 0) {
                        int i = Long.numberOfTrailingZeros(EN_PASSANT_LEFT);
                        allWPMoves += "" + (i % 8 + 1) + (i % 8) + "WE";
                    }
                }
            }
        }
        return allWPMoves;
    }

    /** Returns all possible moves for black pawns.
     *
     * @param BP Bitboard of black pawns
     * @param WP Bitboard of white pawns
     * @param history All past moves for the current game
     * @return String of all possible moves for black pawns
     */
    public static String possibleBPMoves(long BP, long WP, String history) {
        String allBPMoves = "";
        // All legal moves for a black pawn capturing to the right.
        long PAWN_RIGHT = (BP << 7) & (WHITE_PIECES) & ~(RANK1) & ~(FILEH);

        long aMove = PAWN_RIGHT & ~(PAWN_RIGHT - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allBPMoves += "" + (i / 8 - 1) + (i % 8 + 1) + (i / 8) + (i % 8);
            PAWN_RIGHT = PAWN_RIGHT & ~aMove;
            aMove = PAWN_RIGHT & ~(PAWN_RIGHT - 1);
        }
        // All legal moves for a black pawn capturing to the left.
        long PAWN_LEFT = (BP << 9) & (WHITE_PIECES) & ~(RANK1) & ~(FILEA);
        aMove = PAWN_LEFT & ~(PAWN_LEFT - 1);
        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allBPMoves += "" + (i / 8 - 1) + (i % 8 - 1) + (i / 8) + (i % 8);
            PAWN_LEFT = PAWN_LEFT & ~aMove;
            aMove = PAWN_LEFT & ~(PAWN_LEFT - 1);
        }
        // All legal moves for pushing a white pawn 1 square.
        long PAWN_PUSH = (BP << 8) & (EMPTY) & ~(RANK1);
        aMove = PAWN_PUSH & ~(PAWN_PUSH - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allBPMoves += "" + (i / 8 - 1) + (i % 8) + (i / 8) + (i % 8);
            PAWN_PUSH = PAWN_PUSH & ~aMove;
            aMove = PAWN_PUSH & ~(PAWN_PUSH - 1);
        }
        // All legal moves for pushing a white pawn 2 squares. (this can only happen when the pawn is on the 7th rank)
        long PAWN_LEAP = (BP << 16) & (EMPTY) & (EMPTY << 8) & (RANK5);
        aMove = PAWN_LEAP & ~(PAWN_LEAP - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allBPMoves += "" + (i / 8 - 2) + (i % 8) + (i / 8) + (i % 8);
            PAWN_LEAP = PAWN_LEAP & ~aMove;
            aMove = PAWN_LEAP & ~(PAWN_LEAP - 1);
        }

        // Promotions:
        // All legal moves for pushing a white pawn 1 square with promotion.
        long PAWN_PROMOTION_FORWARD = (BP << 8) & (RANK1) & (EMPTY);
        aMove = PAWN_PROMOTION_FORWARD & ~(PAWN_PROMOTION_FORWARD - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allBPMoves += "" + (i % 8) + (i % 8) + "bP" + (i % 8) + (i % 8) + "nP" + (i % 8) + (i % 8) + "rP" + (i % 8) + (i % 8) + "qP";
            PAWN_PROMOTION_FORWARD = PAWN_PROMOTION_FORWARD & ~aMove;
            aMove = PAWN_PROMOTION_FORWARD & ~(PAWN_PROMOTION_FORWARD - 1);
        }
        // All legal moves for capturing to the right with promotion.
        long PAWN_PROMOTION_RIGHT = (BP << 7) & (RANK1) & (WHITE_PIECES) & ~(FILEH);
        aMove = PAWN_PROMOTION_RIGHT & ~(PAWN_PROMOTION_RIGHT - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allBPMoves += "" + (i % 8 + 1) + (i % 8) + "bP" + (i % 8 + 1) + (i % 8) + "nP" + (i % 8 + 1) + (i % 8) + "rP" + (i % 8 + 1) + (i % 8) + "qP";
            PAWN_PROMOTION_RIGHT = PAWN_PROMOTION_RIGHT & ~aMove;
            aMove = PAWN_PROMOTION_RIGHT & ~(PAWN_PROMOTION_RIGHT - 1);
        }
        // All legal moves for capturing to the left with promotion.
        long PAWN_PROMOTION_LEFT = (BP << 9) & (RANK1) & (WHITE_PIECES) & ~(FILEA);
        aMove = PAWN_PROMOTION_LEFT & ~(PAWN_PROMOTION_LEFT - 1);

        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allBPMoves += "" + (i % 8 - 1) + (i % 8) + "bP" + (i % 8 - 1) + (i % 8) + "nP" + (i % 8 - 1) + (i % 8) + "rP" + (i % 8 - 1) + (i % 8) + "qP";
            PAWN_PROMOTION_LEFT = PAWN_PROMOTION_LEFT & ~aMove;
            aMove = PAWN_PROMOTION_LEFT & ~(PAWN_PROMOTION_LEFT - 1);
        }

        // En Passants:

        if (history.length() >= 4) {
            char initialx = history.charAt(history.length() - 4);
            char initialy = history.charAt(history.length() - 3);
            char finalx = history.charAt(history.length() - 2);
            char finaly = history.charAt(history.length() - 1);

            if (initialy == finaly && Math.abs(finalx - initialx) == 2) {

                int end = Character.getNumericValue(finalx) * 8 + Character.getNumericValue(finaly);

                if (((WP >> end) & 1) == 1) {

                    int file = Character.getNumericValue(history.charAt(history.length() - 1));

                    long EN_PASSANT_RIGHT = (BP >> 1) & WP & ~FILEH & (FILES[file]) & RANK4; // Bitboard of the pawn to be captured

                    if (EN_PASSANT_RIGHT != 0) {
                        int i = Long.numberOfTrailingZeros(EN_PASSANT_RIGHT);
                        allBPMoves += "" + (i % 8 + 1) + (i % 8) + "BE";
                    }
                    long EN_PASSANT_LEFT = (BP << 1) & WP & ~FILEA & (FILES[file]) & RANK4;
                    if (EN_PASSANT_LEFT != 0) {
                        int i = Long.numberOfTrailingZeros(EN_PASSANT_LEFT);
                        allBPMoves += "" + (i % 8 - 1) + (i % 8) + "BE";
                    }
                }
            }
        }
        return allBPMoves;
    }

    /** Returns all possible bishop moves.
     *
     * @param OCCUPIED Bitboard of all occupied squares
     * @param bishops Bitboard of all bishops of a certain colour
     * @return String of all possible bishop moves
     */
    public static String possibleBishopMoves(long OCCUPIED, long bishops) {
        String allBishopMoves = "";
        // Getting the bitboard of one bishop
        long aBishop = bishops & ~(bishops - 1);
        long bishopMoves;

        // Looping through every bishop on the board
        while (aBishop != 0) {
            int bishopLocation = Long.numberOfTrailingZeros(aBishop);
            // Getting all legal bishop moves. DADMoves() returns a bitboard of moves including every piece so we must
            // & with the bitboard of capturable pieces
            bishopMoves = DADMoves(bishopLocation) & CAPTURABLE;
            // Getting the first bishop move
            long aMove = bishopMoves & ~(bishopMoves - 1);
            // Looping through every bishop move
            while (aMove != 0) {
                // Getting the square of the move
                int i = Long.numberOfTrailingZeros(aMove);
                // Adding the move to the list.
                allBishopMoves += "" + (bishopLocation / 8) + (bishopLocation % 8) + (i / 8) + (i % 8);
                // Removing the move from the bitboard of all moves
                bishopMoves = bishopMoves & (~aMove);
                // Getting the next move
                aMove = bishopMoves & ~(bishopMoves - 1);
            }
            // Removing the bishop. Since long is a primitive type this won't affect WB outside of this function.
            bishops = bishops & ~(aBishop);
            // Getting the next bishop.
            aBishop = bishops & ~(bishops - 1);
        }
        return allBishopMoves;
    }

    /** Returns all possible rook moves.
     *
     * @param OCCUPIED Bitboard of all occupied squares
     * @param rooks Bitboard of all rooks of a certain colour
     * @return String of all possible rook moves
     */
    public static String possibleRookMoves(long OCCUPIED, long rooks) {
        String allRookMoves = "";
        long aRook = rooks & ~(rooks - 1);
        long rookMoves;

        while (aRook != 0) {
            int rookLocation = Long.numberOfTrailingZeros(aRook);
            // Same as bishop but with HVMoves() instead of DADMoves()
            rookMoves = HVMoves(rookLocation) & CAPTURABLE;
            long aMove = rookMoves & ~(rookMoves - 1);
            while (aMove != 0) {
                int i = Long.numberOfTrailingZeros(aMove);
                allRookMoves += "" + (rookLocation / 8) + (rookLocation % 8) + (i / 8) + (i % 8);
                rookMoves = rookMoves & (~aMove);
                aMove = rookMoves & ~(rookMoves - 1);
            }
            rooks = rooks & ~(aRook);
            aRook = rooks & ~(rooks - 1);
        }
        return allRookMoves;
    }

    /** Returns all possible queen moves.
     *
     * @param OCCUPIED Bitboard of all occupied squares
     * @param queens Bitboard of all queens of a certain colour
     * @return String of all possible queen moves
     */
    public static String possibleQueenMoves(long OCCUPIED, long queens) {
        String allQueenMoves = "";
        long aQueen = queens & ~(queens - 1);
        long queenMoves;

        while (aQueen != 0) {
            int queenLocation = Long.numberOfTrailingZeros(aQueen);
            // We now use both HVMoves() and DADMoves()
            queenMoves = (HVMoves(queenLocation) | DADMoves(queenLocation)) & CAPTURABLE;
            long aMove = queenMoves & ~(queenMoves - 1);
            while (aMove != 0) {
                int i = Long.numberOfTrailingZeros(aMove);
                allQueenMoves += "" + (queenLocation / 8) + (queenLocation % 8) + (i / 8) + (i % 8);
                queenMoves = queenMoves & (~aMove);
                aMove = queenMoves & ~(queenMoves - 1);
            }
            queens = queens & ~(aQueen);
            aQueen = queens & ~(queens - 1);
        }
        return allQueenMoves;
    }

    /** Returns all possible knight moves.
     *
     * @param OCCUPIED Bitboard of all occupied squares
     * @param knights Bitboard of all knights of a certain colour
     * @return String of all possible knight moves
     */
    public static String possibleKnightMoves(long OCCUPIED, long knights) {
        String allKnightMoves = "";
        long aKnight = knights & ~(knights - 1);
        long knightMoves;

        while (aKnight != 0) {
            int knightLocation = Long.numberOfTrailingZeros(aKnight);
            // Note: KNIGHT_SPAN is the span of a knight on square 18. 18 is chosen because it is the smallest square
            // that the knight can be on while still showing all 8 moves.
            if (knightLocation > 18) {
                // This means the knight is "in front" of the span knight so we must left shift by the difference between
                // the span knight and the actual knight
                knightMoves = KNIGHT_SPAN << (knightLocation - 18);
            } else {
                // This means the knight is "behind" the span knight so we right shift by 18 - location
                knightMoves = KNIGHT_SPAN >> (18 - knightLocation);
            }

            // Depending on where the knight is, shifting KNIGHT_SPAN may result in invalid moves. This is to remove those invalid moves
            if (knightLocation % 8 < 4) {
                // When the knight is on rank 1-4 all invalid moves from shifting will be on the GH files.
                knightMoves = knightMoves & CAPTURABLE & ~FILES[6] & ~FILEH;
            } else {
                // When the knight is on rank 4-8 all invalid moves from shifting will be on the AB files.
                knightMoves = knightMoves & CAPTURABLE & ~FILEA & ~FILES[1];
            }
            // Same as every other piece
            long aMove = knightMoves & ~(knightMoves - 1);
            while (aMove != 0) {
                int i = Long.numberOfTrailingZeros(aMove);
                allKnightMoves += "" + (knightLocation/8) + (knightLocation%8) + (i/8) + (i%8);
                knightMoves = knightMoves & (~aMove);
                aMove = knightMoves & ~(knightMoves - 1);
            }
            knights = knights & ~(aKnight);
            aKnight = knights & ~(knights - 1);
        }
        return allKnightMoves;
    }

    /** Returns all possible king moves.
     *
     * @param OCCUPIED Bitboard of all occupied squares
     * @param king Bitboard of the king.
     * @return String of all possible king moves
     */
    public static String possibleKingMoves(long OCCUPIED, long king) {
        String allKingMoves = "";
        long kingMoves;

        int kingLocation = Long.numberOfTrailingZeros(king);
        // Note: KING_SPAN is the span of a king on square 9. 9 is chosen because it is the smallest square
        // that the king can be on while still showing all 8 moves.

        // Exactly the same as possibleWNMoves() but with KING_SPAN instead.
        if (kingLocation > 18) {
            kingMoves = KING_SPAN << (kingLocation - 9);
        } else {
            kingMoves = KING_SPAN >> (9 - kingLocation);
        }
        if (kingLocation % 8 < 4) {
            kingMoves = kingMoves & CAPTURABLE & ~FILES[6] & ~FILEH;
        } else {
            kingMoves = kingMoves & CAPTURABLE & ~FILEA & ~FILES[1];
        }

        long aMove = kingMoves & ~(kingMoves - 1);
        while (aMove != 0) {
            int i = Long.numberOfTrailingZeros(aMove);
            allKingMoves += "" + (kingLocation / 8) + (kingLocation % 8) + (i / 8) + (i % 8);
            kingMoves = kingMoves & (~aMove);
            aMove = kingMoves & ~(kingMoves - 1);
        }
        return allKingMoves;
    }

    /** Returns possible castling moves for white.
     *
     * @return String of possible castling moves or "" if none are possible
     */
    public static String whiteCastling(){
        String castles = "";
        if (wkcastle){
            // Castling king side. Checking if squares 61 and 62 are empty
            if ((OCCUPIED&((1L<<61)|(1L<<62)))==0){
                castles += "7476";
            }
        }
        if (wqcastle){
            // Castling queen side. Checking if squares 57, 58, 59 are empty
            if ((OCCUPIED&((1L<<57)|(1L<<58)|(1L<<59)))==0){
                castles += "7472";
            }
        }
        return castles;
    }

    /** Returns possible castling moves for black.
     *
     * @return String of possible castling moves or "" if none are possible
     */
    public static String blackCastling(){
        String castles = "";
        if (bkcastle){
            if ((OCCUPIED&((1L<<5)|(1L<<6)))==0){
                castles += "0406";
            }
        }
        if (bqcastle){
            if ((OCCUPIED&((1L<<1)|(1L<<2)|(1L<<3)))==0){
                castles += "0402";
            }
        }
        return castles;
    }

    /** Return the bitboard containing all squares covered by black. (Black can capture if there is a piece there)
     *
     * @param allBitBoards List of bitboards of every type of piece
     * @return Bitboard of all squares covered by black
     */
    public static long coveredByWhite(long[] allBitBoards) {
        long WP = allBitBoards[0], WB = allBitBoards[1], WN = allBitBoards[2], WR = allBitBoards[3],
                WQ = allBitBoards[4], WK = allBitBoards[5], BP = allBitBoards[6], BB = allBitBoards[7],
                BN = allBitBoards[8], BR = allBitBoards[9], BQ = allBitBoards[10], BK = allBitBoards[11];

        long covered = 0L;

        OCCUPIED = WP | WB | WN | WR | WQ | WK | BP | BB | BN | BR | BQ | BK;

        // PAWNS
        covered = covered | ((WP >> 7) & ~FILEA); // Capture right
        covered = covered | ((WP >> 9) & ~FILEH); // Capture left

        // KNIGHTS
        // Same code as possibleWNMoves() but without going through every move individually (since we want every move)
        long aKnight = WN & ~(WN - 1);
        long knightSquares;
        while (aKnight != 0) {
            int knightLocation = Long.numberOfTrailingZeros(aKnight);
            if (knightLocation > 18) {
                knightSquares = KNIGHT_SPAN << (knightLocation - 18);
            } else {
                knightSquares = KNIGHT_SPAN >> (18 - knightLocation);
            }
            if (knightLocation % 8 < 4) {
                knightSquares = knightSquares & ~FILES[6] & ~FILEH;
            } else {
                knightSquares= knightSquares & ~FILEA & ~FILES[1];
            }
            covered = covered | knightSquares;
            WN = WN & ~(aKnight);
            aKnight = WN & ~(WN - 1);
        }

        // KING
        // Same code as possibleWKMoves()
        long kingSquares;
        int kingLocation = Long.numberOfTrailingZeros(WK);
        if (kingLocation > 9) {
            kingSquares = KING_SPAN << (kingLocation - 9);
        } else {
            kingSquares = KING_SPAN >> (9 - kingLocation);
        }
        if (kingLocation % 8 < 4) {
            kingSquares = kingSquares & ~FILES[6] & ~FILEH;
        } else {
            kingSquares = kingSquares & ~FILEA & ~FILES[1];
        }
        covered = covered | kingSquares;

        // BISHOPS
        long aBishop = WB & ~(WB - 1);
        long bishopSquares;
        while (aBishop != 0) {
            int bishopLocation = Long.numberOfTrailingZeros(aBishop);
            bishopSquares = DADMoves(bishopLocation);
            covered = covered | bishopSquares;
            WB = WB & ~(aBishop);
            aBishop = WB & ~(WB - 1);
        }

        // ROOKS
        long aRook = WR & ~(WR - 1);
        long rookSquares;
        while (aRook != 0) {
            int rookLocation = Long.numberOfTrailingZeros(aRook);
            rookSquares = HVMoves(rookLocation);
            covered = covered | rookSquares;
            WR = WR & ~(aRook);
            aRook = WR & ~(WR - 1);
        }

        //QUEENS
        long aQueen = WQ & ~(WQ - 1);
        long queenSquares;
        while (aQueen != 0) {
            int queenLocation = Long.numberOfTrailingZeros(aQueen);
            queenSquares = (HVMoves(queenLocation) | DADMoves(queenLocation));
            covered = covered | queenSquares;
            WQ = WQ & ~(aQueen);
            aQueen = WQ & ~(WQ - 1);
        }

        return covered;
    }

    /** Return the bitboard containing all squares covered by white. (White can capture if there is a piece there)
     *
     * @param allBitBoards List of bitboards of every type of piece
     * @return Bitboard of all squares covered by white
     */
    public static long coveredByBlack(long[] allBitBoards) {
        long WP = allBitBoards[0], WB = allBitBoards[1], WN = allBitBoards[2], WR = allBitBoards[3],
                WQ = allBitBoards[4], WK = allBitBoards[5], BP = allBitBoards[6], BB = allBitBoards[7],
                BN = allBitBoards[8], BR = allBitBoards[9], BQ = allBitBoards[10], BK = allBitBoards[11];

        long covered = 0L;
        OCCUPIED = WP | WB | WN | WR | WQ | WK | BP | BB | BN | BR | BQ | BK;

        // PAWNS
        covered = covered | ((BP << 7) & ~FILEA); // Capture right
        covered = covered | ((BP << 9) & ~FILEH); // Capture left

        // KNIGHTS
        // Same code as possibleWNMoves() but without going through every move individually (since we want every move)
        long aKnight = BN & ~(BN - 1);
        long knightSquares;
        while (aKnight != 0) {
            int knightLocation = Long.numberOfTrailingZeros(aKnight);
            if (knightLocation > 18) {
                knightSquares = KNIGHT_SPAN << (knightLocation - 18);
            } else {
                knightSquares = KNIGHT_SPAN >> (18 - knightLocation);
            }
            if (knightLocation % 8 < 4) {
                knightSquares = knightSquares & ~FILES[6] & ~FILEH;
            } else {
                knightSquares= knightSquares & ~FILEA & ~FILES[1];
            }
            covered = covered | knightSquares;
            BN = BN & ~(aKnight);
            aKnight = BN & ~(BN - 1);
        }

        // KING
        // Same code as possibleWKMoves()
        long kingSquares;
        int kingLocation = Long.numberOfTrailingZeros(BK);
        if (kingLocation > 9) {
            kingSquares = KING_SPAN << (kingLocation - 9);
        } else {
            kingSquares = KING_SPAN >> (9 - kingLocation);
        }
        if (kingLocation % 8 < 4) {
            kingSquares = kingSquares & ~FILES[6] & ~FILEH;
        } else {
            kingSquares = kingSquares & ~FILEA & ~FILES[1];
        }
        covered = covered | kingSquares;

        // BISHOPS
        long aBishop = BB & ~(BB - 1);
        long bishopSquares;
        while (aBishop != 0) {
            int bishopLocation = Long.numberOfTrailingZeros(aBishop);
            bishopSquares = DADMoves(bishopLocation);
            covered = covered | bishopSquares;
            BB = BB & ~(aBishop);
            aBishop = BB & ~(BB - 1);
        }

        // ROOKS
        long aRook = BR & ~(BR - 1);
        long rookSquares;
        while (aRook != 0) {
            int rookLocation = Long.numberOfTrailingZeros(aRook);
            rookSquares = HVMoves(rookLocation);
            covered = covered | rookSquares;
            BR = BR & ~(aRook);
            aRook = BR & ~(BR - 1);
        }

        //QUEENS
        long aQueen = BQ & ~(BQ - 1);
        long queenSquares;
        while (aQueen != 0) {
            int queenLocation = Long.numberOfTrailingZeros(aQueen);
            queenSquares = (HVMoves(queenLocation) | DADMoves(queenLocation));
            covered = covered | queenSquares;
            BQ = BQ & ~(aQueen);
            aQueen = BQ & ~(BQ - 1);
        }

        return covered;

    }
}



