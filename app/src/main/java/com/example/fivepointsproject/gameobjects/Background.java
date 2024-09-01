package com.example.fivepointsproject.gameobjects;

import android.graphics.Bitmap;

public class Background {

    public Bitmap background;
    public int x;
    public int y;

    public Background(Bitmap background, int screenWidth, int screenHeight) {
        this.background = background;

        this.background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);
        x = 0;
        y = 0;
    }
}
