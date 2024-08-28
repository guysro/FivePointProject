package com.example.fivepointsproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bullet {

    int x, y, height, width, lvl;
    boolean inScreen;
    Bitmap bullet;
    final int velocity = 30;

    public Bullet(int lvl, int x, Resources res) {
        this.lvl = lvl;
        this.x = x;
        y = 1770;

        switch (lvl){
            case 1:
                bullet = BitmapFactory.decodeResource(res, R.drawable.bulletlvl1);
                height = 33;
                width = 10;
                break;
            case 2:
                bullet = BitmapFactory.decodeResource(res, R.drawable.bulletlvl2);
                height = 33;
                width = 10;
                break;
            case 3:
                bullet = BitmapFactory.decodeResource(res, R.drawable.bulletlvl3);
                height = 33;
                width = 10;
                break;
        }

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);
    }

    public void updateLocation(){
        y -= velocity;
        inScreen = y > -height;
    }
}
