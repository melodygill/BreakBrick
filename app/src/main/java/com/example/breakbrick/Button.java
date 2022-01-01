package com.example.breakbrick;

import android.graphics.Rect;

public class Button {

    //INSTANCE VARIABLES
    private Rect hitbox;
    private String text;

    //METHODS
    public Button(int left, int top, int screenX, int minYPixels, String s) {
        int right = left + (screenX / Utility.buttonLengthFactor);
        int bottom = top + (minYPixels / Utility.buttonHeightFactor);
        hitbox = new Rect(left, top, right, bottom);
        text = s;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public String getText() {
        return text;
    }

    public void setHitbox(Rect hitbox1) {
        hitbox = hitbox1;
    }
}
