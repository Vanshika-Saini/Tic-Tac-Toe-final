package com.javamaster.memory;

import com.javamaster.model.Game;

import java.util.HashMap;
import java.util.Map;

public class GameMemory {

    private static Map<String, Game> games;
    private static GameMemory instance;

    private GameMemory() {
        games = new HashMap<>();
    }

    public static synchronized GameMemory getInstance() {
        if (instance == null) {
            instance = new GameMemory();
        }
        return instance;
    }

    public Map<String, Game> getGames() {
        return games;
    }

    public void setGame(Game game) {
        games.put(game.getGameId(), game);
    }
}
