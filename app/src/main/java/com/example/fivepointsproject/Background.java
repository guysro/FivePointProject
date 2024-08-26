package com.example.fivepointsproject;

import android.graphics.Bitmap;

public class Background {

    Bitmap background;
    int x, y;

    public Background(Bitmap background, int screenWidth, int screenHeight) {
        this.background = background;

        this.background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false);
        x = 0;
        y = 0;
    }
}
