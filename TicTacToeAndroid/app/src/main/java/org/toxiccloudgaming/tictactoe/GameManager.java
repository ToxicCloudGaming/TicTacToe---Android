package org.toxiccloudgaming.tictactoe;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.toxiccloudgaming.manager.ActivityManager;
import org.toxiccloudgaming.manager.Prefs;
import org.toxiccloudgaming.tictactoe.client.Action;
import org.toxiccloudgaming.tictactoe.client.GameClient;
import org.toxiccloudgaming.tictactoe.client.ResponseManager;
import org.toxiccloudgaming.tictactoe.view.MenuManager;
import org.toxiccloudgaming.tictactoe.view.PlayGridAdapter;
import org.toxiccloudgaming.tictactoe.view.PlayTile;

import java.util.ArrayList;
import java.util.List;

public class GameManager extends ActivityManager {

    public final static String PREFS_USER = "Username";
    public final static String PREFS_LAST_SESSION = "LastSession";
    public final static String PREFS_ADDRESS = "Address";

    private Game game;
    private boolean inGame;
    private boolean searchingForGame;

    private ResponseManager responseManager;

    public GameManager(Context context) {
        super(context);
    }

    @Override
    protected void preInit() {
        this.game = null;
        this.inGame = false;
        this.searchingForGame = false;

        this.setClient(new GameClient(this));
        this.responseManager = new ResponseManager(this, (GameClient)this.getClient());
    }

    @Override
    protected void initUI() {
        this.setGameStatus("You are not currently in a game!");

        GridView playGrid = (GridView)this.getActivityFromContext().findViewById(R.id.play_grid);
        List<PlayTile> playTiles = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            PlayTile playTile = new PlayTile(this.getContext());
            playTile.disable();
            playTile.setBackground(ResourcesCompat.getDrawable(this.getActivityFromContext().getResources(),
                    R.drawable.play_tile_background, null));
            playTiles.add(playTile);
        }

        final PlayGridAdapter playGridAdapter = new PlayGridAdapter(this.getContext(), playTiles);
        playGrid.setAdapter(playGridAdapter);

        playGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlayTile playTile = (PlayTile) parent.getItemAtPosition(position);

