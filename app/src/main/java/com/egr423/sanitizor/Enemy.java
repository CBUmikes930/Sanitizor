package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.HeaderViewListAdapter;

import androidx.core.content.res.ResourcesCompat;

import java.util.Random;

// THIS IS A TEST COMMIT TO JESSE's CONTRIBUTION
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
    private final int SPEED = 2;

    private double[] enemySpeeds;
    private Paint mPaint;
    private PointF mTopLeft;

    private Drawable mEnemyImage;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private boolean wrappingx = false;
    private boolean wrappingy = false;


    public Enemy(String color, Context context, int surfaceWidth, int  surfaceHeight, PointF location){
        mEnemyImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy_red,null);
        if (mEnemyImage != null){
            HEIGHT = (int) (WIDTH * ((float) mEnemyImage.getIntrinsicHeight() /(float) mEnemyImage.getIntrinsicWidth()));
        } else {
            Log.d("Enemy Error", "Could not load mEnemyImage from resource: R.drawable.enemy_" +color);
            HEIGHT=WIDTH;
        }
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffffaaaa);

        mTopLeft = location;
    }

    public void move (PointF velocity){
        mTopLeft.offset((velocity.x*SPEED) , (velocity.y*SPEED));

        if(mTopLeft.x + WIDTH <= mSurfaceWidth && mTopLeft.x > 0){
            wrappingx = false;
        }
        if(mTopLeft.y +HEIGHT <= mSurfaceHeight && mTopLeft.y > 0){
            wrappingy = false;
        }

        if(!wrappingy) {
            if (mTopLeft.y > mSurfaceHeight) {
                mTopLeft.y = -HEIGHT;
                wrappingy = true;
            }
        }
        if(!wrappingx)
            if (mTopLeft.x > mSurfaceWidth) {
                mTopLeft.x = -WIDTH;
                wrappingx = true;
            }
            if (mTopLeft.x < -WIDTH) {
                mTopLeft.x = WIDTH+mSurfaceWidth;
                wrappingx = true;
            }
    }

    private void wrapScreen() {

    }

    public void setPosition(int x, int y){
        mTopLeft.x = x;
        mTopLeft.y = y;
    }

    public PointF getPostition(){
        return new PointF(mTopLeft.x, mTopLeft.y);
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
