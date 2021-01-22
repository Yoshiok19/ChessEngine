package src;

import java.util.Arrays;

public class Board {
    static long[] allBitboards;
    static String history;
    public static void main(String[] args){
        // For debugging purposes
        generateBoard();

    }
    public static void generateBoard() {
        // Initializing bitboards for every type of piece
        long WP = 0L, WB = 0L, WN = 0L, WR = 0L, WQ = 0L, WK = 0L, BP = 0L, BB = 0L, BN = 0L, BR = 0L, BQ = 0L, BK = 0L;
        allBitboards = new long[]{WP, WB, WN, WR, WQ, WK, BP, BB, BN, BR, BQ, BK};

        // Creating string representation of a chess board then converting the board into 12 bitboards using convertToBitBoard
        String[][] chessBoard = {

                {"r", "n", "b", "q", "k", "b", "n", "r"},
                {"p", "p", "p", "p", "p", "p", "p", "p"},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {"P", "P", "P", "P", "P", "P", "P", "P"},
                {"R", "N", "B", "Q", "K", "B", "N", "R"}

        };

        convertToBitBoards(chessBoard, allBitboards);
        WP = allBitboards[0]; WB = allBitboards[1]; WN = allBitboards[2]; WR = allBitboards[3];
        WQ = allBitboards[4]; WK = allBitboards[5]; BP = allBitboards[6]; BB = allBitboards[7];
        BN = allBitboards[8]; BR = allBitboards[9]; BQ = allBitboards[10]; BK = allBitboards[11];

        Perft.perft(WP,WB,WN,WR,WQ,WK,BP,BB,BN,BR,BQ,BK,0,Moves.wkcastle, Moves.wqcastle, Moves.bkcastle, Moves.bqcastle, true, "");
        System.out.println(Perft.perftCounter);
        System.out.println(Perft.counter);
    }


    public static void convertToBitBoards(String[][] chessBoard, long[] allBitboards) {

        String stringBitboard;
        for (int i = 0; i < 64; i++){
            // Creating an empty bitboard
            stringBitboard = "0000000000000000000000000000000000000000000000000000000000000000";
            // Placing a 1 where the piece/(or no piece) is located on the board ( the ith digit of bitboard reading from the right)
            stringBitboard = stringBitboard.substring(i + 1) + "1" + stringBitboard.substring(0, i);

            switch (chessBoard[i / 8][i % 8]) {
                case "P":
                    allBitboards[0] += convertToBitBoard(stringBitboard);
                    break;
                case "B":
                    allBitboards[1] += convertToBitBoard(stringBitboard);
                    break;
                case "N":
                    allBitboards[2] += convertToBitBoard(stringBitboard);
                    break;
                case "R":
                    allBitboards[3] += convertToBitBoard(stringBitboard);
                    break;
                case "Q":
                    allBitboards[4] += convertToBitBoard(stringBitboard);
                    break;
                case "K":
                    allBitboards[5] += convertToBitBoard(stringBitboard);
                    break;
                case "p":
                    allBitboards[6] += convertToBitBoard(stringBitboard);
                    break;
                case "b":
                    allBitboards[7] += convertToBitBoard(stringBitboard);
                    break;
                case "n":
                    allBitboards[8] += convertToBitBoard(stringBitboard);
                    break;
                case "r":
                    allBitboards[9] += convertToBitBoard(stringBitboard);
                    break;
                case "q":
                    allBitboards[10] += convertToBitBoard(stringBitboard);
                    break;
                case "k":
                    allBitboards[11] += convertToBitBoard(stringBitboard);
                    break;

            }

        }
    }

    public static long convertToBitBoard(String stringBitboard){

        if (stringBitboard.charAt(0) == '0'){
            // The bitboard is positive so we can just convert it to a long normally. Since stringBitboard is a binary
            // number the radix is set to 2.
            return Long.parseLong(stringBitboard, 2);
        }
        // The bitboard is negative so we must convert taking into account two's complement.
        // Long.parseLong() doesn't treat the first 1 as a sign bit so you will get an overflow error.

        return Long.parseLong(stringBitboard.substring(1), 2) + Long.MIN_VALUE;

    }

    public static void drawBoard(long[] allBitboards){
        // Creating an empty array based chess board
        String[][] chessBoard = {

                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "},
                {" ", " ", " ", " ", " ", " ", " ", " "}

        };

        // For every square, reference the bitboard of each piece and determine if the piece is on the square i. (BP>>i
        // shifts the bitboard i digits to the right so that the right most digit is the bit corresponding to the ith square)
        for (int i=0;i<64;i++) {
            if (((allBitboards[0]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="P";
            }
            if (((allBitboards[1]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="B";
            }
            if (((allBitboards[2]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="N";
            }
            if (((allBitboards[3]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="R";
            }
            if (((allBitboards[4]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="Q";
            }
            if (((allBitboards[5]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="K";
            }
            if (((allBitboards[6]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="p";
            }
            if (((allBitboards[7]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="b";
            }
            if (((allBitboards[8]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="n";
            }
            if (((allBitboards[9]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="r";
            }
            if (((allBitboards[10]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="q";
            }
            if (((allBitboards[11]>>i)&1) == 1) {
                chessBoard[i/8][i%8]="k";
            }



        }

        // Drawing the constructed chess board to the console
        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }

}















