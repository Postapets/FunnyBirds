package ru.samsung.itschool.funnybirds;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class GameView extends View{

    private Sprite playerBird;
    private Sprite enemyBird;
    private Sprite touchBird;
    private Sprite Bonus;


    private int viewWidth;
    private int viewHeight;

    private int pauseX;
    private int pauseY;
    private boolean paused;
    private Bitmap pause;

    private int points = 0;

    private int level = 1;

    private Timer t;

    private final int timerInterval = 30;

    public GameView(Context context) {
        super(context);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth()/5;
        int h = b.getHeight()/3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i ==0 && j == 0) {
                    continue;
                }
                if (i ==2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth()/5;
        h = b.getHeight()/3;
        firstFrame = new Rect(4*w, 0, 5*w, h);

        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i ==0 && j == 4) {
                    continue;
                }

                if (i ==2 && j == 0) {
                    continue;
                }

                enemyBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.touchbird);
        w = b.getWidth()/5;
        h = b.getHeight()/3;
        firstFrame = new Rect(0, 0, w, h);
        touchBird = new Sprite(2000, 400, -300, 0, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i ==0 && j == 4) {
                    continue; }

                if (i ==2 && j == 0) {
                    continue; }
                touchBird.addFrame(new Rect(j*w+10, i*h+10, j*w+w+10, i*w+w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.bonus);
        b = Bitmap.createScaledBitmap(b,140,140,true);
        w = b.getWidth();
        h = b.getHeight();
        firstFrame = new Rect(0,0,w,h);
        Bonus = new Sprite(2000,500,-150,0,firstFrame,b);

        paused = false;
        pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        pause = Bitmap.createScaledBitmap(pause,130,130,true);

        t = new Timer();
        t.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        //canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        enemyBird.draw(canvas);
        touchBird.draw(canvas);
        Bonus.draw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(45.0f);
        p.setColor(Color.BLACK);

        pauseX = getWidth()-150;
        pauseY = getHeight()-150;
        canvas.drawBitmap(pause, pauseX, pauseY, p);
        canvas.drawText("?????????????? "+level, viewWidth - 250, 120, p);
        canvas.drawText("????????: "+points, viewWidth - 250, 70, p);
        if(paused){
            p.setTextSize(100);
            p.setColor(Color.BLACK);
            canvas.drawText("??????????",viewWidth/2-120,viewHeight/2,p);
        }
    }

    protected void update () {
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval);
        touchBird.update(timerInterval);
        Bonus.update(timerInterval);
        //
        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        //???????????????? ???????????????????? ?????? ?????????????? ????????, ???????? ????????
        if (enemyBird.getX() < - enemyBird.getFrameWidth()) {
            teleportAnySprite(enemyBird);
            points +=10;
        }
        //???????????????? ???????????????????? ?????? ?????????????? ?????????? ??????????, ?????????? ????????
        if (enemyBird.intersect(playerBird)) {
            teleportAnySprite(enemyBird);
            points -= 30;
        }
        //???????????????? ?????????????????? ?????? ?????????????? ????????, ?????????? ????????
        if (touchBird.getX() < - touchBird.getFrameWidth()) {
            teleportAnySprite(touchBird);
            points -=15;
        }
        //???????????????? ?????????????????? ?????? ?????????????? ?????????? ????????????, ?????????? ????????
        if (touchBird.intersect(playerBird)) {
            teleportAnySprite(touchBird);
            points -= 30;
        }
        //???????????????? ???????????? ?????? ?????????????????????? ??????????????
        if (Bonus.getX() < - Bonus.getFrameWidth()) {
            teleportAnySprite(Bonus);
        }
        //???????????????? ???????????? ?????? ?????????????? ?????????? ????????????
        if (Bonus.intersect(playerBird)) {
            teleportAnySprite(Bonus);
            points += 15;
        }
        //???????? ?????????????? ???????????? 200, ???????????? ??????????????
        if (points>=200){
            NextLevel();
        }
        //???????? ?????????????? ?????????????? -60, ?????????? ????????
        if(points<=-60){
            EndGame();
        }
        //??????????????????????
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN)  {
            //?????????????????? ?????????????? (??????????)
            if (event.getX() >= pauseX-100 && event.getY() >= pauseY-100){
                if(!paused){
                    paused = true;
                    t.cancel();
                    invalidate();
                }
                else if (paused){
                    paused = false;
                    t.start();
                }
            }
            //?????????????????? ?????????????? (????????????)
            else {
                if(event.getX() <= touchBird.getX()+touchBird.getFrameWidth() && event.getX() >= touchBird.getX()
                        && event.getY() <= touchBird.getY()+touchBird.getFrameHeight() && event.getY() >= touchBird.getY()){
                    teleportAnySprite(touchBird);
                    points+=15;
                }
                else if (event.getY() < playerBird.getBoundingBoxRect().top) {
                    playerBird.setVy(-100);
                    points--;
                } else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                    playerBird.setVy(100);
                    points--;
                }
            }
        }

        return true;
    }

    //?????????? ???????????????????????? ????????????????
    private void teleportAnySprite (Sprite sprite) {
        sprite.setX(viewWidth + Math.random() * 300);
        sprite.setY(Math.random() * (viewHeight - sprite.getFrameHeight()));
    }

    private void NextLevel(){
        level++;
        enemyBird.setVx(enemyBird.getVx()-40);
        touchBird.setVx(touchBird.getVx()-40);
        points = 0;
        Toast.makeText(getContext(),"?????????????? ??????????????!",Toast.LENGTH_SHORT);
    }

    private void EndGame(){
        t.cancel();
        LayoutInflater li = LayoutInflater.from(getContext());
        View diView = li.inflate(R.layout.restartdialog, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());
        mDialogBuilder.setView(diView);
        mDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
        diView.findViewById(R.id.button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                level = 1;
                points = 0;
                playerBird.setY(0);
                enemyBird.setVx(-300);
                touchBird.setVx(-300);
                teleportAnySprite(enemyBird);
                teleportAnySprite(touchBird);
                teleportAnySprite(Bonus);
                t.start();
                alertDialog.cancel();
            }
        });
    }
    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update ();
        }

        @Override
        public void onFinish() {

        }
    }
}
