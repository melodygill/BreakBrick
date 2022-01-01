package com.example.breakbrick;

import android.graphics.Rect;

public class Block {

    //INSTANCE VARIABLES
    //Position and velocity are measured in "meters", converted from pixels
    //Ensures that the blocks take up the same amount of screen on every screen size
    //xpos and ypos refer to the top left corner
    private double xposMeters, yposMeters;
    private double xposGrid, yposGrid; //blocks are also defined by their place in the row/col grid
    private int value; //how many hits it would take to break the block
    private Rect hitbox; //For collision detection
    private int length; //Length of square block in meters. Will be dimensions of square hitbox


    //METHODS
    //constructor
    public Block(int x, int y, int val) {
        length = Utility.blockLength;
        xposGrid = x;
        yposGrid = y;
        xposMeters = x * length;
        yposMeters = y * length;
        value = val;

        //consider converting xpos and ypos to pixels so that the hitbox is more accurate
        hitbox = new Rect((int)xposMeters, (int)yposMeters, (int)xposMeters+length, (int)yposMeters+length);
    }

    //Shifts block down by 1 row; updates y-pos variables and hitbox
    //To be called at the end of a turn
    public void moveDown() {
        yposGrid++;
        yposMeters += length;
        hitbox.top = (int) yposMeters;
        hitbox.bottom = (int) yposMeters + length;
    }

    //Getters and setters
    public double getXposMeters() {
        return xposMeters;
    }

    public double getYposMeters() {
        return yposMeters;
    }

    public double getYposGrid() {
        return yposGrid;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public int getValue() {
        return value;
    }

    //To be called when the block is hit
    public void decreaseValue() {
        value -= 1;
    }
}
