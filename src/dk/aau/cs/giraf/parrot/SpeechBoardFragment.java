package dk.aau.cs.giraf.parrot;


import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.gui.*;
import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.Category;
import dk.aau.cs.giraf.oasis.lib.models.PictogramCategory;
import dk.aau.cs.giraf.pictogram.Pictogram;

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
	private PARROTProfile user = null;
	private static Pictogram emptyPictogram = null;
    public static SpeechBoardBoxDragListener speechDragListener;

    private PictogramController pictogramController;
    private PictogramCategoryController pictogramCategoryController;

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		menu.findItem(R.id.goToParrot).setVisible(false);
		menu.findItem(R.id.goToSettings).setVisible(true);
		menu.findItem(R.id.goToLauncher).setVisible(true);
		menu.findItem(R.id.clearBoard).setVisible(true);
		
		super.onPrepareOptionsMenu(menu);
	}
	
	/**
	 * Selector for what happens when a menu Item is clicked
	 */
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch(item.getItemId()){
		case R.id.clearBoard:
			clearSentenceboard();
			break;
		case R.id.goToLauncher:
			returnToLauncher();
			break;
		case R.id.goToSettings:
            MainActivity parrotA= new MainActivity();
			parrotA.switchTabs();
			break;
		}
		return true;
	}
	
	/**
	 * this activating a new  Activity class which handles the settings which can be changed. 
	 */
	public void goToSettings(){
		//TODO
	}
	/**
	 * This exits the MainActivity and should return to the giraf-launcher.
	 */
	public void returnToLauncher()
	{
		parrent.finish();
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

			//Setup the view for the listing of pictograms in pictogramgrid
			final GGridView pictogramGrid = (GGridView) parrent.findViewById(R.id.pictogramgrid);

			
			//Setup the view for the sentences
            GGridView sentenceBoardGrid = (GGridView) parrent.findViewById(R.id.sentenceboard);
			sentenceBoardGrid.setAdapter(new SentenceboardAdapter(pictogramList, parrent.getApplicationContext()));
			int noInSentence=user.getNumberOfSentencePictograms();
			sentenceBoardGrid.setNumColumns(noInSentence);

			//setup pictogramGrid.setNumColumns and sentenceBoardGrid.setColumnWidth

            sentenceBoardGrid.setColumnWidth(160);

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
            noInSentence = (int)width/(145+dpToPx(16));

            if(pictogramList.size() == 0)
            {
                for (int i = 0; i < noInSentence; i++)
                {
                    pictogramList.add(null);
                }
            }

            sentenceBoardGrid.setNumColumns(noInSentence);

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
                for(int q = 0; q < pictogramList.size(); q++)
                {
                for(int i = 0; i < pictogramList.size(); i++)
                {
                    if(pictogramList.get(i) == null)
                    {
                        for (int j = i + 1; j < pictogramList.size(); j++)
                        {
                            pictogramList.set(j-1,pictogramList.get(j));
                            pictogramList.set(j,null);
                        }
                    }
                }
                }
                GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
                speech.setAdapter(new SentenceboardAdapter(pictogramList, parrent));
                speech.invalidate();
            }
        });
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

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

	/**
	 *This function set the colors in the speechBoardFragment
	 */

	/**
	 * set color for the PictogramGrid, which changes upon a change of category to be shown
	 */

}

