package dk.aau.cs.giraf.parrot;

import dk.aau.cs.giraf.gui.GComponent;
import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
        //GComponent.SetBaseColor(0xFF961BC2);
    }

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.goToParrot).setVisible(true);
		menu.findItem(R.id.goToSettings).setVisible(false);
		menu.findItem(R.id.goToLauncher).setVisible(false);
		menu.findItem(R.id.clearBoard).setVisible(false);
	}
	/**
	 * Selector for what happens when a menu Item is clicked
	 */
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch(item.getItemId()){
		case R.id.goToParrot:
			MainActivity parrotA= new MainActivity();
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
        v.setBackgroundColor(GComponent.GetBackgroundColor());

        parrent.setContentView(v);
		parrent.invalidateOptionsMenu();
		user = MainActivity.getUser();
		dataloader = new PARROTDataLoader(parrent, false, this.getActivity());



		//get the current Settings
        readTheCurrentData();

        final SeekBar sk=(SeekBar) parrent.findViewById(R.id.sbarNumberOfPictograms);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

        TextView numberOfPictograms = (TextView) parrent.findViewById(R.id.txtNumberofPictograms);


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            user.setNumberOfSentencePictograms(progress+1);

            numberOfPictograms.setText(String.valueOf(progress+1));
        }
        });

        Switch pictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
        pictogramSize.setOnClickListener(new View.OnClickListener() {
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



        Button button = (Button) parrent.findViewById(R.id.btnBack);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().add(new SpeechBoardFragment(), "1").commit();
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
			Switch switchPictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
            switchPictogramSize.setChecked(false);

		}
		else if(pictogramSize == PARROTProfile.PictogramSize.LARGE)
		{
            Switch switchPictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
            switchPictogramSize.setChecked(true);

		}


        SeekBar seek = (SeekBar) parrent.findViewById(R.id.sbarNumberOfPictograms);

        seek.setProgress(noOfPlacesInSentenceboard-1);

        TextView tview = (TextView) parrent.findViewById(R.id.txtNumberofPictograms);
        tview.setText(String.valueOf(noOfPlacesInSentenceboard));
		
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


