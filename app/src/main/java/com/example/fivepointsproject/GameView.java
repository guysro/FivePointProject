package com.example.fivepointsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying;
    private final Paint paint;
    private final Shooter shooter;
    private final Background background;
    private final int screenX;
    private final int screenY;
    private int setPoint;
    private boolean move;
    private final double maxSpeed = 30.0;
    private final double minSpeed = 10.0;
    private final double slowStart = 20.0;

    public GameView(Context context) {
        super(context);

        this.screenY = 2412;
        this.screenX = 1080;

        paint = new Paint();
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), screenX, screenY);
        shooter = new Shooter(screenX, screenY, getResources());
    }

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.screenY = screenY;
        this.screenX = screenX;

        paint = new Paint();
        background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background), screenX, screenY);
        shooter = new Shooter(screenX, screenY, getResources());
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

    private void update() {
        int diff = Math.abs(shooter.x - setPoint);
        int speed = (int) Math.max(Math.min(Math.pow((diff/slowStart), 5), maxSpeed), minSpeed);
        if (shooter.x > screenX-shooter.width+30 && setPoint > screenX-shooter.width+30){
            setPoint = screenX-shooter.width+30;
            speed = 0;
        }
        if (shooter.x < -30 && setPoint < -30){
            setPoint = -30;
            speed = 0;
        }
        if (diff < 15 || !move)
            return;
        else if (shooter.x < setPoint)
            shooter.x += speed;
        else if (shooter.x > setPoint)
            shooter.x -= speed;
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
            canvas.drawBitmap(background.background, background.x, background.y, paint);
            canvas.drawBitmap(shooter.shooter, (int)(shooter.x), shooter.y, paint);
            getHolder().unlockCanvasAndPost(canvas);
        }
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