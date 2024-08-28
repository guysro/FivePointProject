package com.example.fivepointsproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;

public class Ball {

    double velX = 10, startVelY = 0, gravity = 1, velY;
    boolean positiveX = false, show = true;
    int x, y, size;
    Bitmap ball;
    int screenY, screenX;
    int hp = 10;
    int id;

    public Ball(Resources res, int size, int screenX, int screenY, boolean startRight, int id) {
        this.size = size;
        ball = BitmapFactory.decodeResource(res, R.drawable.ball);
        ball = Bitmap.createScaledBitmap(ball, size, size, false);

        changeColor(100, 100, 100);

        x = startRight ? screenX - 100 : 100;
        y = 600;
        this.screenY = screenY;
        this.screenX = screenX;
        velY = startVelY;
        this.id = id;
    }

    public void updateLocation(){
        if (show){
            velY += gravity;
            y += (int) velY;
            // Check for collision with the bottom of the screen
            if (y > 2070 - size) {
                y = 2070 - size;
                velY *= -1; // Reverse velocity and apply bounce factor
            }
            x += (int) (velX * (positiveX ? 1 : -1));
            if (x < 0){
                x = 0;
                positiveX = !positiveX;
            }
            if (x > screenX - size){
                x = screenX - size;
                positiveX = !positiveX;
            }
        }
    }

    private void changeColor(int r, int g, int b){
        for (int x = 0; x < ball.getWidth(); x++) {
            for (int y = 0; y < ball.getHeight(); y++) {
                int pixel = ball.getPixel(x, y);

                // Modify the color
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // Convert to color by r,g,b parameter
                int newPixel = Color.rgb(r, g, b);

                if (red > 0){
                    // Set the new pixel color
                    ball.setPixel(x, y, newPixel);
                }
            }
        }
    }
}
