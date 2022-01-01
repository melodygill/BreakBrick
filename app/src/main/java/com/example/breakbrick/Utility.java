package com.example.breakbrick;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;


public class Utility {
    //Constants
    //all measurements in "meters" unless otherwise specified
    //modeling balls and blocks as squares
    public static final int ballDiameter = 20;
    public static final int blockLength = 60;

    public static final int numBlockRows = 10;
    public static final int numBlockCols = 10;

    public static final int ballSpeed = 30; //meters per frame

    public static final int playableWidth = numBlockCols*blockLength; //x coord
    public static final int playableLength = numBlockRows*blockLength; //y coord

    public static final int buttonHeightFactor = 4; //button height is 1/4 of minYPixels (from GameView)
    public static final int buttonLengthFactor = 6; //button length is 1/6 of screenX (also GameView)

    public static final String HIGHSCORES_NAME = "highscores"; //file that stores level high scores

    //Arrays that store sound files for various events
    //Note only use the button sound for my buttons because the built in Android Studio buttons
    //already have sound.
    public static final String[] soundEvents = {"hit", "win", "loss", "button"};
    public static final int[] soundIDs = {R.raw.mixkitgameballtap, R.raw.mixkitgamelevelcompleted,
            R.raw.mixkitplayerlosing, R.raw.mixkitsmallhitinagame};
    public static MediaPlayer hitMP; //Not final; will be created when GameView is instantiated

    //Methods

    //Uses playableWidth and screenX to find conversion factor
    public static double metersToPixels(double meters, int screenX) {
        double convFactor = (double) screenX/playableWidth;
        return (convFactor * meters);
    }

    //Converts all 4 Rect sides to pixels and adds minYPixels to top and bottom
    //In order to prepare Rect for drawing to screen
    public static Rect makeDrawable(Rect meters, int screenX, int minYPixels) {
        Rect temp = new Rect();
        temp.top = (int)metersToPixels(meters.top, screenX) + minYPixels;
        temp.left = (int)metersToPixels(meters.left, screenX);
        temp.bottom = (int)metersToPixels(meters.bottom, screenX) + minYPixels;
        temp.right = (int)metersToPixels(meters.right, screenX);
        return temp;
    }

    public static double pixelsToMeters(double pixels, int screenX) {
        double convFactor = playableWidth/(double)screenX;
        return convFactor * pixels;
    }

    //Given angle of overall velocity vector in degrees and the constant ballSpeed
    //Returns the value of the horizontal vector
    public static double findXvel(double angle) {
        //convert to radians
        double radians = Math.toRadians(angle);
        double xvel = ballSpeed * Math.cos(radians);
        return xvel;
    }

    //Returns the value of the vertical vector
    public static double findYvel(double angle) {
        //convert to radians my abhorred
        double radians = Math.toRadians(angle);
        double yvel = ballSpeed * Math.sin(radians);
        return yvel;
    }


    //Saves high score data
    public static void setHighScore(Context context, String levelNum, int score) {
        //0 is MODE_PRIVATE
        SharedPreferences sp = context.getSharedPreferences(HIGHSCORES_NAME, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(levelNum, score);
        edit.apply();
    }

    //Retrieves high score data. Returns 0 if the given level does not have an associated score
    public static int getHighScore(Context context, String level) {
        SharedPreferences sp = context.getSharedPreferences(HIGHSCORES_NAME, 0);
        int score = sp.getInt(level, 0);
        return score;
    }

    //Plays the sound file that corresponds to the given string
    //Can use this method for hit sounds, but prefer playHit() because it's faster
    public static void playSound(Context context, String event) {
        //Find the resource ID that corresponds to the given event
        int index = -1;
        for (int i = 0; i < soundEvents.length; i++) {
            if (event.equals(soundEvents[i])) {
                index = i;
            }
        }
        assert index != -1 : "Sound event was not found";

        //Play the sound
        MediaPlayer mediaPlayer = MediaPlayer.create(context, soundIDs[index]);
        mediaPlayer.start();
        //mediaPlayer.release();
        //mediaPlayer = null;
    }

    //Instantiates hitMP for faster sound playing
    public static void setHitMP(MediaPlayer mp) {
        hitMP = mp;
    }

    public static void playHit() {
        assert hitMP != null : "MediaPlayer was not instantiated; cannot play sound";
        hitMP.start();
    }

    //Returns resource ID of color depending on value of block
    public static int getBlockColor(Block b) {
        int val = b.getValue();
        if (val < 10) {
            return R.color.purple_200;
        }
        else if (val < 20) {
            return R.color.purple_500;
        }
        else if (val < 30) {
            return R.color.purple_700;
        }
        else if (val < 40) {
            return R.color.turquoise;
        }
        else {
            return R.color.leaf_green;
        }
    }
}
