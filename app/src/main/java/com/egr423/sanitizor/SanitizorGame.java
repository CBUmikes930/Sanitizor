package com.egr423.sanitizor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Random;


/**
 * Created by Micah Steinbock on 10/6/2020
 * <p>
 * The Game handler which controls object creation and calculations
 */
public class SanitizorGame {

    public static final double PIXEL_MULTIPLIER = .5;
    private static final int SECOND = 1000;
    private static final int SECONDS_BETWEEN_LEVELS = 5;
    private static final int MAX_LEVEL = 10;
    static Player mPlayer;
    static int mSurfaceWidth;
    static int mSurfaceHeight;
    private final int ENEMY_ROWS = 2;
    private final int ENEMIES_IN_ROW = 5;
    private final int BG_COLOR;
    private final boolean mPlayerIsInvincible = false;
    private final Joystick mJoystick = Joystick.getInstance();
    private final Enemy[] enemies;
    private final Projectile[] mProjectiles;
    private final PowerUp[] mPowerUps;
    private final Context mContext;
    private int mPlayerScore = 0;
    private boolean mGameOver = false;
    private int enemySize;
    private int mProjPointer;
    private int mPowerPointer;
    private int level;
    private boolean levelEnding;

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

        //Initialize power ups array
        mPowerUps = new PowerUp[1000];


        //Start the game
        newGame();
    }


    private void endLevel() {
        levelEnding = true;
        clearLevel();
    }

    private void progressLevel() {
        if (level < MAX_LEVEL) {
            levelEnding = false;
            level++;
            generateEnemies();
        } else {
            updateGameOver();
        }
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
                        100 + row * enemies[i - 1].getEnemyHeight());
            } catch (Exception e) {
                location = new Point(20, 100);
            }
            enemies[enemySize++] = new BlueEnemy(mContext, location);
        }
    }

    public void newGame() {
        mGameOver = false;
        level = 1;
        levelEnding = false;

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
                if (!levelEnding)
                    endLevel();
            } else {
                enemiesAttack();
                checkForDeadEnemies();
            }
            //Move player
            mPlayer.move(velocity);
            mPlayer.checkInvincibility();
            moveProjectiles();

        } else {
            Intent gameIntent = new Intent(mContext, GameOver.class);
            //TODO Take out random score when we implement score
            //Generate random score for now
            Random random = new Random();
            gameIntent.putExtra("com.egr423.sanitizor.score", mPlayerScore);
            mContext.startActivity(gameIntent);
        }
    }

    private void checkForDeadEnemies() {
        int currentEnemyIndex = 0;
        for (Enemy enemy : enemies) {
            if (enemy != null && enemy.shouldDestroy()) {
                Random rand = new Random();
                int spawn = rand.nextInt(10);
                if(spawn == 0){
                    Log.d("PowerUp", "PowerUp should have spawned");
                    createPowerup(enemy);
                }
                enemies[currentEnemyIndex] = null;
                enemySize--;
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
                    enemy.damageEnemy();
                    projectile.startAnimation();
                    if (enemy.isDead() && !enemy.shouldDestroy() &&
                            !enemy.isDeathAnimationRunning()) {
                        enemy.startDeathAnimation();
                        mPlayerScore += 100;
                    } else {
                        mPlayerScore += 10;
                    }
                }
            }
        } else {
            if (Rect.intersects(projectile.getRect(), mPlayer.getRect()) && !projectile.isAnimationRunning()) {
                projectile.startAnimation();
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
//                Log.d("Enemy", "Enemy collided with Player");
                if (!mPlayerIsInvincible) {
                    mPlayer.damagePlayer();
                }
            }
            int attack = ran.nextInt(1001);
//            Log.d("Attack Chance", "attack variable logged at: " + attack);
            //if random variable is < 5 then I want to check if I can attack
            if (enemy.getIsAttacking()) {
                enemy.checkAttack();
//                Log.d("Enemy update", "Enemy is attacking: " + enemy);
            } else if (enemy.getIsReturning()) {
//                Log.d("Sanitizor.update", "Enemy is still returning");
                enemy.returnToOriginalPos();
//                Log.d("Enemy update", "Enemy returning to Original position");
            } else if (!enemy.getAtOriginalPos()) {
                try {
                    enemy.returnToOriginalPos();
//                    Log.d("Enemy update", "Enemy returning to Original position");
                } catch (Exception ignored) {
                    // Do nothing
                }
            } else {
                if (attack <= 1) {
                    try {
                        if (enemy.checkAttackCooldown()) {
//                            Log.d("Enemy update", "Enemy is attacking: " + enemy);
                            enemy.checkAttack();
                        } else {
                            if (!enemy.getIsAttacking()) {
                                enemy.move(new PointF(30, 0));
                            }
//                            Log.d("Enemy update", "Enemy couldn't attack");
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
        if (levelEnding || System.currentTimeMillis() - character.lastFired < character.shotCoolDown) {
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

    private void createPowerup(Enemy enemy){
        PowerUp powerUp;
        if (levelEnding){
            return;
        }
        powerUp = new PowerUp(mContext);
        powerUp.setPosition(enemy.getPosition());
        mPowerUps[mPowerPointer++] = powerUp;
        mPowerPointer %= mPowerUps.length;
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

    private void updateGameOver(){
        if(mPlayer.isGameOver() || level >= MAX_LEVEL){
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

    private void clearLevel() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                ConstraintLayout layout = ((Activity) mContext).findViewById(R.id.constraint_layout_activity_game);
                TextView textView = new TextView(mContext);

                textView.setId(View.generateViewId());


                Log.d("ClearedLevelTask", "Starting Pre Execute");
                final Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(SECOND);

                textView.setText(mContext.getResources().getString(R.string.level_cleared_message, System.lineSeparator(), SECONDS_BETWEEN_LEVELS));
                textView.getPaint().setColor(mContext.getResources().getColor(R.color.settingsTextColorDark));
                textView.setVisibility(View.VISIBLE);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                ((Activity) mContext).runOnUiThread(() -> {
                    // Stuff that updates the UI
                    layout.addView(textView);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(layout);
                    constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                    constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                    constraintSet.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                    constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                    constraintSet.applyTo(layout);
                    textView.startAnimation(in);
                });
//                textView.startAnimation(out);
                for (int i = SECONDS_BETWEEN_LEVELS; i >= 0; i--) {
                    try {
                        Log.d("ClearedLevelTask", "doInBackground iteration: " + i);
                        Thread.sleep(SECOND);
                        int finalI = i;
                        ((Activity) mContext).runOnUiThread(() -> {
                            // Stuff that updates the UI
                            textView.setText(mContext.getResources().getString(R.string.level_cleared_message, System.lineSeparator(), finalI));
                        });
                    } catch (InterruptedException ignored) {
                    }
                }

                ((Activity) mContext).runOnUiThread(() -> {
                    // Stuff that updates the UI
                    textView.setText(R.string.level_start_message);
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                final Animation out = new AlphaAnimation(1.0f, 0.0f);
                out.setDuration(500);
                ((Activity) mContext).runOnUiThread(() -> {
                    // Stuff that updates the UI
                    textView.setVisibility(View.GONE);
                    textView.startAnimation(out);
                    layout.removeView(textView);
                });
                progressLevel();
            }
        };
        thread.start();
    }
}
