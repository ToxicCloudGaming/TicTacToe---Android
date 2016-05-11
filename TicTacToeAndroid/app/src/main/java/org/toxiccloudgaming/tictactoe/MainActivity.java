package org.toxiccloudgaming.tictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.toxiccloudgaming.manager.ActivityManager;

public class MainActivity extends AppCompatActivity {

    private ActivityManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.manager = new GameManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.manager.stop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return this.manager.prepareOptions(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return this.manager.createOptions(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        GameManager manager = (GameManager)this.manager;
        switch(item.getItemId()) {
            //Stop looking for new game
            case R.id.action_cancel:
                manager.setSearchingForGame(false);
                return true;
            //Find a new game
            case R.id.action_find_game:
                manager.findGame();
                return true;
            //Leave/surrender a current game
            case R.id.action_leave_game:
                manager.leaveGame();
                return true;
            //User can modify current settings including IP, sounds, etc
            case R.id.action_settings:
                manager.settings();
                return true;
            //User can sign in to a profile available on website server
            case R.id.action_sign_in:
                manager.signIn();
                return true;
            //If signed in, user can sign out from server
            case R.id.action_sign_out:
                manager.signOut();
                return true;
            //Displays a prompt with application information
            case R.id.action_about:
                manager.about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
