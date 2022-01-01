package com.example.breakbrick;

import android.util.Log;

import java.util.Random;

public class PlayerInput {

    public static double findAngle(float x, float y, Ball ball, int screenX, int minYPixels) {
        //Find x and y coord of where the user is touching the screen
        //find the difference between those coords and where the balls are
        //use inverse tangent to find the angle
        //Convert to degrees

        float ballX = (float) Utility.metersToPixels(ball.getXCenter(), screenX);
        float ballY = (float) Utility.metersToPixels(ball.getYCenter(), screenX);
        ballY += minYPixels;
        float delty = Math.abs(ballY-y);
        //TODO throw out negative delty values (player aimed down not up)
        float deltx = x - ballX;

        //Because if x is 0 then arctan will be NaN
        if (deltx == 0.0) {
            deltx += 0.001;
        }

        double angle = Math.atan(delty/deltx);
        angle = Math.toDegrees(angle);

        //If the player shoots to the left, then add 180 (bc of arctan's domain)
        if(deltx < 0) {
            angle+=180;
        }

        double newAngle = 360-angle;

        return newAngle;
    }
}
