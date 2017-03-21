package dk.aau.cs.giraf.pictoreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.activity.GirafActivity;
import dk.aau.cs.giraf.dblib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;
import dk.aau.cs.giraf.dblib.models.Category;
import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.pictogram.PictoMediaPlayer;
import dk.aau.cs.giraf.showcaseview.ShowcaseManager;
import dk.aau.cs.giraf.showcaseview.ShowcaseView;
import dk.aau.cs.giraf.showcaseview.targets.ViewTarget;
import dk.aau.cs.giraf.utilities.GirafScalingUtilities;

/**
 * @author PARROT spring 2012 and adapted by SW605f13
 * This class handles the views and actions of the speechLearning "Tale" function
 * Updated last by: sw614f16
 */

@SuppressLint("ValidFragment") //Avoid default constructor
public class SpeechBoardFragment extends Fragment implements ShowcaseManager.ShowcaseCapable
{
    private Activity parent;
    public static final int GET_MULTIPLE_PICTOGRAMS = 104;
    public static final String PICTO_SEARCH_MULTI_TAG = "multi";
    public static final String REMOVE_PICTOGRAMS = "Fjern piktogrammer";
    public static final String HERE_YOU_REMOVE_PICTOGRAMS = "Her fjerner du piktogrammer der er tilføjet til sætnings kassen";
    List<dk.aau.cs.giraf.dblib.models.Pictogram> selectedPictograms = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();
    //Remembers the index of the pictogram that is currently being dragged.
    public static int draggedPictogramIndex = -1;
    public static int dragOwnerID =-1;
    //We need to set a max on the number of loaded pictograms, since too many would crash
    //the application because of insufficient heap space
    public static int MaxNumberOfAllowedPictogramsInCategory = 125;
    //Serves as the back-end storage for the visual speechboard
    public static List<dk.aau.cs.giraf.dblib.models.Pictogram> speechboardPictograms = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();
    //This category contains the pictograms on the sentenceboard
    public static ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram> sentencePictogramList = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();
    public static Category displayedCategory = null;
    public static Category displayedMainCategory = null;
    public static int displayedMainCategoryIndex = 0;
    private PictoreaderProfile user = null;
    public static SpeechBoardBoxDragListener speechDragListener;
    private PictogramController pictogramController;
    //This variable is used! Android studio is a liar
    private PictogramCategoryController pictogramCategoryController;
    private Context context;
    private List<dk.aau.cs.giraf.dblib.models.Pictogram> displayPictogramList = null;
    private boolean justSearched = false;
    private PictoMediaPlayer myPictoMediaPlayer;
    GirafActivity girafActivity;
    boolean isBound = false;

    /**
     * Used to showcase views
     */
    private ShowcaseManager showcaseManager;


