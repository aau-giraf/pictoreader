package dk.aau.cs.giraf.parrot;


import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.content.pm.PackageManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.gui.*;
import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.Category;
import dk.aau.cs.giraf.oasis.lib.models.PictogramCategory;
import dk.aau.cs.giraf.pictogram.PictoMediaPlayer;
import dk.aau.cs.giraf.pictogram.Pictogram;
import dk.aau.cs.giraf.pictogram.TextToSpeech;

/**
 * @author PARROT spring 2012 and adapted by SW605f13
 * This class handles the views and actions of the speechLearning "Tale" function
 */
public class SpeechBoardFragment extends Fragment
{

	private Activity parrent;
	
	//Remembers the index of the pictogram that is currently being dragged.
	public static int draggedPictogramIndex = -1;
	public static int dragOwnerID =-1;
    public static int MaxNumberOfAllowedPictogramsInCategory = 125;

    //Serves as the back-end storage for the visual speechboard
	public static List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> speechboardPictograms = new ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram>();
	
	//This category contains the pictograms on the sentenceboard
	public static ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram> pictogramList = new ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram>();
	//This category contains the pictograms displayed on the big board
	public static Category displayedCategory = null;
    public static Category displayedMainCategory = null;
    public static int displayedMainCategoryIndex = 0;
    public static int displayedSubCategoryIndex = -1;
	private PARROTProfile user = null;
	private static Pictogram emptyPictogram = null;
    public static SpeechBoardBoxDragListener speechDragListener;

    private PictogramController pictogramController;
    private PictogramCategoryController pictogramCategoryController;

    private Context context;

    private PictoMediaPlayer pictoMediaPlayer;
    private List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> displayPictogramList = null;

    private boolean backToNormalView = false;

    int guadianID = (int) MainActivity.getGuardianID();
    int childID = MainActivity.getChildID();

    public SpeechBoardFragment(Context c)
    {
        context = c;
        pictoMediaPlayer =  new PictoMediaPlayer(context);
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.parrent = activity;
        pictogramController = new PictogramController(activity.getApplicationContext());
        pictogramCategoryController = new PictogramCategoryController(activity.getApplicationContext());
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}


	/**
	 * Most is done in this. eg setup the gridviews get data shown in the gridviews.
	 */
	@Override
	public void onResume() {
		super.onResume();
		parrent.invalidateOptionsMenu();


        View v = LayoutInflater.from(parrent.getApplicationContext()).inflate(R.layout.speechboard_layout, null);
        //Set the background
        v.setBackgroundColor(GComponent.GetBackgroundColor());

        parrent.setContentView(v);

		user=MainActivity.getUser();
		
		//check whether there are categories
		if(user.getCategoryAt(0)!=null)
		{
			displayedCategory = user.getCategoryAt(0);
            displayedMainCategory = displayedCategory;

			//Setup the view for the listing of pictograms in pictogramgrid
			final GGridView pictogramGrid = (GGridView) parrent.findViewById(R.id.pictogramgrid);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            //Setup the view for the sentences
            GGridView sentenceBoardGrid = (GGridView) parrent.findViewById(R.id.sentenceboard);
			sentenceBoardGrid.setAdapter(new SentenceboardAdapter(pictogramList, parrent.getApplicationContext()));
			int noInSentence=user.getNumberOfSentencePictograms();
			sentenceBoardGrid.setNumColumns(noInSentence);

			//setup pictogramGrid.setNumColumns and sentenceBoardGrid.setColumnWidth
            setGridviewColNumb();

			
			//Setup the view for the categories
            GGridView superCategoryGrid = (GGridView) parrent.findViewById(R.id.supercategory);
			superCategoryGrid.setAdapter(new PARROTCategoryAdapter(user.getCategories(), parrent, R.id.supercategory, user, displayedMainCategoryIndex));
            GGridView subCategoryGrid = (GGridView) parrent.findViewById(R.id.subcategory);
            CategoryController categoryController = new CategoryController(parrent);

            try
            {
			    subCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getSubcategoriesByCategory(displayedCategory), parrent, R.id.subcategory, user, displayedSubCategoryIndex));
            }
            catch (OutOfMemoryError e)
            {
                e.getStackTrace();
                return;
            }

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

		 	pictogramGrid.setAdapter(new PictogramAdapter(speechboardPictograms, parrent.getApplicationContext(),parrent, user));

			//setup drag listeners for the views
			//parrent.findViewById(R.id.pictogramgrid).setOnDragListener(new SpeechBoardBoxDragListener(parrent));
            speechDragListener = new SpeechBoardBoxDragListener(parrent, parrent.getApplicationContext(), user);

			parrent.findViewById(R.id.sentenceboard).setOnDragListener(speechDragListener);

            if(pictogramList.size() == 0)
            {
                for (int i = 0; i < noInSentence; i++)
                {
                    pictogramList.add(null);
                }
            }

            // Set sentence board width dependent on the screen size
            LinearLayout playButton = (LinearLayout) parrent.findViewById(R.id.playButtonLayout);
            LinearLayout.LayoutParams playButtonLayout = new LinearLayout.LayoutParams(playButton.getLayoutParams());

            LinearLayout sentenceBoard = (LinearLayout) parrent.findViewById(R.id.sentenceBoardLayout);
            LinearLayout.LayoutParams sBParams = new LinearLayout.LayoutParams(width - playButtonLayout.width, GComponent.DpToPixel(150, parrent));

            sentenceBoard.setLayoutParams(sBParams);

