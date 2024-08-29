package com.example.fivepointsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;

    private boolean isPlaying, hasLost, move, midWave;
    private int screenX, screenY;
    private int setPoint, score = 0;
    private long lastShotTime;

    private Background background;
    private Paint paint;
    private Shooter shooter;
    private Ball[] balls;
    private int ballCount = 0;
    private static final int MAX_BALLS = 100; // Define your maximum number of balls
    private Queue<Bullet> bullets;

    private boolean isShaking = false;
    private long shakeStartTime;
    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());

    private final int shooterLvl = 20, minShootingDiff = 30;

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
        balls = new Ball[MAX_BALLS];
        balls[0] = new Ball(getResources(), 200, screenX, screenY, true, 10);
        ballCount++;
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

        for (int i = 0; i < ballCount; i++) {
            Ball b = balls[i];
            if (b != null){
                b.updateLocation();
                if (((b.x + b.size / 2) > shooter.x && b.x < shooter.x + shooter.width / 2) && b.y > 1700 && !hasLost) {
                    move = false;
                    hasLost = true;
                    ((GameActivity)getContext()).showGameOverDialog();
                    isShaking = true;
                    shakeStartTime = System.currentTimeMillis();
                }
                if (b.hp <= 0){
                    System.arraycopy(balls, i + 1, balls, i, ballCount - i - 1);
                    ballCount--;
                    i--;
                    score++;
                }
            }
        }
        // move ball to position by speed and gravity

        if (!midWave){
            generateWave(2, 10, 50);
            midWave = true;
        }

        // move shooter to setpoint
        shooter.updateLocation(setPoint, move);

        // check for collision between the ball and the cannon

        // check for collision between ball and bullets
        for (Bullet bullet : bullets) {
            int id = bullet.checkBallCollision(balls, ballCount);
            if (id != -1){
                bullet.inScreen = false;
                try {
                    balls[id].hp--;
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
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextLocale(Locale.ENGLISH);
            paint.setLinearText(true);
            paint.setTextSize(118);
            String scoreStr = Integer.toString(score);
            canvas.drawText(scoreStr, (float) canvas.getWidth()/2, 250 , paint);

            for (int i = 0; i < ballCount; i++) {
                Ball ball = balls[i];
                if (ball != null && ball.show){
                    canvas.drawBitmap(ball.ball, ball.x, ball.y, paint);
                    canvas.drawText(Integer.toString(ball.hp), ball.x + (float) ball.size/2, ball.y + (float) ball.size/1.5f, paint);
                }
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void shoot(){
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - lastShotTime;
        if (diff > Math.max(300 - (10 * shooterLvl), minShootingDiff)){
            bullets.add(new Bullet(1, shooter.x + shooter.width/2, getResources()));
            lastShotTime = currentTime;
        }
    }

    public void resetGame(){
        balls = new Ball[MAX_BALLS];
        balls[0] = new Ball(getResources(), 200, screenX, screenY, true, 10);
        ballCount++;
        bullets = new LinkedList<>();
        shooter = new Shooter(screenX, getResources());
        hasLost = false;
        score = 0;
    }

    public void generateWave(int numBalls, int minHp, int maxHp) {
        for (int i = 0; i < numBalls; i++) {
            int delay = random.nextInt(1500) + 500;
            handler.postDelayed(() -> {
                createBall(minHp, maxHp);
            }, delay);
        }
    }

    private void createBall(int minHp, int maxHp) {
        if (ballCount >= MAX_BALLS) return; // Limit the number of balls

        int hp = random.nextInt(maxHp - minHp + 1) + minHp;
        float size = calculateSizeFromHp(hp);
        boolean generateOnRight = random.nextBoolean();

        balls[ballCount] = new Ball(getResources(), (int) size, screenX, screenY, generateOnRight, hp);
        ballCount++;
    }


    private float calculateSizeFromHp(int hp) {
        return (float) Math.sqrt(hp) * 50;
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