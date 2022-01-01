package com.example.breakbrick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final android.widget.Button mainBtn = (Button) findViewById(R.id.mainBtn);
        mainBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent startMainIntent = new Intent(this, MainActivity.class);
        startActivity(startMainIntent);
        finish();
    }
}