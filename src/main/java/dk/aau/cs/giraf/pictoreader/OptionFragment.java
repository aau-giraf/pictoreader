package dk.aau.cs.giraf.pictoreader;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GSwitch;
import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import dk.aau.cs.giraf.pictoreader.PARROTProfile.PictogramSize;

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
        //GComponent.SetBaseColor(0xFF961BC2);
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
        v.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.GRADIENT));

        parrent.setContentView(v);
		parrent.invalidateOptionsMenu();
		user = MainActivity.getUser();
		dataloader = new PARROTDataLoader(parrent, false, this.getActivity());



		//get the current Settings
        readTheCurrentData();

        GSwitch pictogramSize = (GSwitch) parrent.findViewById(R.id.swtPictogramSize);
        pictogramSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onSizePictogramChanged(v);
            }
        });
        pictogramSize.refresh();

        CheckBox textChangeCheckBox = (CheckBox) parrent.findViewById(R.id.checkBoxShowText);
        textChangeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	onShowTextChanged(v);
            }
        });


        Button buttonBack = (Button) parrent.findViewById(R.id.btnBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().add(new SpeechBoardFragment(parrent.getApplicationContext()), "1").commit();
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
			GSwitch switchPictogramSize = (GSwitch) parrent.findViewById(R.id.swtPictogramSize);
            if(switchPictogramSize.isToggled())
            {
                switchPictogramSize.Toggle();
            }
            switchPictogramSize.refresh();
		}
		else if(pictogramSize == PARROTProfile.PictogramSize.LARGE)
		{
            GSwitch switchPictogramSize = (GSwitch) parrent.findViewById(R.id.swtPictogramSize);
            switchPictogramSize.setToggled(true);
            switchPictogramSize.refresh();
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
	    boolean checked = ((GSwitch) view).isToggled();

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

