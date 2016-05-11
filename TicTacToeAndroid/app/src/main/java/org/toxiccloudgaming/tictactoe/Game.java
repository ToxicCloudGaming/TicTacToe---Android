package org.toxiccloudgaming.tictactoe;

import org.toxiccloudgaming.tictactoe.view.PlayGridAdapter;
import org.toxiccloudgaming.tictactoe.view.PlayTile;

public class Game {

    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';
    public static final char EMPTY = '_';

    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;

    private int gameID;
    private char[][] gameGrid;
    private String player1;
    private String player2;
    private String currentPlayer;
    private String winner;

    private boolean gameStarted;
    private boolean gameOver;
    private boolean playerSurrendered;
    private boolean gameTied;

    public Game(int gameID, char[][] gameGrid, String player1, String player2, String currentPlayer, String winner) {
        this.gameID = gameID;
        this.gameGrid = gameGrid;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.winner = winner;

        this.gameStarted = false;
        this.gameOver = false;
        this.playerSurrendered = false;
        this.gameTied = false;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setPlayerSurrendered(boolean playerSurrendered) {
        this.playerSurrendered = playerSurrendered;
    }

    public void setGameTied(boolean gameTied) {
        this.gameTied = gameTied;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getWaitingPlayer() {
        if(this.player1 != null && this.currentPlayer.equals(this.player1)) return this.player1;
        return this.player2;
    }

    public boolean getGameStarted() {
        return this.gameStarted;
    }

    public boolean getGameOver() {
        return this.gameOver;
    }

    public boolean getPlayerSurrendered() {
        return this.playerSurrendered;
    }

    public boolean getGameTied() {
        return this.gameTied;
    }

    public String getPlayer1() {
        return this.player1;
    }

    public String getPlayer2() {
        return this.player2;
    }

    public int getPlayerNum(String player) {
        if(this.player1 != null && this.player1.equals(player)) return PLAYER_1;
        return PLAYER_2;
    }

    public String getChallenger(String player) {
        if(this.getPlayerNum(player) == PLAYER_1) return this.player2;
        return this.player1;
    }

    public String getWinner() {
        return this.winner;
    }

    public String getCurrentPlayer() {
        return this.currentPlayer;
    }

    public String getForfeiter() {
        if(this.winner.equals(this.player1)) return this.player2;
        return this.player1;
    }

    public void updateBoard(char[][] gameGrid, PlayGridAdapter adapter) {
        for(int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (this.gameGrid[y][x] != gameGrid[y][x]) {
                    this.gameGrid[y][x] = gameGrid[y][x];
                    PlayTile playTile = (PlayTile)adapter.getItem(x + y * 3);
                    playTile.disable();
                    if (gameGrid[y][x] == EMPTY) {
                        playTile.setText(" ");
                    } else {
                        playTile.setText(Character.toString(gameGrid[y][x]));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
