package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
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
        int row = 0;
        int col = 0;
        for (int i = 0; i < 89; i++) {


            Point location;
            try {
                if(((col*enemies.get(i-1).getEnemyWidth())+20) > (mSurfaceWidth)){
                    col = 0;
                    row++;
                }
                location = new Point((col++ * enemies.get(i-1).getEnemyWidth()) + 20, 100+row * enemies.get(i-1).getEnemyHeight()); // TODO figure out spawn grid equation for enemies
            } catch (Exception e){
                location = new Point(20, 100);
            }
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

        Random ran = new Random();
        for (Enemy enemy : enemies) {
            int attack = ran.nextInt(1001);
            //Log.d("Attack Chance", "attack variable logged at: " +attack);
            //if random variable is < 5 then I want to check if I can attack
            if(enemy.getIsAttacking()){
                enemy.attack();
            } else if(enemy.getIsReturning()){
                //Log.d("Sanitizor.update", "Enemy is still returning");
                enemy.returnToOriginalPos();
            }else if(!enemy.getAtOriginalPos()){
                try {
                    enemy.returnToOriginalPos();
                } catch (Exception e){
                    // Do nothing
                }
            }else {
                if (attack <= 1) {
                    try {
                        if (enemy.checkAttack()) {
                            //Log.d("Enemy update", "Enemy is attacking: " + enemy);
                            enemy.attack();
                        } else {
                            if (!enemy.getIsAttacking()) {
                                enemy.move(new PointF(  30, 0));
                            }
                            // Log.d("Enemy update", "Enemy couldn't attack");
                        }
                    } catch(Exception e) {
                        // DO NOTHING}
                    }
                } else {
                    if (!enemy.getIsAttacking()) {
                        enemy.move(new PointF(30, 0));
                    }
                }
            }
        }

        //Move Projectiles
        int i = 0;
        for (Projectile proj : mProjectiles) {
            if (proj != null && !proj.shouldDestroy()) {
                proj.move();
            } else {
                mProjectiles[i] = null;
            }
            i++;
        }

        //Check for collision of projectiles against enemies
        for (Projectile proj : mProjectiles) {
            for (Enemy enemy : enemies) {
                //If the projectile and enemy exist and they have collided (according to Rects)
                //  and the projectile hasn't already hit a different enemy
                if (proj != null && enemy != null &&
                        Rect.intersects(proj.getRect(), enemy.getRect()) &&
                        !proj.isAnimationRunning()) {
                    proj.startAnimation();
                    //Log.d("Projectile", "Projectile hit enemy");
                }
            }
        }

        //Check for win (for now, nothing)
        mGameOver = false;
    }

    //Used to create a new projectile (if the cooldown has been surpassed) and add it to
    //the mProjectils array
    private void createProjectile(Character character) {
        final int SHOT_COOL_DOWN = 500;

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
