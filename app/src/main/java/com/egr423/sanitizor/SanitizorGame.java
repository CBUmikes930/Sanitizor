package com.egr423.sanitizor;

import android.content.Context;
import android.content.Intent;
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

    private final int ENEMY_ROWS = 2;
    private final int ENEMIES_IN_ROW = 5;

    static Player mPlayer;
    private int mPlayerScore = 0;
    private boolean mGameOver = false;
    private boolean mPlayerIsInvincible = false;


    private Joystick mJoystick = Joystick.getInstance();

    private int enemySize;
    private Enemy[] enemies;

    //Projectile System
    private Projectile[] mProjectiles;

    private int mProjPointer;

    //Context is saved in order to load resources
    private Context mContext;
    //Screen dimensions
    static int mSurfaceWidth;
    static int mSurfaceHeight;

    private final int BG_COLOR;

    public static final double pixelMultiplier = .5;

    public SanitizorGame(Context context, int surfaceWidth, int surfaceHeight) {
        mContext = context;
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;


        //Create a player object
        mPlayer = new Player(mContext);


        enemies = new Enemy[1000];
        enemySize = 0;

        generateEnemies();

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

    //Create enemy objects
    private void generateEnemies() {
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
        updateGameOver();
        if (!mGameOver) {
            //level progression
            if (enemySize == 0) {

            } else {
                //Move player
                mPlayer.move(velocity);
                enemiesAttack();
                moveProjectiles();
                checkForDeadEnemies();
            }
        } else {
            Intent gameIntent = new Intent(mContext, GameOver.class);
            //TODO Take out random score when we implement score
            //Generate random score for now
            Random random = new Random();
            mPlayerScore = random.nextInt(10000);
            gameIntent.putExtra("com.egr423.sanitizor.score", mPlayerScore);
            mContext.startActivity(gameIntent);
        }
    }

    private void checkForDeadEnemies() {
        int currentEnemyIndex = 0;
        for (Enemy enemy : enemies) {
            if (enemy != null && enemy.shouldDestroy()) {
                enemies[currentEnemyIndex] = null;
            }
            currentEnemyIndex++;
        }
    }

    private void moveProjectiles() {
        //Move Projectiles
        int i = 0;
        for (Projectile proj : mProjectiles) {
            if (proj != null && !proj.shouldDestroy()) {
                proj.move();
                moveProjectile(proj);
            } else {
                mProjectiles[i] = null;
            }
            i++;
        }
    }

    private void moveProjectile(Projectile projectile) {
        if (projectile.isFromPlayer()) {
            for (Enemy enemy : enemies) {// check if enemy got hit
                //If the projectile and enemy exist and they have collided (according to Rects)
                //  and the projectile hasn't already hit a different enemy
                if (enemy != null &&
                        Rect.intersects(projectile.getRect(), enemy.getRect()) &&
                        !projectile.isAnimationRunning()) {
                    // enemy hit
                    enemy.hit();
                    projectile.startAnimation();
                    if (enemy.isDead() && !enemy.shouldDestroy() &&
                            !enemy.isDeathAnimationRunning()) {
                        enemy.startDeathAnimation();
                    }
                }
            }
        } else {
            if (Rect.intersects(projectile.getRect(), mPlayer.getRect()) && !projectile.isAnimationRunning()) {
                projectile.startAnimation();
                if (BuildConfig.DEBUG) {
                    Log.d("Projectile", "Projectile hit Player");
                }
                if (!mPlayerIsInvincible) {
                    mPlayer.damagePlayer();
                }
            }
        }
    }

    private void enemiesAttack() {// enemies attack
        for (Enemy enemy : enemies) {
            enemyAttack(enemy);
        }
    }

    private void enemyAttack(Enemy enemy) {
        Random ran = new Random();
        if (enemy != null && !enemy.isDeathAnimationRunning()) {
            if (Rect.intersects(enemy.getRect(), mPlayer.getRect())) {
                if (BuildConfig.DEBUG) {
                    Log.d("Enemy", "Enemy collided with Player");
                }
                if (!mPlayerIsInvincible) {
                    mPlayer.damagePlayer();
                }
            }
            int attack = ran.nextInt(1001);
            if (BuildConfig.DEBUG) {
                Log.d("Attack Chance", "attack variable logged at: " + attack);
            }
            //if random variable is < 5 then I want to check if I can attack
            if (enemy.getIsAttacking()) {
                enemy.attack();
                if (BuildConfig.DEBUG) {
                    Log.d("Enemy update", "Enemy is attacking: " + enemy);
                }
            } else if (enemy.getIsReturning()) {
                if (BuildConfig.DEBUG) {
                    Log.d("Sanitizor.update", "Enemy is still returning");
                }
                enemy.returnToOriginalPos();
                if (BuildConfig.DEBUG) {
                    Log.d("Enemy update", "Enemy returning to Original position");
                }
            } else if (!enemy.getAtOriginalPos()) {
                try {
                    enemy.returnToOriginalPos();
                    if (BuildConfig.DEBUG) {
                        Log.d("Enemy update", "Enemy returning to Original position");
                    }
                } catch (Exception ignored) {
                    // Do nothing
                }
            } else {
                if (attack <= 1) {
                    try {
                        if (enemy.checkAttack()) {
                            if (BuildConfig.DEBUG) {
                                Log.d("Enemy update", "Enemy is attacking: " + enemy);
                            }
                            enemy.attack();
                        } else {
                            if (!enemy.getIsAttacking()) {
                                enemy.move(new PointF(30, 0));
                            }
                            if (BuildConfig.DEBUG) {
                                Log.d("Enemy update", "Enemy couldn't attack");
                            }
                        }
                    } catch (Exception ignored) {
                        // DO NOTHING
                    }
                } else {
                    if (!enemy.getIsAttacking()) {
                        enemy.move(new PointF(30, 0));
                    }
                }
            }
            //Randomize whether to attack
            attack = ran.nextInt(500);
            if (attack <= 1) {
                createProjectile(enemy);
            }
        }
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

    private void updateGameOver() {
        if (mPlayer.getPlayerLives() <= 0) {
            mGameOver = true;
        }
    }

    public boolean getGameOver() {
        return mGameOver;
    }

    public int getPlayerScore() {
        return mPlayerScore;
    }

    public void killPlayer() {
        mPlayer.setPlayerLives(0);
    }

}
