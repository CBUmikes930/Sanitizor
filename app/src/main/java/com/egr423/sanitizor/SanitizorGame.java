package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import java.util.Random;
import java.util.ArrayList;


/**
 * Created by Micah Steinbock on 10/6/2020
 * <p>
 * The Game handler which controls object creation and calculations
 */
public class SanitizorGame {

    static Player mPlayer;
    private Joystick mJoystick = Joystick.getInstance();
    private ArrayList<Enemy> enemies;

    //Projectile System
    private Projectile[] mProjectiles;
    private int mProjPointer;
    private long lastFired;

    //Context is saved in order to load resources
    private Context mContext;
    //Screen dimensions
    static int mSurfaceWidth;
    static int mSurfaceHeight;

    private final int BG_COLOR;

    private boolean mGameOver;

    public static final double pixelMultiplier = .5;

    public SanitizorGame(Context context, int surfaceWidth, int surfaceHeight) {
        mContext = context;
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;

        //Create a player object
        mPlayer = new Player(mContext);


        enemies = new ArrayList<>();
        //Create enemy objects
        for (int i = 0; i < 1; i++) {
            Point location = new Point(20, 20); // TODO figure out spawn grid equation for enemies
            Random ran = new Random();
            int num = ran.nextInt(3);
//            if(num == 0){
//                enemies.add(new Enemy("red", mContext, location));
//            }
            enemies.add(new Enemy("red", mContext, location));
        }

        //Set joystick position to bottom center of screen and set handle to center
        mJoystick.setCenter(new PointF((float) mSurfaceWidth / 2, (float) mSurfaceHeight - 150));
        mJoystick.resetHandlePos();

        //Set colors based on theme
        TypedValue a = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.gameBackgroundColor, a, true);
        BG_COLOR = a.data;
        mContext.getTheme().resolveAttribute(R.attr.joystick_bg, a, true);
        mJoystick.setOuterColor(a.data);
        mContext.getTheme().resolveAttribute(R.attr.joystick_handle, a, true);
        mJoystick.setHandleColor(a.data);

        //Initialize projectiles array
        mProjectiles = new Projectile[1000];
        mProjPointer = 0;

        //Start the game
        newGame();
    }

    public void newGame() {
        mGameOver = false;

        //Reset Player position
        mPlayer.setStartPosition();
    }

    //Called from GameThread when a button is clicked
    public void buttonClicked() {
        createProjectile(mPlayer);
    }

    public void update(PointF velocity) {
        if (mGameOver) return;

        //Move player
        mPlayer.move(velocity);

        for (Enemy enemy : enemies) {
            enemy.move(velocity);
//            createProjectile(enemy);
        }

        //Move Projectiles
        int i = 0;
        for (Projectile proj : mProjectiles) {
            if (proj != null && !proj.shouldDestroy()) {
                proj.move();
                if (proj.isFromPlayer()) {
                    for (Enemy enemy : enemies) {
                        //If the projectile and enemy exist and they have collided (according to Rects)
                        //  and the projectile hasn't already hit a different enemy
                        if (enemy != null &&
                                Rect.intersects(proj.getRect(), enemy.getRect()) &&
                                !proj.isAnimationRunning()) {

                            Log.d("Projectile", enemy.mImage.getTransparentRegion())

                            proj.startAnimation();
                            Log.d("Projectile", "Projectile hit enemy");
                        }
                    }
                } else {
                    if (Rect.intersects(proj.getRect(), mPlayer.getRect()) && !proj.isAnimationRunning()) {
                        proj.startAnimation();
                        Log.d("Projectile", "Projectile hit Player");
                    }
                }
            } else {
                mProjectiles[i] = null;
            }
            i++;
        }

        //Check for win (for now, nothing)
        mGameOver = false;
    }

    //Used to create a new projectile (if the cooldown has been surpassed) and add it to
    //the mProjectils array
    private void createProjectile(Character character) {
        final int SHOT_COOL_DOWN = 500;

        Projectile projectile;

        //If its been longer than the cooldown to shoot, then fire a new projectile
        if (character.equals(mPlayer) && System.currentTimeMillis() - lastFired >= SHOT_COOL_DOWN) {
            //Create a new projectile
            projectile = new Projectile(mContext, character);

            //Record shot time
            lastFired = System.currentTimeMillis();
        } else if (!character.equals(mPlayer)) {
            //Create a new projectile at enemy position
            projectile = new EnemyProjectile(mContext);
        } else {
            return;
        }
        //Set it to the Player's position
        projectile.setPosition(character.getPosition());

        //Add it to the projectile array
        mProjectiles[mProjPointer++] = projectile;
        //If we have filled the array, then loop back to the front of the array
        mProjPointer %= mProjectiles.length;
    }

    public void draw(Canvas canvas) {
        //Clear Canvas
        canvas.drawColor(BG_COLOR);

        //Draw Player
        mPlayer.draw(canvas);

        //Draw Enemies
        for (Enemy e : enemies) {
            e.draw(canvas);
        }


        //Draw all projectiles
        for (Projectile proj : mProjectiles) {
            if (proj != null) {
                proj.draw(canvas);
            }
        }


        //Draw the joystick circle
        mJoystick.draw(canvas);
    }
}
