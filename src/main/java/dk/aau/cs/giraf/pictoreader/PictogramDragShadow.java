package dk.aau.cs.giraf.pictoreader;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created on 23-04-14.
 */
public class PictogramDragShadow extends View.DragShadowBuilder {

    private Drawable shadow;
    private View dragView;


    public PictogramDragShadow(View view) {
        super(view);

        shadow = view.getResources().getDrawable(R.drawable.trans);
        shadow.setCallback(view);
        shadow.setBounds(25, 25, view.getWidth(), view.getHeight());
        dragView = view;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.scale((float) 1.3, (float) 1.3);
        shadow.draw(canvas);
        getView().draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point touchPoint) {
        shadowSize.set(1000, 1000);
        touchPoint.set(touchPoint.x + dragView.getWidth() / 2, touchPoint.y + dragView.getHeight() / 2);
    }
}

