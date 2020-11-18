package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created by Jesse Breslin on 10/9/2020
 * <p>
 * The Enemy class along with its subclasses
 * May separate the subclasses into different class files later //TODO
 */


//TODO Add enemy movement commands;
//TODO Add attack command to each subclass
//TODO Create graphical components for each enemy type

public abstract class Enemy extends Character {

    public static final Class<?>[] SUB_CLASSES = {RedEnemy.class, BlueEnemy.class, GreenEnemy.class};
    private final float ROTATION_RATE = 0.5f;
    private final int TIME_TO_ATTACK = 20;
    //VARIABLE Fields
    int ATTACK_PHASE = 4000;
    int hitPoints = 2;
    int ATTACK_COOL_DOWN = 5000;
    int mPointValue;
    private float rotation = 0;
    private int mDeathStatus;
    private boolean mDeathAnimationIsRunning = false;
    private boolean wrappingx = false;
    private boolean wrappingy = false;
    private long mDeathStartTime;
    long mLastMovedTime;
    private long mBLastMovedTime;
    private final int TIME_TO_ATTACK = 20;

    private long lastAttacked;
    private long attackTime;
    private Rect gridPos;
    private PointF gridSpeed;
    private boolean isAttacking;
    private boolean isReturning;
    private boolean atOriginalPos;
    private boolean wrappingGx = false;
    private boolean wrappingGy = false;

