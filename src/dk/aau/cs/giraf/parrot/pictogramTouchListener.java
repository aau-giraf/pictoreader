package dk.aau.cs.giraf.parrot;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.GridView;

import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;

public class pictogramTouchListener implements OnTouchListener {
	private int position;
    private int owner;
    private PARROTProfile user;
    private Activity activity;
    private PictogramController pictogramController;


	public pictogramTouchListener(int position, int owner, Activity activity, PARROTProfile user)
	{
		this.position=position;
        this.owner = owner;
        this.activity = activity;
        this.user = user;
        pictogramController = new PictogramController(activity.getApplicationContext());

	}
	@Override
	public boolean onTouch(View view, MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) { 
		     
			SpeechBoardFragment.draggedPictogramIndex = position;
            SpeechBoardFragment.dragOwnerID = owner;

            if(owner == R.id.supercategory)
            {

                        CategoryController categoryController = new CategoryController(activity.getApplicationContext());
                        SpeechBoardFragment.displayedCategory = categoryController.getCategoriesByProfileId(user.getProfileID()).get(position);
                        GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);
                        SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory);
                        pictogramGrid.setAdapter(new PictogramAdapter(SpeechBoardFragment.speechboardPictograms, activity.getApplicationContext(),activity, user));
                        //Setup the view for the categories
                        GridView subCategoryGrid = (GridView) activity.findViewById(R.id.subcategory);
                        subCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getSubcategoriesByCategory(SpeechBoardFragment.displayedCategory), activity, R.id.subcategory, user));
            }
            else if (owner == R.id.subcategory)
            {
                CategoryController categoryController = new CategoryController(activity.getBaseContext());
                //this check is neccessary if you click twice at a subcategory it will crash since subCategories does not contain any subCategory
                if(!categoryController.getSubcategoriesByCategory(SpeechBoardFragment.displayedCategory).isEmpty())
                {
                    SpeechBoardFragment.displayedCategory = categoryController.getSubcategoriesByCategory(SpeechBoardFragment.displayedCategory).get(position);
                    GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);
                    SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory);
                    pictogramGrid.setAdapter(new PictogramAdapter(SpeechBoardFragment.speechboardPictograms, activity.getApplicationContext(),activity, user));

                }
            }
			
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
