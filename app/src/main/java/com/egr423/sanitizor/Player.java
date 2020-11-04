package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

    //Speed multiplier for the movement speed based on accelerometer


    //The location of the top left corner of the player

    //The image resource for the player
    private Bitmap[] mImage;
    private int rotation;
    private int cur_sprite;

    private final int INVICIBILITY_DURATION = 1000;
    private boolean isAlive;
    private boolean justTookDamage;
    private long lastDamaged;
    private final boolean isInvincible;
    private int playerLives;
    private boolean mAnimationIsRunning;
    private long mStartTime;
    private boolean mGameOverStatus;
    //Screen Dimensions

    public Player(Context context) {
        //Load the image from the resources
        cur_sprite = 0;
        mImage = new Bitmap[11];
        for (int i = 0; i < 11; i++) {
            //Get sprite name
            String name = "player_0" + i;
            //Get sprite id
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            if (id == 0) {
                Log.e("Projectile Error", "ID lookup for resource " + name + " failed.");
            }
            //Get sprite
            mImage[i] = BitmapFactory.decodeResource(context.getResources(), id);
        }

        if (mImage[0] != null) {
            //If image was loaded, then calculate it's relative height compared to the WIDTH (aspect ratio)
            rotation = 0;
            mAnimationIsRunning = false;
            bounds = new Rect(0, 0,
                    (int) (mImage[0].getWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mImage[0].getHeight() * SanitizorGame.pixelMultiplier));
        } else {
            //Couldn't load, so post a message and set HEIGHT = WIDTH
            Log.d("Player Error", "Could not load mPlayerImage from resource: R.drawable.player");
        }


        SPEED = 1.5;
        justTookDamage = false;
        isInvincible = false;
        playerLives = 3;
        mGameOverStatus = false;

        //Set initial position
        setStartPosition();

        //Set color
    }

    public void damagePlayer(){
        if(!justTookDamage){
            Log.d("Player", "Player took damage!");
            playerLives--;
            Log.d("Player","Player has "+ playerLives +" lives left");
            justTookDamage = true;
            lastDamaged = System.currentTimeMillis();
        }
        if(System.currentTimeMillis()-lastDamaged > INVICIBILITY_DURATION){
            justTookDamage = false;
            cur_sprite = 0;
        }
        if (playerLives <= 0) {
            startDeathAnimation();
        }
    }

    public void move(PointF velocity) {
        //Move center by velocity on x-axis, but anchor y
        bounds.offset((int) (-velocity.x * SPEED), 0);
        //Log.d("VELOCITY CHECK", "Original Velocity: " + -velocity.x);
        //Log.d("VELOCITY CHECK", "Modified Velocity: " + -velocity.x * SPEED);

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
        float ROTATION_RATE = 0.1f;
        if (mImage[cur_sprite] != null) {
            if (mAnimationIsRunning) {
                rotation = (int) ((System.currentTimeMillis() - mStartTime) * ROTATION_RATE);
                if (rotation > 90) {
                    rotation = 90;
                    mGameOverStatus = true;
                }
            }
            Matrix matrix = new Matrix();
            //Set the destination rectangle
            RectF dst = new RectF(bounds.left,
                    bounds.top,
                    (float) (bounds.left + (bounds.width() * SanitizorGame.pixelMultiplier)),
                    (float) (bounds.top + (bounds.height() * SanitizorGame.pixelMultiplier)));
            //Map to the bounds coordinates
            matrix.setRectToRect(new RectF(0, 0, bounds.width(), bounds.height()),
                    dst,
                    Matrix.ScaleToFit.FILL);
            //Rotate
            matrix.postRotate(rotation, bounds.centerX(), bounds.centerY());
            if (System.currentTimeMillis() - lastDamaged < 100) {
                //Show damaged player animation
                canvas.drawBitmap(mImage[1], matrix, null);
            } else if (System.currentTimeMillis() - lastDamaged < INVICIBILITY_DURATION) {
                //Show invicibility flash
                if (((System.currentTimeMillis() - lastDamaged) / 100) % 2 == 0) {
                    cur_sprite = 2;
                } else {
                    cur_sprite = 0;
                }
                canvas.drawBitmap(mImage[cur_sprite], matrix, null);
            } else {
                cur_sprite = 0;
                canvas.drawBitmap(mImage[cur_sprite], matrix, null);
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
        if (!mAnimationIsRunning) {
            mStartTime = System.currentTimeMillis();
            mAnimationIsRunning = true;
        }
    }

    public boolean isGameOver() {
        return mGameOverStatus;
    }
}