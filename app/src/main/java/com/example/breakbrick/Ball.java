package com.example.breakbrick;

import android.graphics.Rect;
import android.util.Log;

public class Ball {

    //INSTANCE VARIABLES
    //Position and velocity are measured in "meters", converted from pixels
    //Ensures that the balls move across the screen at the same speed on every screen size
    //xpos and ypos refer to the top left corner
    private double xpos, ypos;
    private double xvel, yvel;

    private boolean returned; //True if the ball has returned to the floor in a given turn
    private boolean launched; //True if the ball has been launched in a given turn
    private Rect hitbox; //For collision detection
    private int diameter; //Diameter of ball in meters. Will be dimensions of square hitbox

    //METHODS
    //constructor
    public Ball() {
        diameter = Utility.ballDiameter;

        xpos = (double)Utility.playableWidth/2; //Balls start in the middle of the floor
        ypos = Utility.playableLength - (diameter+1);
        xvel = 0;
        yvel = 0;

        returned = true; //Balls start already returned to floor
        launched = false; //Balls start on the floor, unlaunched
        //consider converting xpos and ypos to pixels so that the hitbox is more accurate
        hitbox = new Rect((int)xpos, (int)ypos, (int)xpos+diameter, (int)ypos+diameter);
    }

    //Moves ball by adding velocity to position; refreshes hitbox
    public void update() {
        xpos += xvel;
        ypos += yvel;
        hitbox.left = (int)xpos;
        hitbox.top = (int) ypos;
        hitbox.right = (int)xpos + diameter;
        hitbox.bottom = (int)ypos + diameter;
    }

    //Getters and setters
    //calculates and returns the x-coord of the ball's center
    public double getXCenter() {
        double xcenter = xpos + ((double)diameter/2);
        return xcenter;
    }

    //calculates and returns the y-coord of the ball's center
    public double getYCenter() {
        double ycenter = ypos + ((double)diameter/2);
        return ycenter;
    }

    public double getXpos() {
        return xpos;
    }

    public double getYpos() {
        return ypos;
    }

    public double getXvel() {
        return xvel;
    }

    public double getYvel() {
        return yvel;
    }

    public boolean isReturned() {
        return returned;
    }

    public boolean isLaunched() {
        return launched;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setXvel(double value) {
        xvel = value;
    }

    public void setYvel(double value) {
        yvel = value;
    }

    public void setXpos(double value) {
        xpos = value;
        hitbox.left = (int)xpos;
        hitbox.right = (int)xpos + diameter;
    }

    public void setYpos(double value) {
        ypos = value;
        hitbox.top = (int) ypos;
        hitbox.bottom = (int)ypos + diameter;
    }

    public void setReturned(boolean value) {
        returned = value;
    }

    public void setLaunched(boolean value) {
        launched = value;
    }

}
