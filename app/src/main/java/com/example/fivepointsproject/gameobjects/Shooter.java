package com.example.fivepointsproject.gameobjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.fivepointsproject.R;

public class Shooter {

    public int x, y, width, height, screenX;
    public Bitmap shooter;

    public Shooter(int screenWidth, Resources res) {
        shooter = BitmapFactory.decodeResource(res, R.drawable.cannon);

        width = shooter.getWidth()/2;
        height = shooter.getHeight()/2;

        shooter = Bitmap.createScaledBitmap(shooter, width, height, false);
        x = (screenWidth / 2) - width/2;
        screenX = screenWidth;
        y = 1775;
    }

    public void updateLocation(int setPoint, boolean move){
        int diff = Math.abs(x - setPoint);

        double minCannonSpeed = 10.0;
        double startSlowingCannon = 20.0;
        double maxCannonSpeed = 40.0;

        int speed = (int) Math.max(Math.min(Math.pow((diff/ startSlowingCannon), 5), maxCannonSpeed), minCannonSpeed);

        if (x > screenX-width+30 && setPoint > screenX-width+30){
            setPoint = screenX-width+30;
            speed = 0;
        }
        if (x < -30 && setPoint < -30){
            setPoint = -30;
            speed = 0;
        }
        if (!(diff < 15) && move)
            if (x < setPoint)
                x += speed;
            else if (x > setPoint)
                x -= speed;

    }
}
