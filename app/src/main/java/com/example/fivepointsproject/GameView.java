package com.example.fivepointsproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, hasLost;
    private final Paint paint;
    private Shooter shooter;
    private List<Ball> balls;
    private final Background background;
    private Queue<Bullet> bullets;
    private long lastShotTime;
    private final int screenX;
    private final int screenY;
    private int setPoint;
    private boolean move;

    private boolean isShaking = false;
    private long shakeStartTime;
    private int shakeDuration = 500; // Duration of the shake effect in milliseconds
    private int shakeMagnitude = 10; // Magnitude of the shake effect


    private final int shooterLvl = 10;


    public GameView(Context context) {
        super(context);

        this.screenY = 2412;
        this.screenX = 1080;

        paint = new Paint();
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), screenX, screenY);
        shooter = new Shooter(screenX, getResources());
        balls = new LinkedList<>();
        bullets = new LinkedList<>();
    }

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.screenY = screenY;
        this.screenX = screenX;

        paint = new Paint();
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), screenX, screenY);
        shooter = new Shooter(screenX, getResources());
        balls = new ArrayList<>();
        balls.add(new Ball(getResources(), 200, screenX, screenY, true, 0));
        bullets = new LinkedList<>();
        lastShotTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (isPlaying) {

            update();

            draw();

            sleep();
        }
    }

    public void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){
        try {
            isPlaying = false;
            thread.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    synchronized
    private void update() {

        // add a new bullet to bullets list if adequate time has elapsed
        if (move)
            shoot();

        // move ball to position by speed and gravity
        synchronized (balls) {
            for (Ball b : balls) {
                b.updateLocation();
                if (((b.x + b.size / 2) > shooter.x && b.x < shooter.x + shooter.width / 2) && b.y > 1700 && !hasLost) {
                    hasLost = true;
                    ((GameActivity)getContext()).showGameOverDialog();
                    isShaking = true;
                    shakeStartTime = System.currentTimeMillis();
                }
                if (b.hp <= 0)
                    balls.remove(b);
            }
            if (balls.isEmpty()){
                balls.add(new Ball(getResources(), 200, screenX, screenY, false, 0));
            }
        }

        // move shooter to setpoint
        shooter.updateLocation(setPoint, move);

        // check for collision between the ball and the cannon

        // check for collision between ball and bullets
        synchronized (balls){
            synchronized (bullets){
                for (Bullet bullet : bullets) {
                    int id = bullet.checkBallCollision(balls);
                    if (id != -1){
                        bullet.inScreen = false;
                        try {
                            balls.get(id).hp--;
                        }
                        catch (IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                        System.out.println("HIT!");
                    }
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(9);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            if (isShaking) {
                long elapsedTime = System.currentTimeMillis() - shakeStartTime;

                if (elapsedTime < shakeDuration) {
                    // Calculate random offsets for shaking
                    int offsetX = (int) (Math.random() * shakeMagnitude * 2 - shakeMagnitude);
                    int offsetY = (int) (Math.random() * shakeMagnitude * 2 - shakeMagnitude);

                    // Apply the offsets to the canvas
                    canvas.translate(offsetX, offsetY);
                } else {
                    // Stop shaking after the duration has passed
                    isShaking = false;
                }
            }


            canvas.drawBitmap(background.background, background.x, background.y, paint);
            canvas.drawBitmap(shooter.shooter, (shooter.x), shooter.y, paint);
            for (Bullet bullet : bullets) {
                bullet.updateLocation();
                if (bullet.inScreen){
                    canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
                }
            }
            for (Ball ball : balls) {
                if (ball.show)
                    canvas.drawBitmap(ball.ball, ball.x, ball.y, paint);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }
    synchronized
    private void shoot(){
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - lastShotTime;
        if (diff > 300 - (10 * shooterLvl)){
            bullets.add(new Bullet(1, shooter.x + shooter.width/2, getResources()));
            lastShotTime = currentTime;
        }
    }

    public void resetGame(){
        balls = new ArrayList<>();
        bullets = new LinkedList<>();
        shooter = new Shooter(screenX, getResources());
        hasLost = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                move = true;
                break;
            case MotionEvent.ACTION_UP:
                move = false;
                break;
        }
        setPoint = (int) (event.getX() - shooter.width/2.0);
        return true;
    }
}