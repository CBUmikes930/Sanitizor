package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import java.util.Random;


/**
 * Created by Jesse Breslin on 10/9/2020
 *
 * The Enemy class along with its subclasses
 * May separate the subclasses into different class files later //TODO
 */


//TODO Add enemy movement commands;
//TODO Add attack command to each subclass
//TODO Create graphical components for each enemy type

public class Enemy {

    private final int WIDTH = 150;
    private int HEIGHT;
    private final int BOTTOM_PADDING = 50;
    private final int TOP_PADDING = 50;
    private final int SIDE_PADDING = 10;

    private double[] enemySpeeds;
    private Paint mPaint;
    private PointF mTopLeft;

    private Drawable mEnemyImage;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public Enemy enemy(Context context, int surfaceWidth, int surfaceHeight, PointF location){
        Random ran = new Random();
        int num = ran.nextInt(3);
        if(num == 0){
            return new Red(context, surfaceWidth, surfaceHeight, location);
        } else if (num ==1){
            return new Blue(context,surfaceWidth, surfaceHeight, location);
        } else {
            return new Yellow(context,surfaceWidth,surfaceHeight,location);
        }
    }

    public class Red extends Enemy{
        public Red(Context context, int surfaceWidth, int surfaceHeight, PointF location){
            assignEnemyImage(context, "enemy_red", surfaceWidth, surfaceHeight, location);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(0xffffaaaa);
        }

    }

    public class Yellow extends Enemy{
        public Yellow(Context context, int surfaceWidth, int surfaceHeight, PointF location){
            assignEnemyImage(context, "enemy_yellow", surfaceWidth, surfaceHeight, location);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(0xffbffaa);
        }
    }

    public class Blue extends Enemy{
        public Blue(Context context, int surfaceWidth, int surfaceHeight, PointF location){
            assignEnemyImage(context, "enemy_blue", surfaceWidth, surfaceHeight, location);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(0xffaaaaff);
        }


    }

    public void setPosition(int x, int y){
        mTopLeft.x = x;
        mTopLeft.y = y;
    }

    public void assignEnemyImage(Context context, String name, int surfaceWidth, int surfaceHeight, PointF location){
        int imageID = context.getResources().getIdentifier(name, "drawable", "com.egr423.sanitizor");
        mEnemyImage = ResourcesCompat.getDrawable(context.getResources(), imageID, null);
        if (mEnemyImage != null){
            HEIGHT = (int) (WIDTH * ((float)mEnemyImage.getIntrinsicHeight()/(float) mEnemyImage.getIntrinsicWidth()));
        } else {
            Log.d("Enemy Error", "Could not load mEnemyImage from resource: R.drawable." +name );
        }

        mSurfaceHeight = surfaceHeight;
        mSurfaceWidth = surfaceWidth;

        mTopLeft = location;

    }

    public int getWIDTH(){
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getBOTTOM_PADDING(){
        return BOTTOM_PADDING;
    }

    public int getSIDE_PADDING() {
        return SIDE_PADDING;
    }

    public int getTOP_PADDING() {
        return TOP_PADDING;
    }

    public void draw(Canvas canvas) {
        if (mEnemyImage != null) {
            //If we have a player image, then draw it
            mEnemyImage.setBounds((int) mTopLeft.x, (int) mTopLeft.y, (int) mTopLeft.x + WIDTH, (int) mTopLeft.y + HEIGHT);
            mEnemyImage.draw(canvas);
        } else {
            //If we don't have a player image, then draw a circle
            canvas.drawCircle(mTopLeft.x + (float) (WIDTH / 2), mTopLeft.y + (float) (WIDTH / 2), (float) WIDTH / 2, mPaint);
        }

    }
}
