package src;

public class BoardRepresentation {

    public static void main() {
        // Initilazing bitboards for every type of piece
        long WP = 0L, WB = 0L, WN = 0L, WR = 0L, WQ = 0L, WK = 0L, BP = 0L, BB = 0L, BN = 0L, BR = 0L, BQ = 0L, BK = 0L;

        //Creating string representation of a chess board the converting the board into 12 bitboards using convertToBitBoard
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


    }


    public static void converToBitBoard(String[][] chessBoard) {

        String bitboard;
        for (int i = 0; i < 64; i++){
            // Creating an empty bitboard
            bitboard = "0000000000000000000000000000000000000000000000000000000000000000";
            // Placing a 1 where the piece is located on the board ( the ith digit of bitboard )
            bitboard = bitboard.substring(0, i) + "1" + bitboard.substring(i + 1);
            
            switch (chessBoard[i/8][i%8]){


            }



        }


    }



}















