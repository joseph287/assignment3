package com.logan20.draganddropgame;



/*created by jsj*/

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

class DragShadow extends View.DragShadowBuilder {
    private final View view;
    private final ColorDrawable shadow;

    DragShadow(ImageView v) {
        super(v);
        this.view = v;
        shadow = new ColorDrawable(Color.parseColor("#ddffffff"));
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        int width, height;

        width = view.getWidth()*2;
        height = view.getHeight()*2;

        shadow.setBounds(0,0,width,height);
        outShadowSize.set(width,height);
        outShadowTouchPoint.set(width/2,height/2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }
}
