package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;

import dk.aau.cs.giraf.pictoreader.PARROTProfile.PictogramSize;

;

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

	/**
	 * This is called when exitting the activity 
	 */
	@Override
	public void onPause() {
		super.onPause();
		dataloader.saveChanges(user);
        MainActivity.setUser(user);
		
	}
		
	/**
	 * This is called when upon returning to the activity or after onCreate.
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
        View v = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.activity_setting, null);

        //Set the background
        v.setBackgroundColor(getResources().getColor(R.color.giraf_background));

        parrent.setContentView(v);
		parrent.invalidateOptionsMenu();
		user = MainActivity.getUser();
		dataloader = new PARROTDataLoader(parrent, false, this.getActivity());



		//get the current Settings
        readTheCurrentData();

        Switch pictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
        pictogramSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onSizePictogramChanged(v);
            }
        });
        //pictogramSize.refresh();

        CheckBox textChangeCheckBox = (CheckBox) parrent.findViewById(R.id.checkBoxShowText);
        textChangeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onShowTextChanged(v);
            }
        });


        /*Button buttonBack = (Button) parrent.findViewById(R.id.btnBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().add(new SpeechBoardFragment(parrent.getApplicationContext()), "1").commit();
            }
        });*/

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
			Switch switchPictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
            if(switchPictogramSize.isChecked());
            {
                //switchPictogramSize.Toggle();
                switchPictogramSize.setChecked(true);
            }
            switchPictogramSize.toggle();
		}
		else if(pictogramSize == PARROTProfile.PictogramSize.LARGE)
		{
            Switch switchPictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
            switchPictogramSize.setChecked(true);
		}

		
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
	 * When mediumPicRadioButton or largePicRadioButton is clicked, this happens. 
	 * Change pictogram size.
	 * @param view, mediumPicRadioButton or largePicRadioButton
	 */
	public void onSizePictogramChanged(View view)
	{
	    boolean checked = ((Switch) view).isChecked();

        if(checked)
        {
            user.setPictogramSize(PictogramSize.LARGE);
        }
        else
        {
            user.setPictogramSize(PictogramSize.MEDIUM);
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


