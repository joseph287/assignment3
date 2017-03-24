package com.logan20.draganddropgame;


/*created by jsj*/

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;


/*codes from instruction*/

public class Item extends  AppCompatImageView{
    public Item(Context context) {
        super(context);
        init();
    }

    public Item(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }
}