                if (playTile.isEnabled()) {
                    playTile.disable();
                    playGridAdapter.notifyDataSetChanged();

                    GameManager manager = GameManager.this;
                    Game game = manager.game;
                    GameClient client = (GameClient) manager.getClient();
                    if (game.getGameStarted() && !game.getGameOver() && game.getCurrentPlayer().equals(client.getUsername())) {
                        manager.setGameStatus("Sending move...");
                        JSONObject json = Action.sendMove(client, client.getUsername(), client.getSessionID(), position);
                        manager.getResponseManager().handleResponse(json, client.getUsername(), client.getSessionID());
                    } else if (!game.getGameStarted()) {
                        manager.setGameStatus("Game has not started.");
                    } else if (game.getGameOver()) {
                        manager.setGameStatus("Game is over.");
                    } else {
                        manager.setGameStatus("It is not your turn!");
                    }

                    playTile.enable();
                    playGridAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void postInit() {
        String savedAddress = (String)this.getPref(PREFS_ADDRESS, GameClient.SERVER_DEFAULT_HOST, Prefs.TYPE_STRING);
        String savedUsername = (String)this.getPref(PREFS_USER, null, Prefs.TYPE_STRING);
        String sessionID = (String)this.getPref(PREFS_LAST_SESSION, null, Prefs.TYPE_STRING);

        this.getClient().setHost(savedAddress);
        if(savedUsername != null && sessionID != null) {
            GameClient client = (GameClient)this.getClient();
            client.setUsername(savedUsername);
            client.setSessionID(sessionID);
            MenuManager.signIn(client, this);
        }
    }

    @Override
    public boolean createOptions(Menu menu) {
        this.getActivityFromContext().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean prepareOptions(Menu menu) {
        boolean signedIn = ((GameClient)this.getClient()).isSignedIn();
        menu.findItem(R.id.action_sign_in).setVisible(!signedIn);
        menu.findItem(R.id.action_sign_out).setVisible(signedIn);

        menu.findItem(R.id.action_find_game).setVisible(!this.inGame && !this.searchingForGame);
        menu.findItem(R.id.action_leave_game).setVisible(this.inGame);
        menu.findItem(R.id.action_cancel).setVisible(this.searchingForGame);
        return true;
    }

    public ResponseManager getResponseManager() {
        return this.responseManager;
    }

    public void findGame() {
        MenuManager.findGame((GameClient) this.getClient(), this);
    }

    public void leaveGame() {
        MenuManager.leaveGame((GameClient) this.getClient(), this);
    }

    public void settings() {
        MenuManager.settings((GameClient) this.getClient(), this);
    }

    public void signIn() {
        MenuManager.signIn((GameClient) this.getClient(), this);
    }

    public void signOut() {
        MenuManager.signOut((GameClient) this.getClient(), this);
    }

    public void about() {
        MenuManager.about(this.getActivityFromContext());
    }

    public void connectionChanged(boolean connected) {
        String text;
        int color;

        if(connected) {
            text = "connected";
            color = Color.GREEN;
        } else {
            text = "disconnected";
            color = Color.RED;
        }

        TextView statusText = (TextView)this.getActivityFromContext().findViewById(R.id.text_ui_status);
        statusText.setText(text);
        statusText.setTextColor(color);
    }

    public void updatePing(boolean connected, int ping) {
        String text;
        int color;

        if(connected) {
            text = ping + " ms";
            if(ping < 250) {
                color = Color.CYAN;
            } else if(ping >= 250 && ping < 400) {
                color = Color.rgb(255, 125, 0);
            } else {
                color = Color.RED;
            }
        } else {
            text = "- - -";
            color = Color.GRAY;
        }

        TextView pingText = (TextView)this.getActivityFromContext().findViewById(R.id.text_ui_ping);
        pingText.setText(text);
        pingText.setTextColor(color);
    }

    @Override
    public void start() {
        this.getClient().start();
    }

    @Override
    public void stop() {
        this.getClient().stop();
    }

    public void setGameStatus(String status) {
        TextView gameStatus = (TextView)this.getActivityFromContext().findViewById(R.id.play_status);
        gameStatus.setText(status);
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public boolean inGame() {
        return this.inGame;
    }

    public void setSearchingForGame(boolean searchingForGame) {
        this.searchingForGame = searchingForGame;
    }

    public void setUsernameText(String username) {
        String text = "N/A";
        int color = Color.GRAY;
        if(username != null) {
            text = username;
            color = Color.BLUE;
        }

        TextView signInText = (TextView)this.getActivityFromContext().findViewById(R.id.text_ui_username);
        signInText.setText(text);
        signInText.setTextColor(color);
    }

    public boolean isSearchingForGame() {
        return this.searchingForGame;
    }

    public char[][] parseGameGrid(JSONArray array) throws JSONException {
        char[][] gameGrid = new char[3][3];
        for(int i = 0; i < 9; i++) {
            int x = i % 3;
            int y = (int)((i - x) / 3);
            gameGrid[y][x] = ((String)array.get(i)).charAt(0);
        }

        return gameGrid;
    }

    public PlayGridAdapter getPlayGridAdapter() {
        GridView playGrid = (GridView)this.getActivityFromContext().findViewById(R.id.play_grid);
        PlayGridAdapter adapter = (PlayGridAdapter)playGrid.getAdapter();
        return adapter;
    }

    public void clearBoard() {
        PlayGridAdapter adapter = this.getPlayGridAdapter();
        for(int i = 0; i < 9; i++) {
            PlayTile playTile = (PlayTile)adapter.getItem(i);
            playTile.disable();
            playTile.lockText();
            playTile.setBackground(ResourcesCompat.getDrawable(this.getActivityFromContext().getResources(),
                    R.drawable.play_tile_background, null));
        }
        adapter.notifyDataSetChanged();
    }

    public void newGame(JSONObject json, boolean rejoinGame) {
        this.inGame = true;
        try {
            JSONArray array = json.getJSONArray("gameGrid");
            char[][] gameGrid = parseGameGrid(array);
            int gameID = json.getInt("gameID");
            boolean gameStarted = json.getBoolean("gameStarted");
            boolean gameOver = json.getBoolean("gameOver");
            boolean playerSurrendered = json.getBoolean("playerSurrendered");
            boolean gameTie = json.getBoolean("gameTie");
            String player1 = json.getString("player1");
            String player2 = json.getString("player2");
            String winner = json.getString("winner");
            String currentPlayer = json.getString("currentPlayer");

            this.game = new Game(gameID, gameGrid, player1, player2, currentPlayer, winner);
            this.game.setGameStarted(gameStarted);
            this.game.setGameOver(gameOver);
            this.game.setGameTied(gameTie);
            this.game.setPlayerSurrendered(playerSurrendered);

            String username = ((GameClient)this.getClient()).getUsername();
            int whoseMove = this.game.getPlayerNum(this.game.getCurrentPlayer());

            if(gameStarted) {
                String turnText = null;
                if(this.game.getPlayerNum(username) == whoseMove) {
                    turnText = "It is your turn!";
                } else {
                    turnText = "It is their turn!";
                }

                if(rejoinGame) {
                    this.setGameStatus("You rejoined a previous game against " + this.game.getChallenger(username) + "!\n" + turnText);
                } else {
                    this.setGameStatus("You joined a game! Your opponent is " + this.game.getChallenger(username) + "!\n" + turnText);
                }
            }

            PlayGridAdapter adapter = this.getPlayGridAdapter();
            for(int i = 0; i < 9; i++) {
                PlayTile playTile = (PlayTile)adapter.getItem(i);
                playTile.setBackground(ResourcesCompat.getDrawable(this.getActivityFromContext().getResources(),
                        R.drawable.play_tile_background, null));
                int x = i % 3;
                int y = (int)((i - x) / 3);
                if(gameGrid[y][x] == Game.EMPTY) {
                    playTile.enable();
                    playTile.setText(" ");
                } else {
                    playTile.disable();
                    playTile.setText(Character.toString(gameGrid[y][x]));
                }
            }
            adapter.notifyDataSetChanged();

        } catch(Exception e) {
            Log.e("BDH", "Exception: ", e);
        }
    }

    public void syncGame(JSONObject json) {
        if(this.game == null) {
            this.newGame(json, true);
        } else {
            try {
                JSONArray array = json.getJSONArray("gameGrid");
                char[][] gameGrid = parseGameGrid(array);
                this.game.updateBoard(gameGrid, this.getPlayGridAdapter());

                boolean gameStarted = json.getBoolean("gameStarted");
                boolean gameOver = json.getBoolean("gameOver");
                boolean playerSurrendered = json.getBoolean("playerSurrendered");
                boolean gameTie = json.getBoolean("gameTie");
                String player1 = json.getString("player1");
                String player2 = json.getString("player2");
                String winner = json.getString("winner");
                String currentPlayer = json.getString("currentPlayer");

                String username = ((GameClient)this.getClient()).getUsername();

                if(!this.game.getGameOver()) {
                    if(!gameStarted) {
                        this.setGameStatus("Waiting for another player...");
                    } else if(gameStarted && !this.game.getGameStarted()) {
                        this.game.setGameStarted(true);
                        this.game.setPlayer2(player2);
                        this.setGameStatus("Player " + player2 + " joined the game!\n"
                                + "You have the first move.");
                    } else {
                        if(!currentPlayer.equals(this.game.getCurrentPlayer())) {
                            this.game.setCurrentPlayer(currentPlayer);
                            if(this.game.getCurrentPlayer().equals(username)) {
                                this.setGameStatus("Player " + this.game.getChallenger(username) + " went!\n"
                                    + "It is now your turn.");
                            } else {
                                this.setGameStatus("It is Player " + this.game.getChallenger(username) + "'s turn.");
                            }
                        }
                    }
                } else {
                    this.endGame(json);
                }
            } catch (Exception e) {
                Log.e("BDH", "Exception: ", e);
            }
        }
    }

    public void endGame(JSONObject json) {
        try {
            boolean gameStarted = json.getBoolean("gameStarted");
            boolean gameOver = json.getBoolean("gameOver");
            boolean gameTie = json.getBoolean("gameTie");
            boolean playerSurrendered = json.getBoolean("playerSurrendered");
            JSONArray array = json.getJSONArray("gameGrid");
            char[][] gameGrid = parseGameGrid(array);
            this.game.updateBoard(gameGrid, this.getPlayGridAdapter());

            if(json.has("winGrid")) {
                JSONArray array2 = json.getJSONArray("winGrid");
                int[] winGrid;
                winGrid = new int[3];
                winGrid[0] = array2.getInt(0);
                winGrid[1] = array2.getInt(1);
                winGrid[2] = array2.getInt(2);
                PlayGridAdapter adapter = this.getPlayGridAdapter();
                for(int i = 0; i < 3; i++) {
                    PlayTile playTile = (PlayTile)adapter.getItem(winGrid[i]);
                    playTile.setBackground(ResourcesCompat.getDrawable(this.getActivityFromContext().getResources(),
                            R.drawable.play_tile_highlight, null));
                }
                adapter.notifyDataSetChanged();
            }

            String winner = json.getString("winner");
            String username = ((GameClient)this.getClient()).getUsername();

            if(gameOver && playerSurrendered) {
                if(winner.equals(username)) {
                    this.setGameStatus(this.game.getChallenger(username) + " left the game, so you won!");
                } else {
                    this.setGameStatus("You surrendered, so " + winner + " won.");
                }
            } else if(gameOver && !gameStarted) {
                this.setGameStatus("Canceled game search.");
            } else if(gameOver && gameTie) {
                this.setGameStatus("Game ended with a tie!");
            } else if(playerSurrendered && !this.game.getPlayerSurrendered()) {
                this.game.setPlayerSurrendered(true);
                this.game.setWinner(winner);
                this.setGameStatus("Player " + this.game.getForfeiter() + " forefeited the game. You won!");
            } else if(gameTie && !this.game.getGameTied()) {
                this.game.setGameTied(true);
                this.setGameStatus("Game tied! No winner.");
            } else {
                if(winner != null) {

                    if(winner.equals(username)) {
                        this.setGameStatus("Congratulations! You won!");
                    } else {
                        this.setGameStatus("Sorry, you lost!");
                    }
                }
            }
        } catch(Exception e) {
            Log.e("BDH", "Exception: ", e);
        }

        this.flushGame();
    }

    public void flushGame() {
        if(this.game != null) this.game = null;
        if(this.inGame) this.inGame = false;

        PlayGridAdapter adapter = this.getPlayGridAdapter();
        for(int i = 0; i < 9; i++) {
            PlayTile playTile = (PlayTile)adapter.getItem(i);
            playTile.disable();
        }
        adapter.notifyDataSetChanged();
    }
}
