package com.example.ahsan_000.dxball;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;


/**
 * Created by ahsan_000 on 03-Sep-16.
 */
public class GameCanvas extends View implements Runnable {


    public static boolean gameOver;
    public static boolean newLife;
    public static boolean end;
    public static int life;
    public static int stage = 1, brickSize = 20;

    public  static int canvasHeight,canvasWidth;
    Paint paint;
    float barWidth=300;
    float brickX=0, brickY=80;
    int score=0,speed=5,r,l;
    Ball myBall;
    Bar myBar;
    float left,right,top,bottom;
    float downX,downY,upX,upY;
    boolean leftPos,rightPos,first=true,second=false;
    int min_distance=50;
    int ballSpeed;
    public static int checkWidth=0;
    final MediaPlayer mp;

    ArrayList<Bricks> bricks=new ArrayList<Bricks>();

    public GameCanvas(Context context, MediaPlayer mp) {
        super(context);
        this.mp = mp;
        paint=new Paint();
        myBar=new Bar();
        life=3;
        gameOver=false;
        end=false;
        newLife=true;

    }


    //Draw canvas
    @Override
    protected void onDraw(Canvas canvas)
    {
        canvasHeight = canvas.getHeight();
        canvasWidth = canvas.getWidth();

        if(first == true)
        {
            first = false;
            for(int i = 0; i < brickSize; i++)
            {
                int brickColor;
                if(brickX >= canvas.getWidth())
                {
                    brickX = 0;
                    brickY += 100;
                }
                brickColor = Color.rgb(255, 215, 0);
                bricks.add(new Bricks(brickX+2,brickY,brickX+canvas.getWidth()/5-5,brickY+95,brickColor));
                brickX += canvas.getWidth()/5;
            }
            brickSize += 5;

            myBall = new Ball(canvas.getWidth()/2,canvas.getHeight()/2,Color.CYAN,20);
            myBall.bounce(canvas);

            left = getWidth()/2 - (barWidth / 2);
            top = getHeight() - 20;
            right = getWidth()/2+(barWidth/2);
            bottom = getHeight();

            myBar.setBottom(bottom);
            myBar.setLeft(left);
            myBar.setRight(right);
            myBar.setTop(top);
            checkWidth = canvas.getWidth();

            myBall.setDx(4);
            myBall.setDy(4);
            Log.d("", bricks.size() + "");

        }

        if(newLife)
        {
            mp.start();
            newLife = false;
            //new ball
            ballSpeed = 5;
            myBall=new Ball(canvas.getWidth()/2,canvas.getHeight()-50,Color.GREEN,20);
            myBall.setDx(ballSpeed);
            myBall.setDy(-ballSpeed);
        }
        canvas.drawRGB(255, 255, 255);
        paint.setColor(Color.rgb(165,42,42));
        paint.setStyle(Paint.Style.FILL);

        //Ball
        canvas.drawCircle(myBall.getX(), myBall.getY(), myBall.getRadius(), myBall.getPaint());
        paint.setTextSize(30);
        paint.setFakeBoldText(true);
        String str = "Point: " + score + " (Life: " +  life + ")";
        canvas.drawText(str,10,30,paint);

        //Bar
        canvas.drawRect(myBar.getLeft(), myBar.getTop(), myBar.getRight(), myBar.getBottom(), myBar.getPaint());

        //bricks
        for(int i=0;i<bricks.size();i++)
        {
            canvas.drawRect(bricks.get(i).getLeft(),bricks.get(i).getTop(),bricks.get(i).getRight(),bricks.get(i).getBottom(),bricks.get(i).getPaint());
        }

        if(gameOver){
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

            paint.setColor(Color.BLUE);
            paint.setTextSize(50);
            paint.setFakeBoldText(true);
            canvas.drawText("Game OVER!",canvas.getWidth()/2-110,canvas.getHeight()/2,paint);
            canvas.drawText("FINAL SCORE: "+score,canvas.getWidth()/2-150,canvas.getHeight()/2+60,paint);
            gameOver = false;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(gameOver == false)
        {
            this.ballBrickCollision(bricks,myBall,canvas);
            this.ballBarCollision(myBar,myBall, canvas);
            myBall.ballBoundaryCheck(canvas);

            if(bricks.isEmpty()) {
                ballSpeed += 1; //increasing ball's speed
                reDrawBricks(canvas);
            }
            myBall.moveBall();

            myBar.moveBar(leftPos,rightPos);
            this.run();
        }
        else {
            gameOver = true;
        }
    }
    public void ballBarCollision(Bar myBar,Ball myBall,Canvas canvas)
    {
        if(((myBall.getY()+myBall.getRadius())>=myBar.getTop())&&((myBall.getY()+myBall.getRadius())<=myBar.getBottom())&& ((myBall.getX())>=myBar.getLeft())&& ((myBall.getX())<=myBar.getRight()))
        {
            myBall.setDy(-(myBall.getDy()));
        }

    }
    public void ballBrickCollision(ArrayList<Bricks> brick ,Ball myBall,Canvas canvas){
        for(int i=0; i < brick.size(); i++)
        {
            if (((myBall.getY() - myBall.getRadius()) <= brick.get(i).getBottom()) && ((myBall.getY() + myBall.getRadius()) >= brick.get(i).getTop()) && ((myBall.getX()) >= brick.get(i).getLeft()) && ((myBall.getX()) <= brick.get(i).getRight()))
            {
                brick.remove(i);
                mp.start();
                if(brick.isEmpty())
                {
                    newLife = true;
                    life += l;
                }
                score+=10;
                myBall.setDy(-(myBall.getDy()));
            }
        }
    }

    public void reDrawBricks(Canvas canvas)
    {
        life += l;
        brickY = 0;
        stage += 1;
        for(int i = 0; i < brickSize; i++)
        {
            int brickColor;
            if(brickX >= canvas.getWidth())
            {
                brickX = 0;
                brickY += 100;
            }
            if(stage == 2)
                brickColor = Color.rgb(148, 0, 211);
            if (stage == 3)
                brickColor = Color.rgb(255, 160, 122);
            if(stage == 4)
                brickColor = Color.rgb(160, 32, 240);
            if(stage == 5)
                brickColor = Color.rgb(255, 127, 80);
            else
            {
                gameOver = true;
                return;
            }

            bricks.add(new Bricks(brickX+2,brickY,brickX+canvas.getWidth()/5-5,brickY+95,brickColor));
            brickX += canvas.getWidth()/5;
        }
        brickSize += 5;
        for(int i=0;i<bricks.size();i++)
        {
            canvas.drawRect(bricks.get(i).getLeft(),bricks.get(i).getTop(),bricks.get(i).getRight(),bricks.get(i).getBottom(),bricks.get(i).getPaint());
        }
    }


    //Moving Bar
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                downX=event.getX();
                downY=event.getY();
                return true;

            }
            case MotionEvent.ACTION_UP:{
                upX=event.getX();
                upY=event.getY();

                float deltaX=downX-upX;
                float deltaY=downY-upY;

                if(Math.abs(deltaX) > Math.abs(deltaY)){
                    if(Math.abs(deltaX) > min_distance) {
                        if (deltaX < 0) {
                            //left=left+100;
                            //right=right+100;
                            leftPos=true;
                            rightPos=false;
                            myBar.moveBar(leftPos, rightPos);
                            return true;
                        }
                        if (deltaX > 0) {
                            leftPos=false;
                            rightPos=true;
                            myBar.moveBar(leftPos,rightPos);
                            //Right to left
                            return true;
                        }
                    }
                    else{
                        return  false;
                    }
                }
                else{
                    if(Math.abs(deltaY) > min_distance) {
                        if (deltaY < 0) {
                            //top to bottom
                            return true;
                        }
                        if (deltaY > 0) {
                            //bottom to top
                            return true;
                        }
                    }
                    else{
                        return  false;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void run() {

        invalidate();
    }

}
