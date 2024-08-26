package com.example.fivepointsproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Shooter {

    int x, y, width, height;
    Bitmap shooter;

    public Shooter(int screenWidth, int screenHeight, Resources res) {
        shooter = BitmapFactory.decodeResource(res, R.drawable.cannon);

        width = shooter.getWidth()/2;
        height = shooter.getHeight()/2;

        shooter = Bitmap.createScaledBitmap(shooter, width, height, false);
        x = screenWidth-width+30;
//        x = (screenWidth / 2) - width/2;
        y = 1775;
    }
}
