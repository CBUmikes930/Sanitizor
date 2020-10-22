package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;

import java.util.Random;


/**
 * Created by Micah Steinbock on 10/6/2020
 * <p>
 * The Game handler which controls object creation and calculations
 */
public class SanitizorGame {

    private final int ENEMY_ROWS = 5;
    private final int ENEMIES_IN_ROW = 11;

    static Player mPlayer;
    private Joystick mJoystick = Joystick.getInstance();

    private int enemySize;
    private Enemy[] enemies;

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


        enemies = new Enemy[1000];
        enemySize = 0;
        //Create enemy objects
        int row = 0;
        int col = 0;
        for (int i = 0; i < ENEMY_ROWS * ENEMIES_IN_ROW + 1; i++) {
            Point location;
            try {
                if (((col * enemies[i - 1].getEnemyWidth()) + 20) > (mSurfaceWidth)) {
                    col = 0;
                    row++;
                }
                location = new Point((col++ * enemies[i - 1].getEnemyWidth()) + 20,
                        100 + row * enemies[i - 1].getEnemyHeight()); // TODO figure out spawn grid equation for enemies
            } catch (Exception e) {
                location = new Point(20, 100);
            }
            enemies[enemySize++] = (new Enemy("red", mContext, location));

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

        Random ran = new Random();
        for (Enemy enemy : enemies) {
            if (enemy != null) {
                int attack = ran.nextInt(1001);
                //Log.d("Attack Chance", "attack variable logged at: " +attack);
                //if random variable is < 5 then I want to check if I can attack
                if (enemy.getIsAttacking()) {
                    enemy.attack();
                    Log.d("Enemy update", "Enemy is attacking: " + enemy);
                } else if (enemy.getIsReturning()) {
                    //Log.d("Sanitizor.update", "Enemy is still returning");
                    enemy.returnToOriginalPos();
                    Log.d("Enemy update", "Enemy returning to Original position");
                } else if (!enemy.getAtOriginalPos()) {
                    try {
                        enemy.returnToOriginalPos();
                        Log.d("Enemy update", "Enemy returning to Original position");
                    } catch (Exception e) {
                        // Do nothing
                    }
                } else {
                    if (attack <= 1) {
                        try {
                            if (enemy.checkAttack()) {
                                Log.d("Enemy update", "Enemy is attacking: " + enemy);
                                enemy.attack();
                            } else {
                                if (!enemy.getIsAttacking()) {
                                    enemy.move(new PointF(30, 0));
                                }
                                Log.d("Enemy update", "Enemy couldn't attack");
                            }
                        } catch (Exception e) {
                            // DO NOTHING}
                        }
                    } else {
                        if (!enemy.getIsAttacking()) {
                            enemy.move(new PointF(30, 0));
                        }
                    }
                }
                createProjectile(enemy);
            }
        }

        //Move Projectiles
        int i = 0;
        for (Projectile proj : mProjectiles) {
            if (proj != null && !proj.shouldDestroy()) {
                proj.move();
                if (proj.isFromPlayer()) {
                    int currentEnemyIndex = 0;
                    for (Enemy enemy : enemies) {// check if enemy got hit
                        //If the projectile and enemy exist and they have collided (according to Rects)
                        //  and the projectile hasn't already hit a different enemy
                        if (enemy != null &&
                                Rect.intersects(proj.getRect(), enemy.getRect()) &&
                                !proj.isAnimationRunning()) {
                            // enemy hit
                            enemy.hit();
                            proj.startAnimation();
                            if (enemy.isDead() && !enemy.shouldDestroy() &&
                                    !enemy.isDeathAnimationRunning()) {
                                enemy.startDeathAnimation();
                            }
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

        int currentEnemyIndex = 0;
        for (Enemy enemy : enemies) {
            if (enemy != null && enemy.shouldDestroy()) {
                enemies[currentEnemyIndex] = null;
            }
            currentEnemyIndex++;
        }


        //Check for win (for now, nothing)
        mGameOver = false;
    }

    //Used to create a new projectile (if the cooldown has been surpassed) and add it to
    //the mProjectils array
    private void createProjectile(Character character) {
        Projectile projectile;

        if (System.currentTimeMillis() - character.lastFired < character.shotCoolDown) {
            return;
        }

        //If its been longer than the cooldown to shoot, then fire a new projectile
        if (character.equals(mPlayer)) {
            //Create a new projectile
            projectile = new Projectile(mContext, character);
        } else {
            //Create a new projectile at enemy position
            projectile = new EnemyProjectile(mContext);
        }
        //Record shot time
        character.lastFired = System.currentTimeMillis();
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
            if (e != null) {
                e.draw(canvas);
            }
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
