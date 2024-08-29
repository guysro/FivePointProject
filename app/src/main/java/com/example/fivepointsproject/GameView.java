package com.example.fivepointsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;

    private boolean isPlaying, hasLost, move, lastWasRight = true;
    private int screenX, screenY;
    private int setPoint, score = 0;
    private long lastShotTime;

    private Background background;
    private Paint paint;
    private Shooter shooter;
    private List<Ball> balls;
    private Queue<Bullet> bullets;

    private boolean isShaking = false;
    private long shakeStartTime;

    private final int shooterLvl = 10;

    public GameView(Context context) {
        super(context);
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
        catch (InterruptedException ignored){

        }
    }

    synchronized
    private void update() {

        // add a new bullet to bullets list if adequate time has elapsed
        if (move)
            shoot();

        // move ball to position by speed and gravity
        for (Ball b : balls) {
            b.updateLocation();
            if (((b.x + b.size / 2) > shooter.x && b.x < shooter.x + shooter.width / 2) && b.y > 1700 && !hasLost) {
                move = false;
                hasLost = true;
                ((GameActivity)getContext()).showGameOverDialog();
                isShaking = true;
                shakeStartTime = System.currentTimeMillis();
            }
            if (b.hp <= 0){
                balls.remove(b);
                score++;
            }
        }
        if (balls.isEmpty()){
            balls.add(new Ball(getResources(), 200, screenX, screenY, lastWasRight, 0));
            lastWasRight = !lastWasRight;
        }

        // move shooter to setpoint
        shooter.updateLocation(setPoint, move);

        // check for collision between the ball and the cannon

        // check for collision between ball and bullets
        for (Bullet bullet : bullets) {
            int id = bullet.checkBallCollision(balls);
            if (id != -1){
                bullet.inScreen = false;
                try {
                    balls.get(id).hp--;
                }
                catch (IndexOutOfBoundsException ignored){}
                System.out.println("HIT!");
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(9);
        }
        catch (InterruptedException ignored){}
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            if (isShaking) {
                long elapsedTime = System.currentTimeMillis() - shakeStartTime;

                if (elapsedTime < 500) {
                    // Calculate random offsets for shaking
                    int offsetX = (int) (Math.random() * 10 * 2 - 10);
                    int offsetY = (int) (Math.random() * 10 * 2 - 10);

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
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(118);
            String scoreStr = Integer.toString(score);
            canvas.drawText(scoreStr, (float) canvas.getWidth()/2, 250 , paint);
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
        score = 0;
    }

    @SuppressLint("ClickableViewAccessibility")
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