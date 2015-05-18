package dk.aau.cs.giraf.pictoreader;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import dk.aau.cs.giraf.activity.GirafActivity;
import dk.aau.cs.giraf.dblib.Helper;
import dk.aau.cs.giraf.dblib.controllers.ApplicationController;
import dk.aau.cs.giraf.dblib.controllers.ProfileController;
import dk.aau.cs.giraf.dblib.models.Application;
import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.gui.GirafCustomButtonsDialog;
import dk.aau.cs.giraf.showcaseview.ShowcaseManager;

/**
 *
 * @author SW605f13-PARROT and PARROT spring 2012.
 *  This is the main Activity Class in Parrot.
 */
public class MainActivity extends GirafActivity implements GirafCustomButtonsDialog.CustomButtons{

    private static PictoreaderProfile parrotUser;
    private static long guardianID;
    private static long childID;
    private static Application app;
    private static Intent girafIntent;
    private GirafButton btnOptions;
    private GirafButton btnHelp;
    // Helper that will be used to fetch profiles
    private final Helper helper = new Helper(this);

    private GirafCustomButtonsDialog girafConfirmDialog;
    private static final String CONFIRM_EXTEND_TAG = "EXTEND_DIALOG";
    private static final int CONFIRM_EXTEND_ID = 1;

    private SpeechBoardFragment speechBoardFragment;
    private GirafButton replaceButton;
    private GirafButton extendButton;

