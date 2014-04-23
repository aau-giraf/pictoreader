package dk.aau.cs.giraf.parrot;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Christian on 23-04-14.
 */
public class SentenceboardTouchListener implements View.OnTouchListener {
    private int position;

    public SentenceboardTouchListener(int position)
    {
        this.position=position;
    }
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            SpeechBoardFragment.draggedPictogramIndex = position;
            SpeechBoardFragment.dragOwnerID = R.id.sentenceboard;
            SpeechBoardFragment.speechDragListener.draggedPictogram = SpeechBoardFragment.pictogramList.get(position);

            ClipData data = ClipData.newPlainText("label", "text");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            return true;
        }
        else
        {
            return false;
        }
    }
}
