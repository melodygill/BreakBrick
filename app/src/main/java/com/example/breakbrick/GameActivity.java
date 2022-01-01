package com.example.breakbrick;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageButton;

public class GameActivity extends Activity {

    private GameView gView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get level number
        int levelNum = -1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            levelNum = extras.getInt("levelNum");
        }
        if (levelNum == -1) {
            try {
                throw new Exception("No level number found");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Store screen resolution in a Point object
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Create GameView and make it the view for this activity
        gView = new GameView(this, size.x, size.y, levelNum);
        setContentView(gView);

        Log.d("Melody", "Here!");
    }

    //Methods to pause and resume the game thread if activity is paused/resumed
    @Override
    protected void onPause() {
        super.onPause();
        gView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gView.resume();
    }
}
