package dk.aau.cs.giraf.pictoreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.gui.*;
import dk.aau.cs.giraf.dblib.controllers.CategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;
import dk.aau.cs.giraf.dblib.models.Category;
import dk.aau.cs.giraf.pictogram.PictoMediaPlayer;

/**
 * @author PARROT spring 2012 and adapted by SW605f13
 * This class handles the views and actions of the speechLearning "Tale" function
 */

@SuppressLint("ValidFragment") //Avoid default constructor
public class SpeechBoardFragment extends Fragment
{
    private Activity parent;

    //Skal dette slettes 17-04-2015?
    //Remembers the index of the pictogram that is currently being dragged.
    public static int draggedPictogramIndex = -1;
    public static int dragOwnerID =-1;
    //HVORFOR SAETTER VI ET MAKS?! - SOEREN COMMENT
    public static int MaxNumberOfAllowedPictogramsInCategory = 125;

    //Serves as the back-end storage for the visual speechboard
    public static List<dk.aau.cs.giraf.dblib.models.Pictogram> speechboardPictograms = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();

    //This category contains the pictograms on the sentenceboard
    public static ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram> pictogramList = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();
    //This category contains the pictograms displayed on the big board
    public static Category displayedCategory = null;
    public static Category displayedMainCategory = null;
    public static int displayedMainCategoryIndex = 0;
    private PARROTProfile user = null;
    //private static Pictogram emptyPictogram = null;
    public static SpeechBoardBoxDragListener speechDragListener;

    private PictogramController pictogramController;
    //This variable is used! Android studio is a liar
    private PictogramCategoryController pictogramCategoryController;

    private Context context;

    private PictoMediaPlayer pictoMediaPlayer;
    private List<dk.aau.cs.giraf.dblib.models.Pictogram> displayPictogramList = null;

    private boolean backToNormalView = false;

    //int guadianID = (int) MainActivity.getGuardianID();
    //int childID = MainActivity.getChildID();

