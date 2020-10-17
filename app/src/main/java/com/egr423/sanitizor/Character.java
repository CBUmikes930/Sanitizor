package com.egr423.sanitizor;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public abstract class Character {

     Rect bounds;
     Drawable mImage;
     double SPEED;


     void setPosition(int x, int y) {
          bounds.left = x;
          bounds.top = y;
     }

     void setPosition(Point point){
          setPosition(point.x, point.y);
     }

     public Rect getRect(){
          return new Rect();
     }

     Point getPosition() {
          return new Point(bounds.left, bounds.top);
     }
}



