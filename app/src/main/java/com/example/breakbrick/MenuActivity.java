package com.example.breakbrick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Home button
        final Button mainBtn2 = (Button) findViewById(R.id.mainBtn2);
        mainBtn2.setOnClickListener(this);

        //level buttons
        final Button level1 = (Button) findViewById(R.id.level1);
        level1.setOnClickListener(this);
        final Button level2 = (Button) findViewById(R.id.level2);
        level2.setOnClickListener(this);
        final Button level3 = (Button) findViewById(R.id.level3);
        level3.setOnClickListener(this);
        final Button level4 = (Button) findViewById(R.id.level4);
        level4.setOnClickListener(this);
        final Button level5 = (Button) findViewById(R.id.level5);
        level5.setOnClickListener(this);
        final Button level6 = (Button) findViewById(R.id.level6);
        level6.setOnClickListener(this);
        final Button level7 = (Button) findViewById(R.id.level7);
        level7.setOnClickListener(this);
        final Button level8 = (Button) findViewById(R.id.level8);
        level8.setOnClickListener(this);
        final Button level9 = (Button) findViewById(R.id.level9);
        level9.setOnClickListener(this);

        final Button level10 = (Button) findViewById(R.id.level10);
        level10.setOnClickListener(this);
        final Button level11 = (Button) findViewById(R.id.level11);
        level11.setOnClickListener(this);
        final Button level12 = (Button) findViewById(R.id.level12);
        level12.setOnClickListener(this);
        final Button level13 = (Button) findViewById(R.id.level13);
        level13.setOnClickListener(this);
        final Button level14 = (Button) findViewById(R.id.level14);
        level14.setOnClickListener(this);
        final Button level15 = (Button) findViewById(R.id.level15);
        level15.setOnClickListener(this);
        final Button level16 = (Button) findViewById(R.id.level16);
        level16.setOnClickListener(this);
        final Button level17 = (Button) findViewById(R.id.level17);
        level17.setOnClickListener(this);
        final Button level18 = (Button) findViewById(R.id.level18);
        level18.setOnClickListener(this);

        //high score text boxes
        //TODO if score is 0 then note that that level hasn't been beat
        TextView level1Score = (TextView) findViewById(R.id.level1Score);
        level1Score.setText("High score: " + Utility.getHighScore(this, "1")+"");  //Score is 0 by default
        TextView level2Score = (TextView) findViewById(R.id.level2Score);
        level2Score.setText(Utility.getHighScore(this, "2")+"");
        TextView level3Score = (TextView) findViewById(R.id.level3Score);
        level3Score.setText(Utility.getHighScore(this, "3")+"");
        TextView level4Score = (TextView) findViewById(R.id.level4Score);
        level4Score.setText(Utility.getHighScore(this, "4")+"");
        TextView level5Score = (TextView) findViewById(R.id.level5Score);
        level5Score.setText(Utility.getHighScore(this, "5")+"");
        TextView level6Score = (TextView) findViewById(R.id.level6Score);
        level6Score.setText(Utility.getHighScore(this, "6")+"");
        TextView level7Score = (TextView) findViewById(R.id.level7Score);
        level7Score.setText(Utility.getHighScore(this, "7")+"");
        TextView level8Score = (TextView) findViewById(R.id.level8Score);
        level8Score.setText(Utility.getHighScore(this, "8")+"");
        TextView level9Score = (TextView) findViewById(R.id.level9Score);
        level9Score.setText(Utility.getHighScore(this, "9")+"");

        TextView level10Score = (TextView) findViewById(R.id.level10Score);
        level10Score.setText(Utility.getHighScore(this, "10")+"");
        TextView level11Score = (TextView) findViewById(R.id.level11Score);
        level11Score.setText(Utility.getHighScore(this, "11")+"");
        TextView level12Score = (TextView) findViewById(R.id.level12Score);
        level12Score.setText(Utility.getHighScore(this, "12")+"");
        TextView level13Score = (TextView) findViewById(R.id.level13Score);
        level13Score.setText(Utility.getHighScore(this, "13")+"");
        TextView level14Score = (TextView) findViewById(R.id.level14Score);
        level14Score.setText(Utility.getHighScore(this, "14")+"");
        TextView level15Score = (TextView) findViewById(R.id.level15Score);
        level15Score.setText(Utility.getHighScore(this, "15")+"");
        TextView level16Score = (TextView) findViewById(R.id.level16Score);
        level16Score.setText(Utility.getHighScore(this, "16")+"");
        TextView level17Score = (TextView) findViewById(R.id.level17Score);
        level17Score.setText(Utility.getHighScore(this, "17")+"");
        TextView level18Score = (TextView) findViewById(R.id.level18Score);
        level18Score.setText(Utility.getHighScore(this, "18")+"");
    }

    //Open the game activity when a level button is clicked
    @Override
    public void onClick(View v) {
        int levelNum = -1;
        boolean home = false;

        //Is there a less repetitive way to do this? Ideally without reading the level file
        switch (v.getId()) {
            case R.id.mainBtn2:
                Log.d("Melody", "home button 2 pressed");
                home = true;
                break;
            case R.id.level1:
                levelNum = 1;
                break;
            case R.id.level2:
                levelNum = 2;
                break;
            case R.id.level3:
                levelNum = 3;
                break;
            case R.id.level4:
                levelNum = 4;
                break;
            case R.id.level5:
                levelNum = 5;
                break;
            case R.id.level6:
                levelNum = 6;
                break;
            case R.id.level7:
                levelNum = 7;
                break;
            case R.id.level8:
                levelNum = 8;
                break;
            case R.id.level9:
                levelNum = 9;
                break;
            case R.id.level10:
                levelNum = 10;
                break;
            case R.id.level11:
                levelNum = 11;
                break;
            case R.id.level12:
                levelNum = 12;
                break;
            case R.id.level13:
                levelNum = 13;
                break;
            case R.id.level14:
                levelNum = 14;
                break;
            case R.id.level15:
                levelNum = 15;
                break;
            case R.id.level16:
                levelNum = 16;
                break;
            case R.id.level17:
                levelNum = 17;
                break;
            case R.id.level18:
                levelNum = 18;
                break;
            default:
                break;
        }

        if (home) {
            Intent startMainIntent = new Intent(this, MainActivity.class);
            startActivity(startMainIntent);
            finish();
        }
        else {
            Intent startGameIntent = new Intent(this, GameActivity.class);
            startGameIntent.putExtra("levelNum", levelNum);
            startActivity(startGameIntent);
            finish(); //end this activity
        }
    }
}