    public SpeechBoardFragment(Context c)
    {
        context = c;
        pictoMediaPlayer =  new PictoMediaPlayer(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parent = activity;
        pictogramController = new PictogramController(activity.getApplicationContext());
        pictogramCategoryController = new PictogramCategoryController(activity.getApplicationContext());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    /**
     * Most is done in this. eg setup the gridviews get data shown in the gridviews.
     */
    @Override
    public void onResume() {
        super.onResume();
        parent.invalidateOptionsMenu();
        GODMETHOD();
    }

    private void GODMETHOD() {
        View v = LayoutInflater.from(parent.getApplicationContext()).inflate(R.layout.speechboard_layout, null);
        //Set the background
        v.setBackgroundColor(GComponent.GetBackgroundColor());
        parent.setContentView(v);

        user=MainActivity.getUser();

        //check whether there are categories
        if(user.getCategoryAt(0)!=null)
        {
            displayedCategory = user.getCategoryAt(0);
            displayedMainCategory = displayedCategory;

            //Setup the view for the listing of pictograms in pictogramgrid
            final GridView pictogramGrid = (GridView) parent.findViewById(R.id.pictogramgrid);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            //Setup the view for the sentences
            GridView sentenceBoardGrid = (GridView) parent.findViewById(R.id.sentenceboard);
            sentenceBoardGrid.setAdapter(new SentenceboardAdapter(pictogramList, parent.getApplicationContext()));
            int noInSentence=user.getNumberOfSentencePictograms();
            sentenceBoardGrid.setNumColumns(noInSentence);

            //setup pictogramGrid.setNumColumns and sentenceBoardGrid.setColumnWidth
            setGridviewColNumb();


            //Setup the view for the categories
            GridView superCategoryGrid = (GridView) parent.findViewById(R.id.supercategory);
            superCategoryGrid.setAdapter(new PARROTCategoryAdapter(user.getCategories(), parent, R.id.supercategory, user, displayedMainCategoryIndex));
            CategoryController categoryController = new CategoryController(parent);

            try
            {
                SpeechBoardFragment.speechboardPictograms.clear();

                if (pictogramController.getPictogramsByCategory(displayedCategory).size() > MaxNumberOfAllowedPictogramsInCategory)
                {
                    speechboardPictograms = pictogramController.getPictogramsByCategory(displayedCategory).subList(0, MaxNumberOfAllowedPictogramsInCategory);
                }
                else
                {
                    speechboardPictograms = pictogramController.getPictogramsByCategory(displayedCategory);
                }
            }
            catch (OutOfMemoryError e)
            {
                e.getStackTrace();
                return;
            }

            pictogramGrid.setAdapter(new PictogramAdapter(speechboardPictograms, parent.getApplicationContext(), parent, user));

            //setup drag listeners for the views
            //parent.findViewById(R.id.pictogramgrid).setOnDragListener(new SpeechBoardBoxDragListener(parent));
            speechDragListener = new SpeechBoardBoxDragListener(parent, parent.getApplicationContext(), user);

            parent.findViewById(R.id.sentenceboard).setOnDragListener(speechDragListener);

            if(pictogramList.size() == 0)
            {
                for (int i = 0; i < noInSentence; i++)
                {
                    pictogramList.add(null);
                }
            }
            final GirafButton trashCanButton = (GirafButton) parent.findViewById(R.id.btnClear);
            trashCanButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearSentenceboard();
                }
            });

            // Set sentence board width dependent on the screen size
            RelativeLayout playButton = (RelativeLayout) parent.findViewById(R.id.playButtonLayout);
            RelativeLayout.LayoutParams playButtonLayout = new RelativeLayout.LayoutParams(playButton.getLayoutParams());

            RelativeLayout sentenceBoard = (RelativeLayout) parent.findViewById(R.id.sentenceBoardLayout);

            int trashButtonWidth = GComponent.DpToPixel((int) getResources().getDimension(R.dimen.buttonTrashWidth), parent.getApplicationContext());
            int playButtonWidth = GComponent.DpToPixel((int) getResources().getDimension(R.dimen.buttonPlayWidth), parent.getApplicationContext());
            RelativeLayout.LayoutParams sBParams = new RelativeLayout.LayoutParams(width - playButtonWidth - trashButtonWidth, GComponent.DpToPixel(150, parent));
            sBParams.leftMargin = trashButtonWidth;
            sentenceBoard.setLayoutParams(sBParams);
            //Delete this? 15/04-2015
            /*
            final PackageManager packMan = parent.getPackageManager();
            List<ApplicationInfo> apps = packMan.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo appInfo : apps)
            {
                if (appInfo.packageName.toString().equalsIgnoreCase(catName))
                {
                    catIntent = packMan.getLaunchIntentForPackage(catName);

                    catIntent.putExtra("currentGuardianID", guadianID);
                    catIntent.putExtra("currentChildID", childID);

                    if (catIntent != null)
                    {
                        catButton.setVisibility(this.getView().VISIBLE);
                        catButton.SetImage(appInfo.loadIcon(packMan));
                    }
                    createOnClickListener(catButton, catIntent);
                }
                else if (appInfo.packageName.toString().equalsIgnoreCase(crocName))
                {
                    crocIntent = packMan.getLaunchIntentForPackage(crocName);

                    crocIntent.putExtra("currentGuardianID", guadianID);
                    crocIntent.putExtra("currentChildID", childID);
                    if (crocIntent != null)
                    {
                        crocButton.setVisibility(this.getView().VISIBLE);
                        crocButton.SetImage(appInfo.loadIcon(packMan));
                    }
                    createOnClickListener(crocButton, crocIntent);
                }
            }
            */
        }

