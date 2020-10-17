package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.TypedValue;

import java.util.Random;
import java.util.ArrayList;


/**
 * Created by Micah Steinbock on 10/6/2020
 * <p>
 * The Game handler which controls object creation and calculations
 */
public class SanitizorGame {

    private Player mPlayer;
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
            //if(num == 0){
//            enemies.add(new Enemy.Red("red", mContext, location);
            //}
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
        mProjectiles = new Projectile[10];
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
        }

        //Move Projectiles
        for (Projectile proj : mProjectiles) {
            if (proj != null) {
                proj.move();
            }
        }

        //Check for win (for now, nothing)
        mGameOver = false;
    }

    //Used to create a new projectile (if the cooldown has been surpassed) and add it to
    //the mProjectils array
    private void createProjectile(Character character) {
        final int SHOT_COOL_DOWN = 1000;

        //If its been longer than the cooldown to shoot, then fire a new projectile
        if (System.currentTimeMillis() - lastFired >= SHOT_COOL_DOWN || !character.equals(mPlayer)) {
            //Create a new projectile
            Projectile projectile = new Projectile(mContext);
            //Set it to the Player's position
            projectile.setPosition(character.getPosition());

            if (character.equals(mPlayer)) {
                //Record shot time
                lastFired = System.currentTimeMillis();
                //Add it to the projectile array
                mProjectiles[mProjPointer++] = projectile;
                //If we have filled the array, then loop back to the front of the array
                mProjPointer %= mProjectiles.length;
            }
        }
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
