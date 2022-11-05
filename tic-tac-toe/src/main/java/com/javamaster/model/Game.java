package com.javamaster.model;

import lombok.Data;

@Data
public class Game {

    private String gameId;
    private Player player1;
    private Player player2;
    private Status status;
    private int[][] board;
    private TicTacToe winner;

}