    /**
     * Used to Bind to the PictoMediaPlayer service in Pictogram-lib
     */
    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PictoMediaPlayer.MyLocalBinder binder = (PictoMediaPlayer.MyLocalBinder) service;
            myPictoMediaPlayer = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };


    public SpeechBoardFragment(Context c)
    {
        context = c;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parent = activity;
        pictogramController = new PictogramController(activity.getApplicationContext());
        pictogramCategoryController = new PictogramCategoryController(activity.getApplicationContext());
        girafActivity = (GirafActivity) activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speechboard_layout, container, false);
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
        setupEverything();
    }

    private void setupEverything() {
        View v = LayoutInflater.from(parent.getApplicationContext()).inflate(R.layout.speechboard_layout, null);
        parent.setContentView(v);

        user=MainActivity.getUser();

        //Binds the PictogramMediaPlayer
        Intent intent = new Intent(parent, PictoMediaPlayer.class);
        parent.bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        //check whether there are categories, if not then we will have null pointer exceptions
        if(user.getCategoryAt(0)!=null)
        {
            setupCategoryGrid();
            setupPictogramGrid();
            setupSentenceBoard();
        }

        setupButtons();

        if(displayPictogramList != null && justSearched)
        {
            justSearched = false;
            displayPictograms(displayPictogramList, this.getActivity());
        }
    }

    private void setupCategoryGrid() {
        if (!isGuardianMode()) {
            displayedCategory = user.getCategoryAt(0);
            displayedMainCategory = displayedCategory;

            //Setup the view for the categories
            GridView categoryGrid = (GridView) parent.findViewById(R.id.category);
            categoryGrid.setAdapter(new PARROTCategoryAdapter(user.getCategories(), parent, R.id.category, user, displayedMainCategoryIndex));
        }
    }

    private void setupPictogramGrid()
    {
        Activity activity = this.getActivity();
        GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);

        if(isGuardianMode()) {
            activity.findViewById(R.id.pcategory).setVisibility(View.GONE);
            LinearLayout pictogramGridWrapper = (LinearLayout) activity.findViewById(R.id.ppictogramview);
            pictogramGridWrapper.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            pictogramGrid.invalidate();
        }
        //setup pictogramGrid.setNumColumns and sentenceBoardGrid.setColumnWidth
        setGridviewColNumb();
    }

    private void setupSentenceBoard()
    {
        //Setup drag listeners for the sentence board
        speechDragListener = new SpeechBoardBoxDragListener(parent, parent.getApplicationContext(), user);
        parent.findViewById(R.id.sentenceboard).setOnDragListener(speechDragListener);

        //Setup the view for the sentences
        GridView sentenceBoardGrid = (GridView) parent.findViewById(R.id.sentenceboard);
        sentenceBoardGrid.setAdapter(new SentenceboardAdapter(sentencePictogramList, parent.getApplicationContext()));

        //Setup the column width of the sentence board
        int colWidth = (int) GirafScalingUtilities.convertDpToPixel(parent.getApplicationContext(), 125);
        sentenceBoardGrid.setColumnWidth(colWidth);

        //Get the size of the trashcan and playbutton width
        int trashButtonWidth = 0;
        if(isGuardianMode()) {
            trashButtonWidth = (int) GirafScalingUtilities.convertDpToPixel(parent.getApplicationContext(), getResources().getDimension(R.dimen.buttonPlayWidth));
            RelativeLayout trashLayout = (RelativeLayout) parent.findViewById(R.id.trashButtonLayout);
            trashLayout.getLayoutParams().width = (int) getResources().getDimension(R.dimen.buttonTrashWidth);
        }

        else {
            trashButtonWidth = (int) GirafScalingUtilities.convertDpToPixel(parent.getApplicationContext(), getResources().getDimension(R.dimen.buttonTrashWidth));
        }

        int playButtonWidth = (int) GirafScalingUtilities.convertDpToPixel(parent.getApplicationContext(), getResources().getDimension(R.dimen.buttonPlayWidth));
        int screenSize = (int) GirafScalingUtilities.convertDpToPixel(parent.getApplicationContext(), getScreenSize());

        //Calculate the size of the sentenceboard
        int sentenceSize = screenSize - trashButtonWidth - playButtonWidth;

        //Calculate how many pictograms there are room for on the sentence board
        int noInSentence = sentenceSize / colWidth;
        sentenceBoardGrid.setNumColumns(noInSentence);

        //Add empty pictograms to the list, so it is possible to drag a pictogram to an empty location
        if(sentencePictogramList.size() == 0)
        {
            for (int i = 0; i < noInSentence; i++)
            {
                sentencePictogramList.add(null);
            }
        }

        RelativeLayout sentenceBoard = (RelativeLayout) parent.findViewById(R.id.sentenceBoardLayout);

        //Set the left margin of the sentence board, so it starts where the trashcan button ends
        RelativeLayout.LayoutParams sBParams = new RelativeLayout.LayoutParams(getScreenSize() - playButtonWidth - trashButtonWidth, (int) GirafScalingUtilities.convertDpToPixel(parent, 150));
        sBParams.leftMargin = trashButtonWidth;
        sentenceBoard.setLayoutParams(sBParams);
    }


    private int getScreenSize()
    {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


    private void setupButtons()
    {
        final GirafButton trashCanButton = (GirafButton) parent.findViewById(R.id.btnClear);
        trashCanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearSentenceboard();
            }
        });

        if (isGuardianMode()){
            RelativeLayout.LayoutParams sBParams = new RelativeLayout.LayoutParams((int) GirafScalingUtilities.convertDpToPixel(parent, 100), (int) GirafScalingUtilities.convertDpToPixel(parent, 150));
            trashCanButton.setLayoutParams(sBParams);
        }

        final GirafButton btnPlay = (GirafButton) parent.findViewById(R.id.btnPlay);
        btnPlay.setIcon(getResources().getDrawable(R.drawable.icon_play));
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                removeEmptyPictograms();
                if (!sentencePictogramList.isEmpty() && sentencePictogramList.get(0) != null) {
                    if (myPictoMediaPlayer != null) {
                        if (myPictoMediaPlayer.isPlaying()) {
                            btnPlay.setIcon(getResources().getDrawable(R.drawable.icon_play));
                            myPictoMediaPlayer.stopSound();
                            return;
                        }
                        else {
                            btnPlay.setIcon(getResources().getDrawable(R.drawable.icon_stop));

                            //Used for removing empty pictograms in the sentence board, and repositioning
                            //the pictograms after the empty ones.
                            removeEmptyPictograms();

                            GridView sentence = (GridView) parent.findViewById(R.id.sentenceboard);
                            sentence.setAdapter(new SentenceboardAdapter(sentencePictogramList, parent));
                            sentence.invalidate();

                            myPictoMediaPlayer.playListOfPictograms(sentencePictogramList);


                        }
                    }
                    else{
                        Log.e("PictoMediaPlayer", "Not bound");
                        Toast.makeText(parent, getString(R.string.PictoMediaPlayerNotBound),Toast.LENGTH_LONG);
                    }

                    //Used to change the icon of the play button from Stop to Start when it is done playing pictograms
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (myPictoMediaPlayer.isPlaying() == true) {
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnPlay.setIcon(getResources().getDrawable(R.drawable.icon_play));
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    private void removeEmptyPictograms()
    {
        boolean change;

        for(int i = 0; i < sentencePictogramList.size(); i++)
        {
            change = true;
            while(change && sentencePictogramList.get(i) == null)
            {
                change = false;
                for (int j = i + 1; j < sentencePictogramList.size(); j++)
                {
                    if(sentencePictogramList.get(j) != null)
                    {
                        sentencePictogramList.set(j-1, sentencePictogramList.get(j));

                        sentencePictogramList.set(j,null);
                        change = true;
                    }
                }
            }
        }
    }
    private boolean isGuardianMode(){
        if(MainActivity.getChildID() != -1) {
            return false;
        }

        else if (MainActivity.getGuardianID() != -1) {
            return true;
        }
        return false;
    }

    public void setGridviewColNumb()
    {
        GridView pictogramGrid = (GridView) parent.findViewById(R.id.pictogramgrid);

        //Get the width for the trashbutton.
        int trashButtonWidth = (int) GirafScalingUtilities.convertDpToPixel(parent.getApplicationContext(), getResources().getDimension(R.dimen.buttonTrashWidth));
        int width = (int) GirafScalingUtilities.convertDpToPixel(parent.getApplicationContext(),getScreenSize());
        int sentenceWidth = width - trashButtonWidth;

        int pictogramgridWidth = 0;

        if(isGuardianMode())
        {
            pictogramgridWidth = sentenceWidth + trashButtonWidth;
        }

        else
        {
            pictogramgridWidth = sentenceWidth;
        }

        int pictogramWidth = 200;
        if(PictoreaderProfile.PictogramSize.MEDIUM == user.getPictogramSize())
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
        for(int i= 0; i <= sentencePictogramList.size()-1; i++)
        {
            sentencePictogramList.set(i, null);
        }

        GridView sentenceBoard = (GridView) parent.findViewById(R.id.sentenceboard);

        sentenceBoard.setAdapter(new SentenceboardAdapter(sentencePictogramList, parent));
        sentenceBoard.invalidate();

    }

    public void displayPictograms(List<dk.aau.cs.giraf.dblib.models.Pictogram> pictograms, Activity activity)
    {
        speechboardPictograms = (ArrayList) pictograms;

        activity.findViewById(R.id.pcategory).setVisibility(View.GONE);

        LinearLayout pictogramGridWrapper = (LinearLayout) activity.findViewById(R.id.ppictogramview);
        pictogramGridWrapper.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

        GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);

        pictogramGrid.setAdapter(new PictogramAdapter(pictograms, activity.getApplicationContext(), activity, user));
        pictogramGrid.invalidate();
    }

    /**
     * Returns true if the user have selected some pictogramz
     * @return
     */
    public boolean isAnyPictogramSelected(){
        return !selectedPictograms.isEmpty();
    }
    /**
     * Clears the selected pictograms
     */
    public void ClearPictograms()
    {
        selectedPictograms = new ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram>();;
    }
    /**
     * Opens pictosearch application, so pictograms can be loaded into pictocreator.
     */

    public void callPictosearch(){
            justSearched = true;
            Intent intent = new Intent(this.context, dk.aau.cs.giraf.pictosearch.PictoAdminMain.class);

            try{
                intent.putExtra("purpose", PICTO_SEARCH_MULTI_TAG);
                intent.putExtra("currentGuardianID", MainActivity.getGuardianID());

                if (intent.getExtras().getLong("currentChildID", -1) != -1) {
                    intent.putExtra(getString(R.string.current_child_id), intent.getExtras().getLong("currentChildID", -1));
                } else {
                    intent.putExtra(getString(R.string.current_child_id), (long) -1);
                }

                intent.putExtra(getString(R.string.current_guardian_id), intent.getExtras().getLong("currentGuardianID", -1));
                startActivityForResult(intent, GET_MULTIPLE_PICTOGRAMS);
            } catch (Exception e){
                Toast.makeText(parent, "Pictosearch er ikke installeret.", Toast.LENGTH_LONG).show();
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

    /**
     * This method loads the pictograms into the gird
     * @param data
     */
    public void loadPictogram(Intent data){
        long[] pictogramIDs = {};
        try{
            pictogramIDs = data.getExtras().getLongArray("checkoutIds");
            for (long pictogramID : pictogramIDs) {
                Log.v("No in sentence", "" + String.valueOf(pictogramID));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        for (int i = 0; i < pictogramIDs.length; i++) {
            selectedPictograms.add(pictogramController.getPictogramById(pictogramIDs[i]));
        }
        displayPictogramList = selectedPictograms;
    }

    @Override
    public void showShowcase() {

        // Create a relative location for the next button
        final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        final int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        // Calculate position for the help text
        final int textX = parent.findViewById(R.id.btnClear).getLayoutParams().width + margin * 2;
        final int textY = getResources().getDisplayMetrics().heightPixels / 2 + margin;

        // Create a relative location for the next button
        final RelativeLayout.LayoutParams rightButtonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rightButtonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rightButtonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rightButtonParams.setMargins(margin, margin, margin, margin);

        // Create a relative location for the next button
        final RelativeLayout.LayoutParams centerRightButtonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        centerRightButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);
        centerRightButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        centerRightButtonParams.setMargins(margin, margin, margin, margin);

        showcaseManager = new ShowcaseManager();

        if(isGuardianMode()) {
        //Search Button
        final ViewTarget searchButtonTarget = new ViewTarget(getActivity().getActionBar().getCustomView().findViewById(R.id.search_button), 1.5f);
        showcaseManager.addShowCase(new ShowcaseManager.Showcase() {
            @Override
            public void configShowCaseView(final ShowcaseView showcaseView) {
                showcaseView.setShowcase(searchButtonTarget, true);
                showcaseView.setContentTitle("Søgning");
                showcaseView.setContentText("Her søger du efter pictogrammer");
                showcaseView.setStyle(R.style.GirafCustomShowcaseTheme);
                showcaseView.setButtonPosition(centerRightButtonParams);
                showcaseView.setTextPostion(textX, textY);
            }
        });
        }

        //trash
        final ViewTarget trashButtonTarget = new ViewTarget(R.id.btnClear, getActivity());
        showcaseManager.addShowCase(new ShowcaseManager.Showcase() {
            @Override
            public void configShowCaseView(final ShowcaseView showcaseView) {
                showcaseView.setShowcase(trashButtonTarget, true);
                showcaseView.setContentTitle(REMOVE_PICTOGRAMS);
                showcaseView.setContentText(HERE_YOU_REMOVE_PICTOGRAMS);
                showcaseView.setStyle(R.style.GirafCustomShowcaseTheme);
                showcaseView.setButtonPosition(centerRightButtonParams);
                showcaseView.setTextPostion(textX, textY);
            }
        });

        final ViewTarget playButtonTarget = new ViewTarget(R.id.btnPlay, getActivity());
        showcaseManager.addShowCase(new ShowcaseManager.Showcase() {
            @Override
            public void configShowCaseView(final ShowcaseView showcaseView) {
                showcaseView.setShowcase(playButtonTarget, true);
                showcaseView.setContentTitle("Afspil");
                showcaseView.setContentText("Her afspiller du piktogrammerne der er tilføjet til sætnings kassen");
                showcaseView.setStyle(R.style.GirafCustomShowcaseTheme);
                showcaseView.setButtonPosition(centerRightButtonParams);
                showcaseView.setTextPostion(textX, textY);
            }
        });

        final ViewTarget helpButtonTarget = new ViewTarget(getActivity().getActionBar().getCustomView().findViewById(R.id.help_button), 1.5f);
        showcaseManager.addShowCase(new ShowcaseManager.Showcase() {
            @Override
            public void configShowCaseView(final ShowcaseView showcaseView) {
                showcaseView.setShowcase(helpButtonTarget, true);
                showcaseView.setContentTitle("Hjælpe knap");
                showcaseView.setContentText("Hvis du bliver i tvivl kan du altid få hjælp her");
                showcaseView.setStyle(R.style.GirafLastCustomShowcaseTheme);
                showcaseView.setButtonPosition(centerRightButtonParams);
                showcaseView.setTextPostion(textX, textY);
            }
        });

        showcaseManager.setOnDoneListener(new ShowcaseManager.OnDoneListener() {
            @Override
            public void onDone(ShowcaseView showcaseView) {
                showcaseManager = null;
            }
        });

        showcaseManager.start(getActivity());
    }

    @Override
    public synchronized void hideShowcase() {

        if (showcaseManager != null) {
            showcaseManager.stop();
            showcaseManager = null;
        }
    }

    @Override
    public synchronized void toggleShowcase() {

        if (showcaseManager != null) {
            hideShowcase();
        } else {
            showShowcase();
        }
    }
}