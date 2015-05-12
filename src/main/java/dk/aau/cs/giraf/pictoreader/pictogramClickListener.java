package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

import dk.aau.cs.giraf.activity.GirafActivity;
import dk.aau.cs.giraf.dblib.controllers.CategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;
import dk.aau.cs.giraf.gui.GirafWaitingDialog;

/**
 * Created by lasse on 15/04/15.
 */


class pictogramClickListener implements OnClickListener {

    private GirafWaitingDialog waitingDialog;
    private final int position;
    private final int owner;
    private PARROTProfile user;
    private final Activity activity;
    private GirafActivity mainActivity;
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
        if(owner != R.id.category)
        {
            int count = 0;
            for(dk.aau.cs.giraf.dblib.models.Pictogram p: SpeechBoardFragment.sentencePictogramList)
            {
                if (p == null)
                {

                    SpeechBoardFragment.sentencePictogramList.set(count,SpeechBoardFragment.speechboardPictograms.get(position));
                    break;
                }
                else{
                    count++;
                }


            }

            SpeechBoardFragment.dragOwnerID = owner;
            GridView sentence = (GridView) activity.findViewById(R.id.sentenceboard);
            sentence.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.sentencePictogramList, activity));
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
            GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);

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

            pictogramGrid.setAdapter(new PictogramAdapter(SpeechBoardFragment.speechboardPictograms, activity.getApplicationContext(), activity, user));
        }

    }
}