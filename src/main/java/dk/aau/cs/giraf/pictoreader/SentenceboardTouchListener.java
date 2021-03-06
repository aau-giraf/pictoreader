package dk.aau.cs.giraf.pictoreader;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Christian on 23-04-14.
 */
public class SentenceboardTouchListener implements View.OnTouchListener {
    private int position;

    public SentenceboardTouchListener(int position) {
        this.position = position;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            SpeechBoardFragment.draggedPictogramIndex = position;
            SpeechBoardFragment.dragOwnerID = R.id.sentenceboard;
            SpeechBoardFragment.speechDragListener.draggedPictogram =
                SpeechBoardFragment.sentencePictogramList.get(position);

            ClipData data = ClipData.newPlainText("label", "text");
            PictogramDragShadow shadowBuilder = new PictogramDragShadow(view);
            view.startDrag(data, shadowBuilder, view, 0);
            return true;
        } else {
            return false;
        }
    }
}
