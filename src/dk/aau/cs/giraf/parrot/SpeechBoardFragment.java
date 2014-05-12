package dk.aau.cs.giraf.parrot;


import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.content.pm.PackageManager;

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
import dk.aau.cs.giraf.pictogram.tts;

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
	//Serves as the back-end storage for the visual speechboard
	public static List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> speechboardPictograms = new ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram>();
	
	//This category contains the pictograms on the sentenceboard
	public static ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram> pictogramList = new ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram>();
	//This category contains the pictograms displayed on the big board
	public static Category displayedCategory = null;
    public static Category displayedMainCategory = null;
    public static int displayedMainCategoryIndex = 0;
	private PARROTProfile user = null;
	private static Pictogram emptyPictogram = null;
    public static SpeechBoardBoxDragListener speechDragListener;

    private PictogramController pictogramController;
    private PictogramCategoryController pictogramCategoryController;

    private Context context;

    private PictoMediaPlayer pictoMediaPlayer;

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

			
			//Setup the view for the sentences
            GGridView sentenceBoardGrid = (GGridView) parrent.findViewById(R.id.sentenceboard);
			sentenceBoardGrid.setAdapter(new SentenceboardAdapter(pictogramList, parrent.getApplicationContext()));
			int noInSentence=user.getNumberOfSentencePictograms();
			sentenceBoardGrid.setNumColumns(noInSentence);

			//setup pictogramGrid.setNumColumns and sentenceBoardGrid.setColumnWidth

            sentenceBoardGrid.setColumnWidth(120);

            if(PARROTProfile.PictogramSize.MEDIUM == user.getPictogramSize())
			{
				pictogramGrid.setNumColumns(6);
				pictogramGrid.setColumnWidth(160);

			}
			else
			{
				pictogramGrid.setNumColumns(5);
				pictogramGrid.setColumnWidth(200);
			}

			
			//Setup the view for the categories 
            GGridView superCategoryGrid = (GGridView) parrent.findViewById(R.id.supercategory);
			superCategoryGrid.setAdapter(new PARROTCategoryAdapter(user.getCategories(), parrent, R.id.supercategory, user));
            GGridView subCategoryGrid = (GGridView) parrent.findViewById(R.id.subcategory);

            CategoryController categoryController = new CategoryController(parrent);

			subCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getSubcategoriesByCategory(displayedCategory), parrent, R.id.subcategory, user));
            speechboardPictograms = pictogramController.getPictogramsByCategory(displayedCategory);
		 	pictogramGrid.setAdapter(new PictogramAdapter(speechboardPictograms, parrent.getApplicationContext(),parrent, user));

			//setup drag listeners for the views
			//parrent.findViewById(R.id.pictogramgrid).setOnDragListener(new SpeechBoardBoxDragListener(parrent));
            speechDragListener = new SpeechBoardBoxDragListener(parrent, parrent.getApplicationContext(), user);

			parrent.findViewById(R.id.sentenceboard).setOnDragListener(speechDragListener);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            noInSentence = width/(145+GComponent.DpToPixel(16, parrent));

            if(pictogramList.size() == 0)
            {
                for (int i = 0; i < noInSentence; i++)
                {
                    pictogramList.add(null);
                }
            }

            sentenceBoardGrid.setNumColumns(noInSentence);

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
                    if (catIntent != null)
                    {
                        catButton.setVisibility(this.getView().VISIBLE);
                    }
                    createOnClickListener(catButton, catIntent);
                }
                else if (appInfo.packageName.toString().equalsIgnoreCase(crocName))
                {
                    crocIntent = packMan.getLaunchIntentForPackage(crocName);
                    if (crocIntent != null)
                    {
                        crocButton.setVisibility(this.getView().VISIBLE);
                    }
                    createOnClickListener(crocButton, crocIntent);
                }
            }

			//Play sound, when click on a pictogram in the sentence board
			sentenceBoardGrid.setOnItemClickListener(new OnItemClickListener() {
                PictogramController pictogramController = new PictogramController(getActivity());

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

                    dk.aau.cs.giraf.oasis.lib.models.Pictogram p = pictogramList.get(position);
                    if (!(p.getId() == -1)) {
                        //PLAY AUDIO HERE
                        //p.playAudio();
                    }
                }
            });

            final GButtonTrash button = (GButtonTrash) parrent.findViewById(R.id.btnClear);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearSentenceboard();
                }
            });

			/*//Drag pictogram from the sentenceBoard, start drag
			sentenceBoardGrid.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    PictogramController pictogramController = new PictogramController(getActivity());

                    try {
                        dk.aau.cs.giraf.oasis.lib.models.Pictogram p = pictogramList.get(position);

                        if (!(p.getId() == -1)) {
                            draggedPictogramIndex = position;
                            dragOwnerID = R.id.sentenceboard;
                            speechDragListener.draggedPictogram = p;
                            ClipData data = ClipData.newPlainText("label", "text"); //TODO Dummy. Pictogram information can be placed here instead.
                            DragShadowBuilder shadowBuilder = new DragShadowBuilder(view);
                            view.startDrag(data, shadowBuilder, view, 0);
                        }
                    }
                    catch (Exception e)
                    {
                        e.getStackTrace();
                    }
                }

            });*/



		}

        GButtonSettings btnOptions = (GButtonSettings) parrent.findViewById(R.id.btnSettings);

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

        GButtonPlay btnPlay = (GButtonPlay) parrent.findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                boolean change;
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

        markSelectedCategory(0,-1,parrent);
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

    private void displayPictograms(List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> pictograms, Activity activity)
    {
        activity.findViewById(R.id.supercategory).setVisibility(View.GONE);
        activity.findViewById(R.id.subcategory).setVisibility(View.GONE);

        GridView pictogramGrid = (GridView) activity.findViewById(R.id.pictogramgrid);
        pictogramGrid.setAdapter(new PictogramAdapter(pictograms, activity.getApplicationContext(), activity, user));

    }

    // Create onclicklistener for GButton
    private void createOnClickListener(GButton button, final Intent intent)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    public static void markSelectedCategory(int mainCategory, int subCategory, Activity activity)
    {
        GGridView mainCat = (GGridView) activity.findViewById(R.id.supercategory);
        for (int i = 0; i < mainCat.getChildCount(); i++)
        {
            boolean mustSet = i == mainCategory;
            ((GSelectableContent)mainCat.getChildAt(i)).SetSelected(mustSet);
        }

        GGridView subCat = (GGridView) activity.findViewById(R.id.subcategory);
        for (int i = 0; i < subCat.getChildCount(); i++)
        {
            boolean mustSet = i == subCategory;
            ((GSelectableContent)subCat.getChildAt(i)).SetSelected(mustSet);
        }
    }

}

