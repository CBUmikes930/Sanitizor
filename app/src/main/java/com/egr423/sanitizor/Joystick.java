package com.egr423.sanitizor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Joystick is a singleton class that creates a joystick that the user can use to move the player character
 */
public class Joystick {
    private static Joystick joystick = new Joystick();

    private static PointF center;
    private static PointF handleCenter;

    //The radius of the outer circle of the joystick
    private static final float OUT_RADIUS = 100;
    //The radius of the "Handle" of the circle of the joystick
    private static final float HANDLE_RADIUS = 50;

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //The color for the outer circle of the joystick
    private static int outColor;
    //The color for the inner circle of the joystick
    private static int handleColor;
    //Whether the graphic should be drawn currently (used in swipe control scheme)
    private static boolean drawGraphic = false;

    private Joystick() {
        outColor = R.attr.joystick_bg;
        handleColor = R.attr.joystick_handle;
    }

    public static Joystick getInstance() {
        return joystick;
    }

    public void setCenter(PointF center) {
        //If a center hasn't been declared, then make a new one, otherwise, set the coords
        if (Joystick.center == null) {
            Joystick.center = new PointF(center.x, center.y);
        } else {
            Joystick.center.set(center.x, center.y);
        }
    }

    public PointF getCenter() {
        //Return a new object with the same values as center
        return new PointF(center.x, center.y);
    }

    public void setHandleCenter(PointF center) {
        //If a handleCenter hasn't been declared, then make a new one, otherwise set the coords
        if (handleCenter == null) {
            handleCenter = new PointF(center.x, center.y);
        } else {
            handleCenter.set(center.x, center.y);
        }
        //Check if handle is currently outside the joystick bounds (out circle)
        double dx = Joystick.center.x - handleCenter.x;
        double dy = Joystick.center.y - handleCenter.y;
        double dist = Math.pow(dx, 2) + Math.pow(dy, 2);
        if (Math.sqrt(dist) + HANDLE_RADIUS > OUT_RADIUS) {
            //Handle is outside the bounds, so get the direction from joystick center to current handle
            double theta = Math.atan(dy / dx);
            int sign = 1;
            if (dx > 0) {
                sign = -1;
            }
            //Find the coordinates of the outer edge of the bounds along the correct angle
            dx = HANDLE_RADIUS * Math.cos(theta) * sign;
            dy = HANDLE_RADIUS * Math.sin(theta) * sign;
            //Set the handle to the edge of the circle.
            handleCenter.set((float) (Joystick.center.x + dx), (float) (Joystick.center.y + dy));
        }
    }

    public PointF getHandleCenter() {
        return new PointF(handleCenter.x, handleCenter.y);
    }

    public void resetHandlePos() {
        setHandleCenter(new PointF(center.x, center.y));
    }

    public float getOutRadius() {
        return OUT_RADIUS;
    }

    public float getHandleRadius() {
        return HANDLE_RADIUS;
    }

    //Sets the color of the outer joystick
    protected void setOuterColor(int color) {
        outColor = color;
    }

    //Sets the color of the joystick's handle
    protected void setHandleColor(int color) {
        handleColor = color;
    }

    protected void setDrawGraphic(boolean shouldDraw) {
        drawGraphic = shouldDraw;
    }

    public void draw(Canvas canvas) {
        if ((drawGraphic && SettingsDialogue.controlScheme == R.id.swipe_controls) ||
            SettingsDialogue.controlScheme == R.id.joystick_controls) {
            //Draw the outer circle
            paint.setColor(outColor);

            canvas.drawCircle(center.x, center.y, OUT_RADIUS, paint);

            //Draw the handle
            paint.setColor(handleColor);
            canvas.drawCircle(handleCenter.x, handleCenter.y, HANDLE_RADIUS, paint);
        }
    }
}
