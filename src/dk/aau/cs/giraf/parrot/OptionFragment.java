package dk.aau.cs.giraf.parrot;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import dk.aau.cs.giraf.parrot.PARROTProfile.PictogramSize;

public class OptionFragment extends Fragment{

	private PARROTProfile user;
	private PARROTDataLoader dataloader;
	private Activity parrent;
	
	
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
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		menu.findItem(R.id.goToParrot).setVisible(true);
		menu.findItem(R.id.goToSettings).setVisible(false);
		menu.findItem(R.id.goToLauncher).setVisible(false);
		menu.findItem(R.id.clearBoard).setVisible(false);
		
		super.onPrepareOptionsMenu(menu);
	}
	/**
	 * Selector for what happens when a menu Item is clicked
	 */
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch(item.getItemId()){
		case R.id.goToParrot:
			PARROTActivity parrotA= new PARROTActivity();
			parrotA.switchTabs();
			break;
		}
		return true;
	}
	
	/**
	 * This is called when exitting the activity 
	 */
	@Override
	public void onPause() {
		super.onPause();
		dataloader.saveChanges(user);
		PARROTActivity.setUser(user);
		
	}
		
	/**
	 * This is called when upon returning to the activity or after onCreate.
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		parrent.setContentView(R.layout.activity_setting);
		parrent.invalidateOptionsMenu();
		user = PARROTActivity.getUser();
		dataloader = new PARROTDataLoader(parrent, false);
		
		
		//Setup of the spinner with is the selector of how many of boxes the child can handle in the sentenceboard
		Spinner spinner = (Spinner) parrent.findViewById(R.id.spinnerNoOfsentence);
		// Create an ArrayAdapter using the string array and a default spinner layout
		Integer[] items = new Integer[]{1,2,3,4,5,6};
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(parrent,android.R.layout.simple_spinner_item, items);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		//get the current Settings
        readTheCurrentData();
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
			public void onItemSelected(AdapterView<?> parent, View view, 
                    int pos, long id) {
                user.setNumberOfSentencePictograms((Integer)parent.getItemAtPosition(pos));
                
            }

            @Override
			public void onNothingSelected(AdapterView<?> parent) {
                // do nothing   
            }        
        }); 
        
        Button changeColor = (Button) parrent.findViewById(R.id.buttonChangeSentenceColor);
        changeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onSentenceboardColorChanged(v);
            }
        });
        RadioButton mRadioButton = (RadioButton) parrent.findViewById(R.id.mediumPicRadioButton);
        mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onSizePictogramChanged(v);
            }
        });
        RadioButton lRadioButton = (RadioButton) parrent.findViewById(R.id.largePicRadioButton);
        lRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onSizePictogramChanged(v);
            }
        }); 
        CheckBox textChangeCheckBox = (CheckBox) parrent.findViewById(R.id.checkBoxShowText);
        textChangeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onShowTextChanged(v);
            }
        }); 
        
        
    }
	
	/**
	 * get the current Settings and show it in the UI
	 */
	public void readTheCurrentData() {
		
		int noOfPlacesInSentenceboard = user.getNumberOfSentencePictograms();
		boolean showText = user.getShowText();
		PARROTProfile.PictogramSize pictogramSize = user.getPictogramSize();
		
		if(pictogramSize == PARROTProfile.PictogramSize.MEDIUM)
		{ 
			RadioButton radioB = (RadioButton) parrent.findViewById(R.id.mediumPicRadioButton);
			radioB.setChecked(true);
		}
		else if(pictogramSize == PARROTProfile.PictogramSize.LARGE)
		{
			RadioButton radioB = (RadioButton) parrent.findViewById(R.id.largePicRadioButton);
			radioB.setChecked(true);
		}

		Spinner spinner = (Spinner) parrent.findViewById(R.id.spinnerNoOfsentence);
		spinner.setSelection(noOfPlacesInSentenceboard-1,true);
		
		CheckBox checkBox  = (CheckBox) parrent.findViewById(R.id.checkBoxShowText);
		if(showText)
		{
			
			checkBox.setChecked(true);
		}
		else
		{
			checkBox.setChecked(false);
		}
	}
	
	/**
	 * When buttonChangeSentenceColor is clicked this happens, change the color of the sentenceboard
	 * @param view, the buttonChangeSentenceColor
	 */
	public void onSentenceboardColorChanged(View view)
	{
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(parrent, 
				user.getSentenceBoardColor(),
				new OnAmbilWarnaListener() {
			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
			}

			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {
				user.setSentenceBoardColor(color);
				Log.v("MessageParrot", "color: " + color);
			}
		});
		dialog.show();

	}
	
	/**
	 * When mediumPicRadioButton or largePicRadioButton is clicked, this happens. 
	 * Change pictogram size.
	 * @param view, mediumPicRadioButton or largePicRadioButton
	 */
	public void onSizePictogramChanged(View view)
	{
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.mediumPicRadioButton:
	            if (checked)
	                user.setPictogramSize(PictogramSize.MEDIUM);
	            break;
	        case R.id.largePicRadioButton:
	            if (checked)
	            	user.setPictogramSize(PictogramSize.LARGE);
	            break;
	    }
	}
	
	/**
	 * When checkBoxShowText is clicked, this happens.
	 * change whether a child can handle text or not. 
	 * @param view
	 */
	public void onShowTextChanged(View view)
	{
		 // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    if (checked)
	    {
	    	user.setShowText(true);
	    }         
	    else
	    {
	    	user.setShowText(false);
	    }
	}


}


