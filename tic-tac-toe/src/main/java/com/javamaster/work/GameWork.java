package com.javamaster.work;

import com.javamaster.exception.InvalidGameException;
import com.javamaster.exception.InvalidParamException;
import com.javamaster.exception.NotFoundException;
import com.javamaster.model.Game;
import com.javamaster.model.GamePlay;
import com.javamaster.model.Player;
import com.javamaster.model.TicTacToe;
import com.javamaster.memory.GameMemory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.javamaster.model.Status.*;

@Service
@AllArgsConstructor
public class GameWork {

    public Game makeGame(Player player) {
        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(NEW);
        GameMemory.getInstance().setGame(game);
        return game;
    }

    public Game connection(Player player2, String gameId) throws InvalidParamException, InvalidGameException {
        if (!GameMemory.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided id doesn't exist");
        }
        Game game = GameMemory.getInstance().getGames().get(gameId);

        if (game.getPlayer2() != null) {
            throw new InvalidGameException("Game is not valid anymore");
        }

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameMemory.getInstance().setGame(game);
        return game;
    }

    public Game randomConnection(Player player2) throws NotFoundException {
        Game game = GameMemory.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(NEW))
                .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameMemory.getInstance().setGame(game);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if (!GameMemory.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameMemory.getInstance().getGames().get(gamePlay.getGameId());
        if (game.getStatus().equals(FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        String a = gamePlay.getType().name();
        board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();

//        State state = new State(0, this.twoDToOneDArray(board));
        if (game.getPlayer2() == null) {
            game.setBoard(this.oneDToTwoDArray(new AdversarialSearchTicTacToe().minMaxDecision(new State(0, this.twoDToOneDArray(board)))));
        }

//        if (game.getPlayer2() == null ) {
//            game.setBoard(this.oneDToTwoDArray(new AdversarialSearchTicTacToe().minMaxDecision(new State(0,
//                    new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}))));
//            Player p = new Player();
//            p.setLogin("Computer");
//            game.setPlayer2(p);
//        } else if (game.getPlayer2().getLogin().equals("Computer")) {
//            game.setBoard(this.oneDToTwoDArray(new AdversarialSearchTicTacToe().minMaxDecision(new State(0,
//                    this.twoDToOneDArray(board)))));
//        }



        Boolean xWinner = winner(game.getBoard(), TicTacToe.X);
        Boolean oWinner = winner(game.getBoard(), TicTacToe.O);

        if (xWinner) {
            game.setWinner(TicTacToe.X);
        } else if (oWinner) {
            game.setWinner(TicTacToe.O);
        }

        GameMemory.getInstance().setGame(game);
        return game;
    }

    private Boolean winner(int[][] board, TicTacToe ticToe) {
        int[] boardArray = new int[9];
        int counterIndex = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardArray[counterIndex] = board[i][j];
                counterIndex++;
            }
        }

        int[][] winCombinations = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
        for (int i = 0; i < winCombinations.length; i++) {
            int counter = 0;
            for (int j = 0; j < winCombinations[i].length; j++) {
                if (boardArray[winCombinations[i][j]] == ticToe.getValue()) {
                    counter++;
                    if (counter == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int[][] oneDToTwoDArray(String[] oneD) {
        int[][] board = new int[3][3];
        board[0][0] = oneD[0].equals("O") ? 2 : oneD[0].equals("X") ? 1 : 0;
        board[0][1] = oneD[1].equals("O") ? 2 : oneD[1].equals("X") ? 1 : 0;
        board[0][2] = oneD[2].equals("O") ? 2 : oneD[2].equals("X") ? 1 : 0;
        board[1][0] = oneD[3].equals("O") ? 2 : oneD[3].equals("X") ? 1 : 0;
        board[1][1] = oneD[4].equals("O") ? 2 : oneD[4].equals("X") ? 1 : 0;
        board[1][2] = oneD[5].equals("O") ? 2 : oneD[5].equals("X") ? 1 : 0;
        board[2][0] = oneD[6].equals("O") ? 2 : oneD[6].equals("X") ? 1 : 0;
        board[2][1] = oneD[7].equals("O") ? 2 : oneD[7].equals("X") ? 1 : 0;
        board[2][2] = oneD[8].equals("O") ? 2 : oneD[8].equals("X") ? 1 : 0;
        return board;
    }

    private String[] twoDToOneDArray(int[][] twoD) {
        String[] oneD = new String[9];
        int index = 0;
        for (int row = 0; row < twoD.length; row++) {
            for (int col = 0; col < twoD[row].length; col++) {
                oneD[index] = twoD[row][col] == 1 ? "X" : twoD[row][col] == 2 ? "O" : String.valueOf(index);
                index++;
            }
        }
        return oneD;
    }
}
