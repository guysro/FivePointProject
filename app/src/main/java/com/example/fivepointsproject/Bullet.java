package com.example.fivepointsproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.List;
import java.util.Queue;

public class Bullet {

    int x, y, height, width, lvl;
    boolean inScreen = true;
    Bitmap bullet;
    final int velocity = 30;

    public Bullet(int lvl, int x, Resources res) {
        this.lvl = lvl;
        this.x = x;
        y = 1815;

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
        if (y < -height){
            inScreen = false;
        }
    }

    public int checkBallCollision(List<Ball> balls){
        for (Ball ball : balls) {
            if (
                !(x + width < ball.x) &&
                !(x > ball.x + ball.size) &&
                !(y > ball.y + ball.size/2) &&
                !(y + height < ball.y))
            {
                return ball.id;
            }
        }
        return -1;
    }
}
