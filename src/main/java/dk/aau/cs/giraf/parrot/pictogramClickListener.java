package dk.aau.cs.giraf.parrot;

import android.app.Activity;
import android.content.ClipData;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import java.util.ArrayList;

import dk.aau.cs.giraf.gui.GGridView;
import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;

/**
 * Created by lasse on 15/04/15.
 */


class pictogramClickListener implements OnClickListener {

    private final int position;
    private final int owner;
    private PARROTProfile user;
    private final Activity activity;
    private PictogramController pictogramController;


    public pictogramClickListener(int position, int owner, Activity activity, PARROTProfile user) {
        this.position = position;
        this.owner = owner;
        this.activity = activity;
        this.user = user;
        pictogramController = new PictogramController(activity.getApplicationContext());

    }

    @Override
    public void onClick(View view) {
        if(owner != R.id.supercategory)
        {
            int count = 0;
            for(dk.aau.cs.giraf.oasis.lib.models.Pictogram p: SpeechBoardFragment.pictogramList)
            {
                if (p == null)
                {

                    SpeechBoardFragment.pictogramList.set(count,SpeechBoardFragment.speechboardPictograms.get(position));
                    break;
                }
                else{
                    count++;
                }


            }

            SpeechBoardFragment.dragOwnerID = owner;

            GridView sentence = (GridView) activity.findViewById(R.id.sentenceboard);
            sentence.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.pictogramList, activity));
            //SpeechBoardFragment.UpdateSpeechBoardView();

            //SpeechBoardFragment.displayPictograms(SpeechBoardFragment.pictogramList,activity);

            //SpeechBoardFragment.speechDragListener.draggedPictogram = ;
        }
        else{
            SpeechBoardFragment.displayedMainCategoryIndex = SpeechBoardFragment.draggedPictogramIndex;
            CategoryController categoryController = new CategoryController(activity.getApplicationContext());
            try {
                SpeechBoardFragment.displayedCategory = categoryController.getCategoriesByProfileId(user.getProfileID()).get(position);
            }
            catch (OutOfMemoryError e)
            {
                e.getStackTrace();
                return ;
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
                return;
            }

            //SpeechBoardFragment.speechboardPictograms = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory);
            pictogramGrid.setAdapter(new PictogramAdapter(SpeechBoardFragment.speechboardPictograms, activity.getApplicationContext(), activity, user));

            //Setup the view for the categories
            GGridView mainCategoryGrid = (GGridView) activity.findViewById(R.id.supercategory);


            //mainCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getCategoriesByProfileId(user.getProfileID()), activity, R.id.supercategory, user, SpeechBoardFragment.displayedMainCategoryIndex));
        }

    }
}