    public Enemy(ENEMY_COLORS color, @NonNull Context context, Point location, int numberOfSprites) {
        mImage = new Bitmap[numberOfSprites];
        for (int i = 0; i < numberOfSprites; i++) {
            //Get sprite name
            String name = (color.name() + "_" + (i + 1)).toLowerCase();
            //Get sprite id
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            if (id == 0) {
                Log.e("Projectile Error", "ID lookup for resource " + name + " failed.");
            }
            //Get sprite
            mImage[i] = BitmapFactory.decodeResource(context.getResources(), id);
        }
        mDeathStatus = 0;
        if (mImage[mDeathStatus] != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage[mDeathStatus].getWidth() * SanitizorGame.PIXEL_MULTIPLIER),
                    (int) (mImage[mDeathStatus].getHeight() * SanitizorGame.PIXEL_MULTIPLIER));
            bounds.offsetTo(location.x, location.y);
        } else {
            Log.d("Enemy Error", "Could not load mEnemyImage from resource: R.drawable.enemy_" + color);
        }
        lastFired = 0;
        gridPos = new Rect(bounds);
        atOriginalPos = true;
        SPEED = 0.1;
        mLastMovedTime = System.currentTimeMillis();
        // 0 0     gridspeed*20  bounds*50
    }

    public void returnToOriginalPos() {
        moveGridPos();
        wrapGrid();
        checkAtOriginalPos(true);
        if (!atOriginalPos) {
//            Log.d("Enemy Movement","" +gridPos.left +"," + gridPos.top);
            int x = gridPos.left - bounds.left;
            int y = gridPos.top - bounds.top;
            double direction = Math.atan((0.0 + y) / x);
            int signx = 1;
            if (x < 0) {
                signx = -1;
            }

            long time = lastMoveTime(mLastMovedTime);
            double speed = 1;
            bounds.offset((int) ((speed * Math.cos(direction) * signx*time)),
                    (int) (Math.min(speed * Math.sin(direction)*signx *time,-10)));
            mLastMovedTime = setLastMoveTime();
           // mLastMovedTime = System.currentTimeMillis() -mLastMovedTime;
            wrapScreen();
            isReturning = true;
            //Log.d("Enemy.returnToAttackPos", "Enemy is returning");
        } else {
            //Log.d("Enemy.returnToAttackPos", "Enemy is no longer returning");
            lastAttacked = System.currentTimeMillis();
            isReturning = false;
        }
    }

    //To check if I can attack, I have to check if it has been enough time since I last attacked.
    public boolean checkAttackCooldown() {
        checkAtOriginalPos(false);
        if (lastAttacked < SanitizorGame.pauseStart) {
            lastAttacked += SanitizorGame.elapsedPauseTime;
        }
        long elapsedTime = System.currentTimeMillis() - lastAttacked;
        if (isAttacking || elapsedTime >= ATTACK_COOL_DOWN) {
            gridPos = new Rect(bounds);
            return true;
        } else {
            return false;
        }
    }

    // Attacking
    public void checkAttack() {
        moveGridPos();
        wrapGrid();
        checkAtOriginalPos(false);
        // How long should the attack last
        // If I am attacking reset the last attack variable to current time
        if (!isAttacking) {
            isAttacking = true;
            attackTime = System.currentTimeMillis();
        }
        if (attackTime < SanitizorGame.pauseStart) {
            attackTime += SanitizorGame.elapsedPauseTime;
        }
        long elapsedTime = System.currentTimeMillis() - attackTime;
        if (elapsedTime <= ATTACK_PHASE) {
            //Log.d("Enemy.attack","Enemy should be attacking");
            attack(new PointF(0.75f, 4));
        } else {
//            Log.d("Enemy Movement","" +gridPos.left +"," + gridPos.top);
            isReturning = true;
            //Log.d("Enemy.attack", "Enemy stopped attacking after " +(System.currentTimeMillis()-attackTime));
            isAttacking = false;
        }
    }

    public void attack(@NonNull PointF velocity) {
        wrapGrid();
//        checkAtOriginalPos();
        wrapScreen();
        bounds.offset(0, (int) (velocity.y * SPEED* lastMoveTime(mLastMovedTime)));
        mLastMovedTime = setLastMoveTime();
        wrapScreen();
    }

    public void move(@NonNull PointF velocity) {
        checkAtOriginalPos(false);
        gridSpeed = velocity;
        moveGridPos();
//        checkAtOriginalPos();
        long time = lastMoveTime(mLastMovedTime);
        bounds.offset((int) (velocity.x * SPEED * time),
                (int) (velocity.y * SPEED* time));
        mLastMovedTime = setLastMoveTime();
        wrapScreen();
        wrapGrid();
    }

    void wrapScreen() {
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

    void wrapGrid() {
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

    public void damageEnemy() {
        hitPoints--;
//        Log.d("Enemy", "Enemy hit, current HP: " + hitPoints);
    }

    public boolean shouldDestroy() {
//        Log.d("Enemy", "Should Destroy " + (mDeathStatus >= mImage.length));
        return mDeathStatus >= mImage.length;
    }

    public void startDeathAnimation() {
        if (!mDeathAnimationIsRunning) {
            mDeathStartTime = System.currentTimeMillis();
            mDeathAnimationIsRunning = true;
//            Log.d("Enemy", "Started Death Animation");
        }

    }

    public void setPosition(int x, int y) {
        bounds.offsetTo(x, y);
    }

    public boolean isDeathAnimationRunning() {
        return mDeathAnimationIsRunning;
    }

    public boolean isDead() {
        return hitPoints <= 0;
    }

    public void draw(Canvas canvas) {
        if (mImage[mDeathStatus] != null) {
            final float ANIMATION_SPEED = 50;

            if (mDeathAnimationIsRunning) {
                if (mDeathStartTime < SanitizorGame.pauseStart) {
                    mDeathStartTime += SanitizorGame.elapsedPauseTime;
                }
                long elapsedTime = System.currentTimeMillis() - mDeathStartTime;
                mDeathStatus = (int) Math.floor(elapsedTime / ANIMATION_SPEED);
            }

            if (mDeathStatus < mImage.length) {
                Matrix matrix = new Matrix();
                //Map to the bounds coordinates
                matrix.setRectToRect(new RectF(0, 0, mImage[mDeathStatus].getWidth(), mImage[mDeathStatus].getHeight()),
                        new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom),
                        Matrix.ScaleToFit.FILL);
                //Rotate
                rotation += ROTATION_RATE;
                matrix.postRotate(rotation, bounds.centerX(), bounds.centerY());
                canvas.drawBitmap(mImage[mDeathStatus], matrix, null);
            }
        }
    }

    public boolean getIsAttacking() {
        return isAttacking;
    }

    public boolean getIsReturning() {
        return isReturning;
    }

    public boolean getAtOriginalPos() {
        return atOriginalPos;
    }

    private void checkAtOriginalPos(boolean shouldReturn) {
        if (bounds.left - gridPos.left > 10 || bounds.top - gridPos.top > 10) {
            //Log.d("Enemy Bounds","Bounds top:" + bounds.top + "Bounds point: " +pointOfAttack.y);
            atOriginalPos = false;
        } else {
            if (shouldReturn) {
//                Log.d("Enemy", "Returning to original position");
                bounds.offsetTo(gridPos.left, gridPos.top);
//            moveGridPos();
                wrapGrid();
                wrapScreen();
                atOriginalPos = true;
            }
        }
    }

    private void setGridSpeed(PointF speedOfGrid) {
        gridSpeed = speedOfGrid;
    }

    private void moveGridPos() {
        long time =lastMoveTime(mBLastMovedTime);
        gridPos.offset((int) (gridSpeed.x * SPEED * time),
                (int) (gridSpeed.y * SPEED  *time));
        mBLastMovedTime = setLastMoveTime();
    }

    public int getEnemyWidth() {
        return bounds.width();
    }

    public int getEnemyHeight() {
        return (bounds.bottom - bounds.top);
    }

    public int getPointValue() {
        return mPointValue;
    }
  
    protected long lastMoveTime(long lastMoveTime){
        if(lastMoveTime < SanitizorGame.pauseStart){
            lastMoveTime += SanitizorGame.elapsedPauseTime;
        }
        return System.currentTimeMillis() - lastMoveTime;
    }

    protected long setLastMoveTime(){
        return System.currentTimeMillis();
    }

    protected enum ENEMY_COLORS {RED, BLUE, GREEN}

}