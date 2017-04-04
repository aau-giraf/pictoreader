package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;

import dk.aau.cs.giraf.dblib.controllers.CategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;

public class PictogramTouchListener implements OnTouchListener {
    private int position;
    private int owner;
    private PictoreaderProfile user;
    private Activity activity;
    private PictogramController pictogramController;


    public PictogramTouchListener(int position, int owner, Activity activity, PictoreaderProfile user) {
        this.position = position;
        this.owner = owner;
        this.activity = activity;
        this.user = user;
        pictogramController = new PictogramController(activity.getApplicationContext());

    }
    //public boolean onTouch(View view, MotionEvent event) {return true;}

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            SpeechBoardFragment.draggedPictogramIndex = position;
            SpeechBoardFragment.dragOwnerID = owner;
            if (owner == R.id.category) {
                SpeechBoardFragment.displayedMainCategoryIndex = SpeechBoardFragment.draggedPictogramIndex;
                CategoryController categoryController = new CategoryController(activity.getApplicationContext());
                try {
                    SpeechBoardFragment.displayedCategory = categoryController.getCategoriesByProfileId(
                        user.getProfileID()).get(position);
                } catch (OutOfMemoryError e) {
                    e.getStackTrace();
                    return false;
                }
                SpeechBoardFragment.displayedMainCategory = SpeechBoardFragment.displayedCategory;
                GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);

                try {
                    SpeechBoardFragment.speechboardPictograms.clear();

                    if (pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory).size() >
                        SpeechBoardFragment.MaxNumberOfAllowedPictogramsInCategory)
                    {
                        SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(
                            SpeechBoardFragment.displayedCategory).subList(0,
                            SpeechBoardFragment.MaxNumberOfAllowedPictogramsInCategory);
                    } else {
                        SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(
                            SpeechBoardFragment.displayedCategory);
                    }
                } catch (OutOfMemoryError e) {
                    e.getStackTrace();
                    return false;
                }

                //SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(
                // SpeechBoardFragment.displayedCategory);
                pictogramGrid.setAdapter(new PictogramAdapter(SpeechBoardFragment.speechboardPictograms,
                    activity.getApplicationContext(), activity, user));
            }

            ClipData data = ClipData.newPlainText("label", "text");

            PictogramDragShadow shadowBuilder = new PictogramDragShadow(view);
            view.startDrag(data, shadowBuilder, view, 0);

            return true;
        } else {
            return false;
        }
    }
}
