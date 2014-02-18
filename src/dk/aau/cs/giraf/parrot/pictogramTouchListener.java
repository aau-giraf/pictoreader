package dk.aau.cs.giraf.parrot;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;

public class pictogramTouchListener implements OnTouchListener {
	private int position;

	public pictogramTouchListener(int position)
	{
		this.position=position;
	}
	@Override
	public boolean onTouch(View view, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) { 
		     
			SpeechBoardFragment.draggedPictogramIndex = position;
			
			ClipData data = ClipData.newPlainText("label", "text");
	    	DragShadowBuilder shadowBuilder = new DragShadowBuilder(view);
	    	view.startDrag(data, shadowBuilder, view, 0);
	    	return true;
		 }
		else
		{
			return false;
		}
	}

}