        final GirafButton btnPictosearch = (GirafButton) parent.findViewById(R.id.btnPictosearch);
        btnPictosearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create new fragment and transaction
                callPictosearch();
            }
        });

        final GirafButton btnPlay = (GirafButton) parent.findViewById(R.id.btnPlay);
        //btnPlay.setIcon(getResources().getDrawable(R.drawable.icon_play));
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean change;
                if (pictoMediaPlayer.isPlaying())
                {
                    //btnPlay.setIcon(getResources().getDrawable(R.drawable.icon_stop));
                    pictoMediaPlayer.stopSound();
                    return;
                }

                for(int i = 0; i < pictogramList.size(); i++)
                {
                    change = true;
                    while(change && pictogramList.get(i) == null)
                    {
                        change = false;
                        for (int j = i + 1; j < pictogramList.size(); j++)
                        {
                            if(pictogramList.get(j) != null)
                            {
                                pictogramList.set(j-1,pictogramList.get(j));
                                pictogramList.set(j,null);
                                change = true;
                            }
                        }
                    }
                }

                GridView sentence = (GridView) parent.findViewById(R.id.sentenceboard);
                sentence.setAdapter(new SentenceboardAdapter(pictogramList, parent));
                sentence.invalidate();

                pictoMediaPlayer.playListOfPictograms(pictogramList);
            }
        });

        if(displayPictogramList != null && backToNormalView)
        {
            displayPictograms(displayPictogramList, this.getActivity());
        }

        if(backToNormalView)
        {
            GLayout btnSearch = (GLayout) parent.findViewById(R.id.btnPictosearchLayout);
            btnSearch.SetMarked(true);
        }
        else
        {
            GLayout btnSearch = (GLayout) parent.findViewById(R.id.btnPictosearchLayout);
            btnSearch.SetMarked(false);
        }

        /*
        if(guadianID == -1 && childID == -1)
        {
            parent.findViewById(R.id.catButton).setVisibility(View.GONE);
            parent.findViewById(R.id.crocButton).setVisibility(View.GONE);
        }
        */
    }

