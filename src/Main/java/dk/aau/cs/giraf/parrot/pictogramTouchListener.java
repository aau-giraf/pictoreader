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
import android.widget.ListAdapter;
import android.widget.TextView;

import dk.aau.cs.giraf.gui.GGridView;
import dk.aau.cs.giraf.gui.GSelectableContent;
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
		if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
			SpeechBoardFragment.draggedPictogramIndex = position;
            SpeechBoardFragment.dragOwnerID = owner;
            if(owner == R.id.supercategory)
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
                SpeechBoardFragment.displayedSubCategoryIndex = -1;
                GGridView subCategoryGrid = (GGridView) activity.findViewById(R.id.subcategory);
                GGridView mainCategoryGrid = (GGridView) activity.findViewById(R.id.supercategory);
                subCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getSubcategoriesByCategory(SpeechBoardFragment.displayedCategory), activity, R.id.subcategory, user,-1));

                TextView selectedCategoryText = (TextView) activity.findViewById(R.id.textViewSelectedCategory);

                selectedCategoryText.setText("Valgt kategori: " +  SpeechBoardFragment.displayedMainCategory.getName());


                //mainCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getCategoriesByProfileId(user.getProfileID()), activity, R.id.supercategory, user, SpeechBoardFragment.displayedMainCategoryIndex));
            }
            else if (owner == R.id.subcategory)
            {
                CategoryController categoryController = new CategoryController(activity.getBaseContext());
                //this check is neccessary if you click twice at a subcategory it will crash since subCategories does not contain any subCategory
                if(!categoryController.getSubcategoriesByCategory(SpeechBoardFragment.displayedMainCategory).isEmpty())
                {
                    try
                    {
                        SpeechBoardFragment.displayedCategory = categoryController.getSubcategoriesByCategory(SpeechBoardFragment.displayedMainCategory).get(position);
                    }
                    catch (OutOfMemoryError e)
                    {
                        e.getStackTrace();
                        return false;
                    }

                    GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);
                    SpeechBoardFragment.displayedSubCategoryIndex = SpeechBoardFragment.draggedPictogramIndex;

                    try
                    {
                        SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory);
                    }
                    catch (OutOfMemoryError e)
                    {
                        e.getStackTrace();
                        SpeechBoardFragment.speechboardPictograms.clear();
                        return false;
                    }

                    pictogramGrid.setAdapter(new PictogramAdapter(SpeechBoardFragment.speechboardPictograms, activity.getApplicationContext(),activity, user));
                    GGridView subCategoryGrid = (GGridView) activity.findViewById(R.id.subcategory);
                    subCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getSubcategoriesByCategory(SpeechBoardFragment.displayedCategory), activity, R.id.subcategory, user,SpeechBoardFragment.displayedSubCategoryIndex));
                }
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
