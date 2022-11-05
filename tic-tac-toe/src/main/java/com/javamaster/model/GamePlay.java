package com.javamaster.model;

import lombok.Data;

@Data
public class GamePlay {

    private TicTacToe type;
    private Integer coordinateX;
    private Integer coordinateY;
    private String gameId;
}