    /**
     * Used to showcase views
     */
    private ShowcaseManager showcaseManager;

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.main, null);

        setContentView(v);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Bundle extras = getIntent().getExtras();
        boolean outsideGIRAF = false;

        if (ActivityManager.isUserAMonkey()) {
            guardianID = helper.profilesHelper.getGuardians().get(0).getId();
            childID = helper.profilesHelper.getChildren().get(0).getId();
        }

        else if (extras == null || (!extras.containsKey(getString(R.string.current_child_id)) && !extras.containsKey(getString(R.string.current_guardian_id)))) {
            Toast.makeText(this, String.format(getString(R.string.error_must_be_started_from_giraf), getString(R.string.pictoreader)), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        else {
            girafIntent = getIntent();
            guardianID = girafIntent.getExtras().getLong("currentGuardianID", -1);
            childID = girafIntent.getExtras().getLong("currentChildID", -1);
        }

        createOptionsButton();
        createExtendPictogramsButton();
        createReplacePictogramsButton();
        createSearchButton();

        ApplicationController applicationController = new ApplicationController(getApplicationContext()); //mcontext

        app = applicationController.getApplicationByPackageName();
        PARROTDataLoader dataLoader = new PARROTDataLoader(this, true, this.getApplicationContext());

        if(guardianID == -1 )
        {
            ProfileController profileController = new ProfileController(this.getApplicationContext());
            Toast toastMessage = Toast.makeText(this.getApplicationContext(), "Kunne ikke finde en brugerprofil.", Toast.LENGTH_LONG);
            toastMessage.show();
            outsideGIRAF = true;
            try
            {
                parrotUser = dataLoader.loadProfile(profileController.getProfilesByName("Offentlig Bruger").get(0).getId(), app.getId());
            }
            catch (Exception e)
            {
                parrotUser = null;
            }
        }
        else
        {
            if (childID != -1)
            {
                parrotUser = dataLoader.loadProfile(childID, app.getId());
            }
            else
            {
                parrotUser = dataLoader.loadProfile( guardianID, app.getId());
            }
        }

        if(parrotUser != null)
        {
            speechBoardFragment = new SpeechBoardFragment(this.getApplicationContext());
            //getFragmentManager().beginTransaction().add(R.id.main, speechBoardFragment).commit();
            getFragmentManager().beginTransaction().add(R.id.main,speechBoardFragment,"HEJ").commit();
        }
        else //TODO - Se if possibile if yes - create giraf component, else delete.
        {
            if (outsideGIRAF)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Bruger ikke fundet.")
                        .setTitle("Fejl")
                        .setNegativeButton(R.string.returnItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                finish();
                            }
                        });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }
        createHelpButton();
    }

    private void createHelpButton() {
        btnHelp = new GirafButton(this, getResources().getDrawable(R.drawable.icon_help));
        btnHelp.setId(R.id.help_button);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ShowcaseManager.ShowcaseCapable currentContent = (ShowcaseManager.ShowcaseCapable) getFragmentManager().findFragmentByTag("HEJ");
                currentContent.toggleShowcase();
            }
        });
        addGirafButtonToActionBar(btnHelp, GirafActivity.RIGHT);
    }
    private void createExtendPictogramsButton() {
        extendButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_accept), "Ja");
        extendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.girafConfirmDialog.dismiss();
                MainActivity.this.speechBoardFragment.callPictosearch();

            }
        });
    }
    private void createReplacePictogramsButton() {
        replaceButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_cancel), "Nej");
        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.girafConfirmDialog.dismiss();
                MainActivity.this.speechBoardFragment.ClearPictograms();
                MainActivity.this.speechBoardFragment.clearSentenceboard();
                MainActivity.this.speechBoardFragment.callPictosearch();
            }
        });
    }
    private void createOptionsButton() {
        btnOptions = new GirafButton(this,getResources().getDrawable(R.drawable.icon_settings));
        btnOptions.setId(R.id.settings_button);
        btnOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment newFragment = new OptionFragment();
                btnOptions.setVisibility(View.INVISIBLE);
                getFragmentManager().beginTransaction()
                        .add(newFragment, "options")
                                // Add this transaction to the back stack
                        .addToBackStack("options")
                        .commit();
            }
        });
        //TODO if guardian add else nothing
        if (childID == -1) {
            addGirafButtonToActionBar(btnOptions, LEFT);
        }
    }
    private void createSearchButton(){
        GirafButton btnSearch = new GirafButton(this, getResources().getDrawable(R.drawable.icon_search));
        btnSearch.setId(R.id.search_button);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.this.speechBoardFragment.isAnyPictogramSelected()){
                    createExtendDialog();
                }
                else
                    MainActivity.this.speechBoardFragment.callPictosearch();
            }
        });
        //Add the search buttOn to the top bar if not child
        if (childID == -1) {
            addGirafButtonToActionBar(btnSearch, GirafActivity.RIGHT);
        }
    }

    @Override
    public void onBackPressed()
    {
        btnOptions.setVisibility(View.VISIBLE);
        try
        {
            OptionFragment optionFragment = (OptionFragment) getFragmentManager().findFragmentByTag("options");

            if (optionFragment.isResumed())
            {
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().add(new SpeechBoardFragment(this.getApplicationContext()), "1").commit();
            }
            else
            {
                finish();
            }
        }
        catch (Exception e)
        {
            finish();
        }
    }

    /**
     * @return the child's user profile.
     */
    public static PictoreaderProfile getUser()
    {
        return parrotUser;
    }
    /**
     * set the current child user profile
     * @param user, a PictoreaderProfile that is a childs profile.
     */
    public static void setUser(PictoreaderProfile user) {
        parrotUser = user;
    }
    /**
     * @return the guardian/parents id.
     */
    public static long getGuardianID() {
        return guardianID;
    }

    /**
     *
     * @return ChildID
     */
    public static long getChildID()
    {
        return childID;
    }
    /**
     * @return instance of App with this apps data
     */
    public static Application getApp()
    {
        return app;
    }

    /**
     * Creates an extend dialog, promt the user if the would like to save the
     * current selected pictograms.
     */
    public void createExtendDialog(){
        girafConfirmDialog = GirafCustomButtonsDialog.newInstance(
                "Beskrivende Titel",
                "Vil du beholde dine tidligere valgte piktogrammer?",
                CONFIRM_EXTEND_ID);
        girafConfirmDialog.show(getSupportFragmentManager(), CONFIRM_EXTEND_TAG);
    }

    /**
     * Implements fillButtonContainer,
     * @param buttonContainer
     * @param dialogID
     * Adds the buttons the the button container
     */
    @Override
    public void fillButtonContainer(int dialogID, GirafCustomButtonsDialog.ButtonContainer buttonContainer) {
        createExtendPictogramsButton();
        createReplacePictogramsButton();
        buttonContainer.addGirafButton(replaceButton);
        buttonContainer.addGirafButton(extendButton);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof SpeechBoardFragment) {
            speechBoardFragment = (SpeechBoardFragment) fragment;
        }
    }
}