package com.egr423.sanitizor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Joystick {

    private static Joystick joystick = new Joystick();

    private static PointF center = new PointF(0,0);
    private static final float OUT_RADIUS = 100;
    private static final float IN_RADIUS = 50;

    private static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static int outColor;
    private static int inColor;

    private Joystick() {
        outColor = 0;
        inColor = 0;
    }

    public static Joystick getInstance() {
        return joystick;
    }

    protected void setCenter(float x, float y) {
        center.x = x;
        center.y = y;
    }

    protected PointF getCenter() {
        return center;
    }

    protected void setOuterColor(int color) {
        outColor = color;
    }

    protected void setInnerColor(int color) {
        inColor = color;
    }

    public void draw(Canvas canvas) {
        paint.setColor(outColor);
        canvas.drawCircle(center.x, center.y, OUT_RADIUS, paint);
        paint.setColor(inColor);
        canvas.drawCircle(center.x, center.y, IN_RADIUS, paint);
    }
}
