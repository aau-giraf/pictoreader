package dk.aau.cs.giraf.parrot;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created on 23-04-14.
 */
public class PictogramDragShadow extends View.DragShadowBuilder {

    private Drawable mShadow;
    private View dragView;


    public PictogramDragShadow(View v) {
        super(v);

        mShadow = v.getResources().getDrawable(R.drawable.trans);
        mShadow.setCallback(v);
        mShadow.setBounds(25, 25, v.getWidth(), v.getHeight());
        dragView = v;
    }
    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale((float)1.3, (float)1.3);
        mShadow.draw(canvas);
        getView().draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {
        shadowSize.set(1000, 1000);
        touchPoint.set(touchPoint.x + dragView.getWidth()/2, touchPoint.y + dragView.getHeight()/2);
    }
}

