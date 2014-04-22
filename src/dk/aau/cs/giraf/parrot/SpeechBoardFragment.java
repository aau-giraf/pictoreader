package dk.aau.cs.giraf.parrot;


import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import java.util.ArrayList;

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
	public static ArrayList<Pictogram> speechboardPictograms = new ArrayList<Pictogram>();
	
	//This category contains the pictograms on the sentenceboard
	public static ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram> pictogramList = new ArrayList<dk.aau.cs.giraf.oasis.lib.models.Pictogram>();
	//This category contains the pictograms displayed on the big board
	public static Category displayedCategory = null;
	private PARROTProfile user = null;
	private static Pictogram emptyPictogram = null;

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
		parrent.setContentView(R.layout.speechboard_layout);


		
		user=MainActivity.getUser();
		
		//check whether there are categories
		if(user.getCategoryAt(0)!=null)
		{
			displayedCategory = user.getCategoryAt(0);
			clearSentenceboard();

			//Setup the view for the listing of pictograms in pictogramgrid
			final GGridView pictogramGrid = (GGridView) parrent.findViewById(R.id.pictogramgrid);

			
			//Setup the view for the sentences
			GridView sentenceBoardGrid = (GridView) parrent.findViewById(R.id.sentenceboard);
			sentenceBoardGrid.setAdapter(new SentenceboardAdapter(pictogramList, parrent.getApplicationContext()));
			int noInSentence=user.getNumberOfSentencePictograms();
			sentenceBoardGrid.setNumColumns(noInSentence);

			//setup pictogramGrid.setNumColumns and sentenceBoardGrid.setColumnWidth
			if(PARROTProfile.PictogramSize.MEDIUM == user.getPictogramSize())
			{
				pictogramGrid.setNumColumns(6);
				pictogramGrid.setColumnWidth(160);
				sentenceBoardGrid.setColumnWidth(160);

			}
			else
			{
				pictogramGrid.setNumColumns(5);
				pictogramGrid.setColumnWidth(200);
				sentenceBoardGrid.setColumnWidth(200);

			}

			
			//Setup the view for the categories 
            GGridView superCategoryGrid = (GGridView) parrent.findViewById(R.id.supercategory);
			superCategoryGrid.setAdapter(new PARROTCategoryAdapter(user.getCategories(), parrent.getApplicationContext()));
            GGridView subCategoryGrid = (GGridView) parrent.findViewById(R.id.subcategory);

            CategoryController categoryController = new CategoryController(parrent);

			subCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getSubcategoriesByCategory(displayedCategory), parrent.getApplicationContext()));
		 	pictogramGrid.setAdapter(new PictogramAdapter(displayedCategory, parrent.getApplicationContext(),parrent));

			//initialise the colors of the fragment

			//setup drag listeners for the views
			//parrent.findViewById(R.id.pictogramgrid).setOnDragListener(new SpeechBoardBoxDragListener(parrent));
			parrent.findViewById(R.id.sentenceboard).setOnDragListener(new SpeechBoardBoxDragListener(parrent, parrent.getApplicationContext()));

            for (int i = 0; i < noInSentence; i++)
            {
                pictogramList.add(null);
            }

			//Play sound, when click on a pictogram in the sentence board
			sentenceBoardGrid.setOnItemClickListener(new OnItemClickListener() {
                PictogramController pictogramController = new PictogramController(getActivity());
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,	int position, long id) {

					dk.aau.cs.giraf.oasis.lib.models.Pictogram p = pictogramList.get(position);
					if(!(p.getId() ==-1))
					{
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

			//Drag pictogram from the sentenceBoard, start drag
			sentenceBoardGrid.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    PictogramController pictogramController = new PictogramController(getActivity());

                    try {
                        dk.aau.cs.giraf.oasis.lib.models.Pictogram p = pictogramList.get(position);

                        if (!(p.getId() == -1)) {
                            draggedPictogramIndex = position;
                            dragOwnerID = R.id.sentenceboard;
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

            });


			//change category that is to be shown 
			superCategoryGrid.setOnItemClickListener(new OnItemClickListener() {


				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
				{
                    CategoryController categoryController = new CategoryController(parrent.getBaseContext());
					displayedCategory = user.getCategoryAt(position);
					GridView pictogramGrid = (GridView) parrent.findViewById(R.id.pictogramgrid);
					pictogramGrid.setAdapter(new PictogramAdapter(displayedCategory, parrent.getApplicationContext(),parrent));
					//Setup the view for the categories 
					GridView subCategoryGrid = (GridView) parrent.findViewById(R.id.subcategory);
					subCategoryGrid.setAdapter(new PARROTCategoryAdapter(categoryController.getSubcategoriesByCategory(displayedCategory), parrent.getApplicationContext()));
				}
			});
			//change subcategory that is to be shown 
			
			subCategoryGrid.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
				{
                    CategoryController categoryController = new CategoryController(parrent.getBaseContext());
					//this check is neccessary if you click twice at a subcategory it will crash since subCategories does not contain any subCategory
					if(!categoryController.getSubcategoriesByCategory(displayedCategory).isEmpty())
					{
						displayedCategory = categoryController.getSubcategoriesByCategory(displayedCategory).get(position);
						GridView pictogramGrid = (GridView) parrent.findViewById(R.id.pictogramgrid);
						pictogramGrid.setAdapter(new PictogramAdapter(displayedCategory, parrent.getApplicationContext(), parrent));
					}
				}
			});
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

	/**
	 *This function set the colors in the speechBoardFragment
	 */

	/**
	 * set color for the PictogramGrid, which changes upon a change of category to be shown
	 */

}

