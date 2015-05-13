package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import dk.aau.cs.giraf.gui.GGridView;
import dk.aau.cs.giraf.dblib.controllers.CategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;

public class pictogramTouchListener implements OnTouchListener {
	private int position;
    private int owner;
    private PictoreaderProfile user;
    private Activity activity;
    private PictogramController pictogramController;


	public pictogramTouchListener(int position, int owner, Activity activity, PictoreaderProfile user)
	{
		this.position=position;
        this.owner = owner;
        this.activity = activity;
        this.user = user;
        pictogramController = new PictogramController(activity.getApplicationContext());

	}
    //public boolean onTouch(View view, MotionEvent event) {return true;}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
			SpeechBoardFragment.draggedPictogramIndex = position;
            SpeechBoardFragment.dragOwnerID = owner;
            if(owner == R.id.category)
            {
                SpeechBoardFragment.displayedMainCategoryIndex = SpeechBoardFragment.draggedPictogramIndex;
                CategoryController categoryController = new CategoryController(activity.getApplicationContext());
                try {
                    SpeechBoardFragment.displayedCategory = categoryController.getCategoriesByProfileId(user.getProfileID()).get(position);
                }
                catch (OutOfMemoryError e)
                {
                    e.getStackTrace();
                    return false;
                }
                SpeechBoardFragment.displayedMainCategory = SpeechBoardFragment.displayedCategory;
                GGridView pictogramGrid = (GGridView) activity.findViewById(R.id.pictogramgrid);

                try
                {
                    SpeechBoardFragment.speechboardPictograms.clear();

                    if (pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory).size() > SpeechBoardFragment.MaxNumberOfAllowedPictogramsInCategory)
                    {
                        SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory).subList(0, SpeechBoardFragment.MaxNumberOfAllowedPictogramsInCategory);
                    }
                    else
                    {
                        SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory);
                    }
                }
                catch (OutOfMemoryError e)
                {
                    e.getStackTrace();
                    return false;
                }

                //SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory);
                pictogramGrid.setAdapter(new PictogramAdapter(SpeechBoardFragment.speechboardPictograms, activity.getApplicationContext(), activity, user));

                //Setup the view for the categories
                GGridView mainCategoryGrid = (GGridView) activity.findViewById(R.id.category);


                //mainCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getCategoriesByProfileId(user.getProfileID()), activity, R.id.supercategory, user, SpeechBoardFragment.displayedMainCategoryIndex));
            }

			ClipData data = ClipData.newPlainText("label", "text"); 

            PictogramDragShadow shadowBuilder = new PictogramDragShadow(view);
	    	view.startDrag(data, shadowBuilder, view, 0);

	    	return true;
		}
		else
		{
			return false;
		}
	}
}
