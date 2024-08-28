package com.example.fivepointsproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Shooter {

    int x, y, width, height, screenX;
    Bitmap shooter;

    private final double maxCannonSpeed = 30.0;
    private final double minCannonSpeed = 10.0;
    private final double startSlowingCannon = 20.0;

    public Shooter(int screenWidth, Resources res) {
        shooter = BitmapFactory.decodeResource(res, R.drawable.cannon);

        width = shooter.getWidth()/2;
        height = shooter.getHeight()/2;

        shooter = Bitmap.createScaledBitmap(shooter, width, height, false);
        x = screenWidth-width+30;
        x = (screenWidth / 2) - width/2;
        screenX = screenWidth;
        y = 1775;
    }

    public void updateLocation(int setPoint, boolean move){
        int diff = Math.abs(x - setPoint);
        int speed = (int) Math.max(Math.min(Math.pow((diff/ startSlowingCannon), 5), maxCannonSpeed), minCannonSpeed);

        if (x > screenX-width+30 && setPoint > screenX-width+30){
            setPoint = screenX-width+30;
            speed = 0;
        }
        if (x < -30 && setPoint < -30){
            setPoint = -30;
            speed = 0;
        }
        if (diff < 15 || !move)
            return;
        else if (x < setPoint)
            x += speed;
        else if (x > setPoint)
            x -= speed;

    }
}
