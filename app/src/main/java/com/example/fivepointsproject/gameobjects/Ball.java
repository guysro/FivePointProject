package com.example.fivepointsproject.gameobjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.fivepointsproject.R;

public class Ball {

    double velX = 10, startVelY = 0, gravity = 1, velY;
    boolean positiveX = false;
    public boolean show = true;
    boolean started = false;
    public int x, y, size;
    public Bitmap ball;
    int screenY, screenX;
    public double hp = 10;

    public Ball(Resources res, int size, int screenX, int screenY, boolean startRight, double hp) {
        this.size = size;
        ball = BitmapFactory.decodeResource(res, R.drawable.ball);
        ball = Bitmap.createScaledBitmap(ball, size, size, false);

        changeColor();

        x = startRight ? screenX + size/2: (int)(-1.5 * size);
        y = 600;
        positiveX = !startRight;
        this.screenY = screenY;
        this.screenX = screenX;
        velY = startVelY;
        this.hp = hp;
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
            if (x < 0 && started){
                x = 0;
                positiveX = !positiveX;
            }
            if (x > screenX - size && started){
                x = screenX - size;
                positiveX = !positiveX;
            }
            if (x < screenX - size && x > 0){
                started = true;
            }
        }
    }

//    private void changeColor(int r, int g, int b){
    private void changeColor(){
        for (int x = 0; x < ball.getWidth(); x++) {
            for (int y = 0; y < ball.getHeight(); y++) {
                int pixel = ball.getPixel(x, y);

                // Modify the color
                int red = Color.red(pixel);
//                int green = Color.green(pixel);
//                int blue = Color.blue(pixel);

                // Convert to color by r,g,b parameter
                int newPixel = Color.rgb(0, 0, 255);

                if (red > 0){
                    // Set the new pixel color
                    ball.setPixel(x, y, newPixel);
                }
            }
        }
    }
}
