package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

// THIS IS A TEST COMMIT TO JESSE's CONTRIBUTION

/**
 * Created by Jesse Breslin on 10/9/2020
 * <p>
 * The Enemy class along with its subclasses
 * May separate the subclasses into different class files later //TODO
 */


//TODO Add enemy movement commands;
//TODO Add attack command to each subclass
//TODO Create graphical components for each enemy type

public class Enemy extends Character {

    private double[] enemySpeeds;
    private final int TIME_TO_ATTACK = 20;
    private long lastAttacked;
    private long attackTime;
    private Rect gridPos;

    private PointF gridSpeed;

    private boolean isAttacking;
    private boolean isReturning;
    private boolean atOriginalPos =false;



    private boolean wrappingx = false;
    private boolean wrappingy = false;
    private boolean wrappingGx = false;
    private boolean wrappingGy = false;

    public Enemy(String color, Context context, Point location) {
        mImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy, null);
        if (mImage != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage.getIntrinsicWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mImage.getIntrinsicHeight() * SanitizorGame.pixelMultiplier));
            bounds.offsetTo(location.x, location.y);
        } else {
            //Log.d("Enemy Error", "Could not load mEnemyImage from resource: R.drawable.enemy_" + color);
        }
        SPEED = .5;
        gridPos = new Rect(bounds);
        atOriginalPos = true;
        // 0 0     gridspeed*20  bounds*50
    }

    //To check if I can attack, I have to check if it has been enough time since I last attacked.
    public boolean checkAttack(){
        checkAtOriginalPos();
        final int ATTACK_COOL_DOWN = 5000;
        if(isAttacking || System.currentTimeMillis()-lastAttacked >= ATTACK_COOL_DOWN){
            gridPos = new Rect(bounds);
            return true;
        } else{
            return false;
        }
    }
    // Attacking
    public void attack(){
        moveGridPos();
        wrapGrid();
        checkAtOriginalPos();
        // How long should the attack last
        final int ATTACK_PHASE = 4000;
        // If I am attacking reset the last attack variable to current time
        if(!isAttacking) {
            isAttacking = true;
            attackTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis()-attackTime <= ATTACK_PHASE){
            //Log.d("Enemy.attack","Enemy should be attacking");
            moveDown(new PointF(0,40));
        } else {
            Log.d("Enemy Movement","" +gridPos.left +"," + gridPos.top);
            isReturning= true;
            //Log.d("Enemy.attack", "Enemy stopped attacking after " +(System.currentTimeMillis()-attackTime));
            isAttacking = false;
        }
    }

    public void returnToOriginalPos(){
        moveGridPos();
        wrapGrid();
        checkAtOriginalPos();
        if(!atOriginalPos) {
            Log.d("Enemy Movement","" +gridPos.left +"," + gridPos.top);
            int x = gridPos.left-bounds.left;
            int y = gridPos.top-bounds.top;
            double direction = Math.atan((0.0+y)/x);
            int sign = 1;
            if(x < 0){
                sign = -1;
            }
            int speed = 40;
            bounds.offset((int) ((speed * Math.cos(direction) * sign)), (int) (Math.ceil(speed * Math.sin(direction) *sign)));
            wrapScreen();
            isReturning=true;
            //Log.d("Enemy.returnToAttackPos", "Enemy is returning");
        } else {
            //Log.d("Enemy.returnToAttackPos", "Enemy is no longer returning");
            lastAttacked = System.currentTimeMillis();
            isReturning = false;
        }
    }

    public void moveDown(PointF velocity){
        wrapGrid();
//        checkAtOriginalPos();
        bounds.offset(0, (int) (velocity.y * SPEED));
        wrapScreen();
    }

    public void move(PointF velocity) {
        gridSpeed = velocity;
        moveGridPos();
//        checkAtOriginalPos();
        bounds.offset((int) (velocity.x * SPEED), (int) (velocity.y * SPEED));
        wrapScreen();
        wrapGrid();
    }

    private void wrapScreen() {
        if (bounds.right <= SanitizorGame.mSurfaceWidth && bounds.left > 0) {
            wrappingx = false;
        }
        if (bounds.bottom <= SanitizorGame.mSurfaceHeight && bounds.top > 0) {
            wrappingy = false;
        }

        if (!wrappingy && bounds.top > SanitizorGame.mSurfaceHeight) {
            bounds.offsetTo(bounds.left, -bounds.height());
            wrappingy = true;
        }

        if (!wrappingx) {
            if (bounds.left > SanitizorGame.mSurfaceWidth) {
                bounds.offsetTo(-bounds.width(), bounds.top);
                wrappingx = true;
            }
            if (bounds.left < -bounds.width()) {
                bounds.offsetTo(SanitizorGame.mSurfaceWidth, bounds.top);
                wrappingx = true;
            }
        }
    }

    private void wrapGrid(){
        if (gridPos.right <= SanitizorGame.mSurfaceWidth && gridPos.left > 0) {
            wrappingGx = false;
        }
        if (gridPos.bottom <= SanitizorGame.mSurfaceHeight && gridPos.top > 0) {
            wrappingGy = false;
        }

        if (!wrappingGy && gridPos.top > SanitizorGame.mSurfaceHeight) {
            gridPos.offsetTo(gridPos.left, -gridPos.height());
            wrappingGy = true;
        }

        if (!wrappingGx) {
            if (gridPos.left > SanitizorGame.mSurfaceWidth) {
                gridPos.offsetTo(-gridPos.width(), gridPos.top);
                wrappingGx = true;
            }
            if (gridPos.left < -gridPos.width()) {
                gridPos.offsetTo(SanitizorGame.mSurfaceWidth, gridPos.top);
                wrappingGx = true;
            }
        }
    }

    public void setPosition(int x, int y) {
        bounds.offsetTo(x,y);
    }


    public void draw(Canvas canvas) {
        if (mImage != null) {
            //If we have a player image, then draw it
            mImage.setBounds((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom);
            mImage.draw(canvas);
            // Grid boxes
//            Paint myPaint = new Paint();
//            myPaint.setColor(0x80000000);
//            myPaint.setStrokeWidth(10);
//            canvas.drawRect(gridPos.left, gridPos.top, gridPos.right, gridPos.bottom, myPaint);

        }
    }

    public boolean getIsAttacking(){
        return isAttacking;
    }

    public boolean getIsReturning(){
        return isReturning;
    }

    public boolean getAtOriginalPos(){
        return atOriginalPos;
    }

    private void checkAtOriginalPos(){
        if(bounds.left -gridPos.left > 1 || bounds.top - gridPos.top > 1){
            //Log.d("Enemy Bounds","Bounds top:" + bounds.top + "Bounds point: " +pointOfAttack.y);
            atOriginalPos = false;
        } else {
            bounds.offsetTo(gridPos.left,gridPos.top);
//            moveGridPos();
            wrapGrid();
            wrapScreen();
            atOriginalPos = true;
        }
    }

    private void setGridSpeed(PointF speedOfGrid){
        gridSpeed=speedOfGrid;
    }

    private void moveGridPos(){
        gridPos.offset((int) (gridSpeed.x * SPEED), (int) (gridSpeed.y * SPEED));
    }

    public int getEnemyWidth(){
        return (bounds.right-bounds.left);
    }
    public int getEnemyHeight(){
        return (bounds.bottom-bounds.top);
    }

}