            // Initialise cat and croc buttons
            String catName = "dk.aau.cs.giraf.pictoadmin";
            String crocName = "dk.aau.cs.giraf.pictocreator";
            Intent catIntent = null;
            Intent crocIntent = null;
            GButton catButton = (GButton) parrent.findViewById(R.id.catButton);
            GButton crocButton = (GButton) parrent.findViewById(R.id.crocButton);


            final PackageManager packMan = parrent.getPackageManager();
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

            final GButtonTrash button = (GButtonTrash) parrent.findViewById(R.id.btnClear);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearSentenceboard();
                }
            });
		}

        final GButtonSettings btnOptions = (GButtonSettings) parrent.findViewById(R.id.btnSettings);

        btnOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment newFragment = new OptionFragment();

                getFragmentManager().beginTransaction()
                        .add(newFragment, "options")
                        // Add this transaction to the back stack
                        .addToBackStack("options")
                        .commit();
            }
        });

        final GButtonSearch btnPictosearch = (GButtonSearch) parrent.findViewById(R.id.btnPictosearch);

        btnPictosearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create new fragment and transaction
                callPictosearch();
            }
        });

        final GButtonPlay btnPlay = (GButtonPlay) parrent.findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean change;

/*                if (btnPlay.isPlaying)
                {
                    btnPlay.setPlayIcon();
                    pictoMediaPlayer.stopSound();
                    return;
                }*/

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

                GridView sentence = (GridView) parrent.findViewById(R.id.sentenceboard);
                sentence.setAdapter(new SentenceboardAdapter(pictogramList, parrent));
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
            GLayout btnSearch = (GLayout)parrent.findViewById(R.id.btnPictosearchLayout);
            btnSearch.SetMarked(true);
        }
        else
        {
            GLayout btnSearch = (GLayout)parrent.findViewById(R.id.btnPictosearchLayout);
            btnSearch.SetMarked(false);
        }

        TextView selectedCategoryText = (TextView) parrent.findViewById(R.id.textViewSelectedCategory);

        if(backToNormalView)
        {
            selectedCategoryText.setText("");
        }
        else
        {
            selectedCategoryText.setText("Valgt kategori: " + displayedMainCategory.getName());
        }


        if(guadianID == -1 && childID == -1)
        {
            parrent.findViewById(R.id.catButton).setVisibility(View.GONE);
            parrent.findViewById(R.id.crocButton).setVisibility(View.GONE);
        }
	}

    public void setGridviewColNumb()
    {
        GGridView pictogramGrid = (GGridView) parrent.findViewById(R.id.pictogramgrid);


        //Setup the view for the sentences
        GGridView sentenceBoardGrid = (GGridView) parrent.findViewById(R.id.sentenceboard);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int buttonsWidth = 100;
        int colWidth = GComponent.DpToPixel(125, parrent.getApplicationContext());
        sentenceBoardGrid.setColumnWidth(colWidth);
        int noInSentence = (width-GComponent.DpToPixel(buttonsWidth, parrent))/(colWidth);
        sentenceBoardGrid.setNumColumns(noInSentence);

        int categoryWidth = 2*150;
        int scrollbarWidth = 50;
        if(backToNormalView)
        {
            categoryWidth = 0;
        }
        int pictogramgridWidth = width-GComponent.DpToPixel(categoryWidth+buttonsWidth+scrollbarWidth,parrent.getApplicationContext());

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

        GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);

        speech.setAdapter(new SentenceboardAdapter(pictogramList, parrent));
        speech.invalidate();
	}

    public void displayPictograms(List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> pictograms, Activity activity)
    {

        speechboardPictograms = (ArrayList) pictograms;

        activity.findViewById(R.id.psubcategory).setVisibility(View.GONE);
        activity.findViewById(R.id.psupercategory).setVisibility(View.GONE);
        activity.findViewById(R.id.btnSettings).setVisibility(View.GONE);
        activity.findViewById(R.id.catButton).setVisibility(View.GONE);
        activity.findViewById(R.id.crocButton).setVisibility(View.GONE);
         
        LinearLayout pictogramGridWrapper = (LinearLayout) activity.findViewById(R.id.ppictogramview);
        pictogramGridWrapper.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;

        GGridView pictogramGrid = (GGridView) activity.findViewById(R.id.pictogramgrid);

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
     * Opens pictosearch application, so pictograms can be loaded into pictocreator.
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

                startActivityForResult(intent, parrent.RESULT_FIRST_USER);
            } catch (Exception e){
                Toast.makeText(parrent, "Pictosearch er ikke installeret.", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            backToNormalView = false;
            TextView selectedCategoryText = (TextView) parrent.findViewById(R.id.textViewSelectedCategory);
            selectedCategoryText.setText("Valgt kategori: " + displayedMainCategory.getName());
            setGridviewColNumb();
            Activity activity = this.getActivity();
            activity.findViewById(R.id.psubcategory).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.psupercategory).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.btnSettings).setVisibility(View.VISIBLE);
            if(guadianID != -1 || childID != -1)
            {
                activity.findViewById(R.id.catButton).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.crocButton).setVisibility(View.VISIBLE);
            }


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

            GGridView pictogramGrid = (GGridView) activity.findViewById(R.id.pictogramgrid);

            pictogramGrid.setAdapter(new PictogramAdapter(speechboardPictograms, activity.getApplicationContext(), activity, user));
            pictogramGrid.invalidate();

            GLayout btnSearch = (GLayout)parrent.findViewById(R.id.btnPictosearchLayout);
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

        if (resultCode == parrent.RESULT_OK){
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

            List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> selectedPictograms = new ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram>();
            for (int i = 0; i < pictogramIDs.length; i++)
            {
                selectedPictograms.add(pictogramController.getPictogramById(pictogramIDs[i]));
            }
            displayPictogramList = selectedPictograms;
    }
}

