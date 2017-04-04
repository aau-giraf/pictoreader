package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;

import dk.aau.cs.giraf.pictoreader.PictoreaderProfile.PictogramSize;

;

public class OptionFragment extends Fragment {

    private PictoreaderProfile user;
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
     * This is called when exitting the activity.
     */
    @Override
    public void onPause() {
        super.onPause();
        dataloader.saveChanges(user);
        MainActivity.setUser(user);

    }

    /**
     * This is called when upon returning to the activity or after onCreate.
     */
    @Override
    public void onResume() {
        super.onResume();
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.activity_setting, null);

        //Set the background
        view.setBackgroundColor(getResources().getColor(R.color.giraf_background));

        parrent.setContentView(view);
        parrent.invalidateOptionsMenu();
        user = MainActivity.getUser();
        dataloader = new PARROTDataLoader(parrent, false, this.getActivity());


        //get the current Settings
        readTheCurrentData();

        Switch pictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
        pictogramSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSizePictogramChanged(view);
            }
        });
        //pictogramSize.refresh();

        CheckBox textChangeCheckBox = (CheckBox) parrent.findViewById(R.id.checkBoxShowText);
        textChangeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowTextChanged(view);
            }
        });

    }

    /**
     * get the current Settings and show it in the UI.
     */
    public void readTheCurrentData() {

        int noOfPlacesInSentenceboard = user.getNumberOfSentencePictograms();
        boolean showText = user.getShowText();
        PictoreaderProfile.PictogramSize pictogramSize = user.getPictogramSize();

        if (pictogramSize == PictoreaderProfile.PictogramSize.MEDIUM) {
            Switch switchPictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
            if (switchPictogramSize.isChecked()) ;
            {
                //switchPictogramSize.Toggle();
                switchPictogramSize.setChecked(true);
            }
            switchPictogramSize.toggle();
        } else if (pictogramSize == PictoreaderProfile.PictogramSize.LARGE) {
            Switch switchPictogramSize = (Switch) parrent.findViewById(R.id.swtPictogramSize);
            switchPictogramSize.setChecked(true);
        }


        CheckBox checkBox = (CheckBox) parrent.findViewById(R.id.checkBoxShowText);
        if (showText) {

            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }


    /**
     * When mediumPicRadioButton or largePicRadioButton is clicked, this happens.
     * Change pictogram size.
     *
     * @param view mediumPicRadioButton or largePicRadioButton
     */
    public void onSizePictogramChanged(View view) {
        boolean checked = ((Switch) view).isChecked();

        if (checked) {
            user.setPictogramSize(PictogramSize.LARGE);
        } else {
            user.setPictogramSize(PictogramSize.MEDIUM);
        }
    }

    /**
     * When checkBoxShowText is clicked, this happens.
     * change whether a child can handle text or not.
     *
     * @param view on show text changed
     */
    public void onShowTextChanged(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            user.setShowText(true);
        } else {
            user.setShowText(false);
        }
    }


}


