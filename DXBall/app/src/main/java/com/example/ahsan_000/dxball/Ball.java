package com.example.ahsan_000.dxball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by ahsan_000 on 03-Sep-16.
 */
public class Ball {

    private int gameOver=0;
    private int x;
    private int y;
    private int color;
    private int radius;
    private  int dx;
    private int dy;
    private Paint paint;

    public  Ball(int x,int y,int col,int radius){
        this.x=x;
        this.y=y;
        this.color=col;
        this.radius=radius;
        paint=new Paint();
        paint.setColor(Color.RED);
        dx=0;
        dy=0;
    }
    public int getX(){
        return x;
    }
    public int getGameOver(){
        return gameOver;
    }
    public int getY() {
        return y;
    }

    public int getC() {
        return color;
    }

    public int getRadius() {
        return radius;
    }

    public Paint getPaint() {
        return paint;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setColor(int col){
        color=col;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void moveBall(){
        x=x+dx;
        y=y+dy;
    }

    public void ballBoundaryCheck(Canvas canvas) {

        if((this.y-this.radius)>=canvas.getHeight()){

            GameCanvas.life-=1;
            GameCanvas.newLife=true;
            this.gameOver=1;
        }

        if(GameCanvas.life==0)
            GameCanvas.gameOver = true;

        if((this.x+this.radius)>=canvas.getWidth()
                || (this.x-this.radius)<=0){
            this.dx = -this.dx;
        }
        if( (this.y-this.radius)<=0){
            this.dy = -this.dy;
        }
    }


    public void bounce(Canvas canvas){
        moveBall();
        if(x==canvas.getWidth()||x<0){
            x=0;
            y=0;
        }
        if(y==canvas.getWidth()||y<0){
            x=0;
            x=0;
        }
    }
}