public void setGridviewColNumb()
    {
        GridView pictogramGrid = (GridView) parent.findViewById(R.id.pictogramgrid);


        //Setup the view for the sentences
        GridView sentenceBoardGrid = (GridView) parent.findViewById(R.id.sentenceboard);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int colWidth = GComponent.DpToPixel(125, parent.getApplicationContext());
        sentenceBoardGrid.setColumnWidth(colWidth);

        int trashButtonWidth = GComponent.DpToPixel((int) getResources().getDimension(R.dimen.buttonTrashWidth), parent.getApplicationContext());
        int playButtonWidth = GComponent.DpToPixel((int) getResources().getDimension(R.dimen.buttonPlayWidth), parent.getApplicationContext());

        int sentenceWidth = width - (trashButtonWidth + playButtonWidth);
        int noInSentence = sentenceWidth/colWidth;

        sentenceBoardGrid.setNumColumns(noInSentence);

        int categoryWidth = 2*150;
        int scrollbarWidth = 50;
        if(backToNormalView)
        {
            categoryWidth = 0;
        }
        int pictogramgridWidth = width-GComponent.DpToPixel(categoryWidth+scrollbarWidth, parent.getApplicationContext()) + playButtonWidth;

        int pictogramWidth = 200;
        if(PARROTProfile.PictogramSize.MEDIUM == user.getPictogramSize())
        {
            pictogramWidth = 160;
        }
        pictogramGrid.setColumnWidth(pictogramWidth);
        int piccolnumb = pictogramgridWidth/pictogramWidth;
        pictogramGrid.setNumColumns(piccolnumb);
    }

    /**
     * fill the sentenceboard with empty pictograms
     */
    public void clearSentenceboard()
    {
        for(int i= 0; i <= pictogramList.size()-1; i++)
        {
            pictogramList.set(i, null);
        }

        GridView speech = (GridView) parent.findViewById(R.id.sentenceboard);

        speech.setAdapter(new SentenceboardAdapter(pictogramList, parent));
        speech.invalidate();
    }

    public void displayPictograms(List<dk.aau.cs.giraf.dblib.models.Pictogram> pictograms, Activity activity)
    {

        speechboardPictograms = (ArrayList) pictograms;

        activity.findViewById(R.id.psupercategory).setVisibility(View.GONE);
        //activity.findViewById(R.id.btnSettings).setVisibility(View.GONE);
        /*
        activity.findViewById(R.id.catButton).setVisibility(View.GONE);
        activity.findViewById(R.id.crocButton).setVisibility(View.GONE);
        */
        LinearLayout pictogramGridWrapper = (LinearLayout) activity.findViewById(R.id.ppictogramview);
        pictogramGridWrapper.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

        GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);

        pictogramGrid.setAdapter(new PictogramAdapter(pictograms, activity.getApplicationContext(), activity, user));
        pictogramGrid.invalidate();
    }

    // Create onclicklistener for GButton
    private void createOnClickListener(GButton button, final Intent intent)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("currentChildID", user.getProfileID());
                startActivity(intent);
            }
        });
    }

    /**
     * Opens pictosearch application, so pictograms can be loaded into pictocreator. //TODO Pictosearch will be renamed some day
     */
    private void callPictosearch(){
        if(!backToNormalView)
        {
            backToNormalView = true;
            Intent intent = new Intent();

            try{
                intent.setComponent(new ComponentName( "dk.aau.cs.giraf.pictosearch",  "dk.aau.cs.giraf.pictosearch.PictoAdminMain"));
                intent.putExtra("currentChildID", user.getProfileID());
                intent.putExtra("purpose", "multi");

                startActivityForResult(intent, parent.RESULT_FIRST_USER);
            } catch (Exception e){
                Toast.makeText(parent, "Pictosearch er ikke installeret.", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            backToNormalView = false;
            setGridviewColNumb();
            Activity activity = this.getActivity();
            activity.findViewById(R.id.psupercategory).setVisibility(View.VISIBLE);
            //activity.findViewById(R.id.btnSettings).setVisibility(View.VISIBLE);

            /*
            if(guadianID != -1 || childID != -1)
            {
                activity.findViewById(R.id.catButton).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.crocButton).setVisibility(View.VISIBLE);
            }
            */

            LinearLayout pictogramGridWrapper = (LinearLayout) activity.findViewById(R.id.ppictogramview);
            pictogramGridWrapper.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

            if(displayedCategory != null)
            {
                try
                {
                    SpeechBoardFragment.speechboardPictograms.clear();

                    if (pictogramController.getPictogramsByCategory(displayedCategory).size() > MaxNumberOfAllowedPictogramsInCategory)
                    {
                        speechboardPictograms = pictogramController.getPictogramsByCategory(displayedCategory).subList(0, MaxNumberOfAllowedPictogramsInCategory);
                    }
                    else
                    {
                        speechboardPictograms = pictogramController.getPictogramsByCategory(displayedCategory);
                    }
                }
                catch (OutOfMemoryError e)
                {
                    e.getStackTrace();
                    return;
                }
            }

            GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);

            pictogramGrid.setAdapter(new PictogramAdapter(speechboardPictograms, activity.getApplicationContext(), activity, user));
            pictogramGrid.invalidate();

            GLayout btnSearch = (GLayout) parent.findViewById(R.id.btnPictosearchLayout);
            btnSearch.SetMarked(false);
        }
    }

    /**
     * This method gets the pictogram that are returned by pictosearch.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == parent.RESULT_OK){
            loadPictogram(data);
        }
    }

    private void loadPictogram(Intent data){
        int[] pictogramIDs = {};
        try{
            pictogramIDs = data.getExtras().getIntArray("checkoutIds");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        List<dk.aau.cs.giraf.dblib.models.Pictogram> selectedPictograms = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();
        for (int i = 0; i < pictogramIDs.length; i++)
        {
            selectedPictograms.add(pictogramController.getPictogramById(pictogramIDs[i]));
        }
        displayPictogramList = selectedPictograms;
    }
}
