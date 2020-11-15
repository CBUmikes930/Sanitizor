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

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The player object which is the class controlled by the player.
 */
public class Player extends Character {

    //Player position values
    private final int BOTTOM_PADDING = 50; //Padding to keep image off of bottom

    //The image resource for the player
    private int rotation;
    private int cur_sprite;

    private final int INVICIBILITY_DURATION = 1000;
    private final long RAPID_FIRE_DURATION = 8000;
    private final int NORMAL_SHOT_COOLDOWN = 1000;


    private boolean isAlive;
    private boolean justTookDamage;
    private long lastDamaged;
    private final boolean isInvincible;
    private int playerLives;
    private boolean mDeathAnimationIsRunning;
    private long mDeathStartTime;
    private boolean mGameOverStatus;
    private long mLastMoved;

    private boolean mHasRapidFire;
    private long mRapidFireStart;

    public Player(Context context) {
        //Load the image from the resources
        cur_sprite = 0;
        mImage = new Bitmap[11];
        for (int i = 0; i < 11; i++) {
            //Get sprite name
            String name = ("player_" + i).toLowerCase();
            //Get sprite id
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            if (id == 0) {
                Log.e("Projectile Error", "ID lookup for resource " + name + " failed.");
            }
            //Get sprite
            mImage[i] = BitmapFactory.decodeResource(context.getResources(), id);
        }
        if (mImage[cur_sprite] != null) {
            //If image was loaded, then calculate it's relative height compared to the WIDTH (aspect ratio)
            rotation = 0;
            mDeathAnimationIsRunning = false;
            bounds = new Rect(0, 0,
                    (int) (mImage[cur_sprite].getWidth() * SanitizorGame.PIXEL_MULTIPLIER),
                    (int) (mImage[cur_sprite].getHeight() * SanitizorGame.PIXEL_MULTIPLIER));
        } else {
            //Couldn't load, so post a message and set HEIGHT = WIDTH
            Log.d("Player Error", "Could not load mPlayerImage from resource: R.drawable.player");
        }


        SPEED = 0.1;
        justTookDamage = false;
        isInvincible = false;
        playerLives = 3;
        mGameOverStatus = false;
        shotCoolDown = NORMAL_SHOT_COOLDOWN;
        mHasRapidFire = false;

        //Set initial position
        setStartPosition();
        mLastMoved = System.currentTimeMillis();
    }

    public void damagePlayer() {
        if(!justTookDamage){
            SoundManager.getInstance().playSound("Player_Damage.ogg");
            Log.d("Player", "Player took damage!");
            playerLives--;
            Log.d("Player","Player has "+ playerLives +" lives left");
            justTookDamage = true;
            lastDamaged = System.currentTimeMillis();
        }
        if (playerLives <= 0) {
            startDeathAnimation();
        }
    }

    public void checkInvincibility() {
        if (System.currentTimeMillis() - lastDamaged > INVICIBILITY_DURATION && !isInvincible) {
            justTookDamage = false;
            cur_sprite = 0;
        }
    }

    public void move(PointF velocity) {
        //Move center by velocity on x-axis, but anchor y
        bounds.offset((int) (-velocity.x * SPEED * (System.currentTimeMillis() - mLastMoved)), 0);
        mLastMoved = System.currentTimeMillis();

        //Check if still on screen
        if (bounds.right > SanitizorGame.mSurfaceWidth) {
            //If too far right, then set it's right side to the edge of screen
            bounds.offsetTo(SanitizorGame.mSurfaceWidth - bounds.width(), bounds.top);
        } else if (bounds.left < 0) {
            //If too far left, then set it's left side to the edge of the screen
            bounds.offsetTo(0, bounds.top);
        }
    }

    public void draw(Canvas canvas) {
        float ROTATION_RATE = 0.033f;

        if (mImage[cur_sprite] != null) {
            final float ANIMATION_SPEED = 270;
            if (mDeathAnimationIsRunning) {
                long elapsedTime = System.currentTimeMillis()   -mDeathStartTime;
                cur_sprite = (int) Math.floor(elapsedTime/ANIMATION_SPEED);
                rotation = (int) ((System.currentTimeMillis() - mDeathStartTime) * ROTATION_RATE);
                if (rotation > 90) {
                    rotation = 90;
                    mGameOverStatus = true;
                }
            }

            Matrix matrix = new Matrix();
            //Map to the bounds coordinates
            matrix.setRectToRect(new RectF(0, 0, mImage[cur_sprite].getWidth(), mImage[cur_sprite].getHeight()),
                    new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom),
                    Matrix.ScaleToFit.FILL);
            //Rotate
            matrix.postRotate(rotation, bounds.centerX(), bounds.centerY());

            if (System.currentTimeMillis() - lastDamaged < 100 && !mDeathAnimationIsRunning) {
                //Show damaged player animation
                canvas.drawBitmap(mImage[1], matrix, null);
            } else if (System.currentTimeMillis() - lastDamaged < INVICIBILITY_DURATION && !mDeathAnimationIsRunning) {
                //Show invincibility flash
                cur_sprite = (((System.currentTimeMillis() - lastDamaged) / 200) % 2 == 0) ? 2 : 0;
                canvas.drawBitmap(mImage[cur_sprite], matrix, null);
            } else {
                canvas.drawBitmap(mImage[cur_sprite], matrix, null);
            }

            if(!(System.currentTimeMillis() - mRapidFireStart < RAPID_FIRE_DURATION && mHasRapidFire)){
                shotCoolDown = NORMAL_SHOT_COOLDOWN;
                mHasRapidFire = false;
            }
        }
    }

    public void setStartPosition() {
        //Set player image to the center of the screen and the bottom off the screen's bottom by the padding
        bounds.offsetTo((SanitizorGame.mSurfaceWidth - bounds.width()) / 2,
                SanitizorGame.mSurfaceHeight - (BOTTOM_PADDING + bounds.height()));
    }

    public int getPlayerLives(){
        return playerLives;
    }

    public void setPlayerLives(int lives){
        playerLives = lives;
        if (playerLives == 0) {
            startDeathAnimation();
        }
    }

    public void startDeathAnimation() {
        if (!mDeathAnimationIsRunning) {
            mDeathStartTime = System.currentTimeMillis();
            mDeathAnimationIsRunning = true;
            SoundManager.getInstance().stopAll();
            SoundManager.getInstance().playSound("GameOver01.ogg");
        }
    }

    @Override
    Point getPosition() {
        return new Point(bounds.centerX(), bounds.top);
    }

    public boolean isGameOver() {
        return mGameOverStatus;
    }

    public void activateRapidFire(){
        mRapidFireStart = System.currentTimeMillis();
        mHasRapidFire = true;
        shotCoolDown = 0;
    }
}