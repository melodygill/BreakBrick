package com.example.breakbrick;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button playBtn = (Button) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);

        final Button aboutBtn = (Button) findViewById(R.id.aboutBtn);
        aboutBtn.setOnClickListener(this);
    }

    //Open the menu activity when play button is clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBtn:
                //No sound because these buttons already have sound via Android Studio
                Intent startMenuIntent = new Intent(this, MenuActivity.class);
                startActivity(startMenuIntent);
                finish(); //end this activity
                break;
            case R.id.aboutBtn:
                Intent startAboutIntent = new Intent(this, AboutActivity.class);
                startActivity(startAboutIntent);
                finish();
                break;
            default:
                break;
        }
    }
}