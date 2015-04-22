package dk.aau.cs.giraf.pictoreader;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.analytics.tracking.android.EasyTracker;
import dk.aau.cs.giraf.activity.GirafActivity;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GToast;
import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.controllers.ApplicationController;
import dk.aau.cs.giraf.oasis.lib.controllers.ProfileController;
import dk.aau.cs.giraf.oasis.lib.models.Application;

/**
 *
 * @author SW605f13-PARROT and PARROT spring 2012.
 *	This is the main Activity Class in Parrot.
 */
public class MainActivity extends GirafActivity {

    private static PARROTProfile parrotUser;
    private static int guardianID;
    private static int childID;
    private static Application app;
    // --Commented out by Inspection (16/04/15 12:25):private static Helper help;
    private static Intent girafIntent;

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

        //Set the background
        v.setBackgroundColor(GComponent.GetBackgroundColor());

        setContentView(v);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean outsideGIRAF = false;

        //These lines get the intent from the launcher //TODO use us when testing with the launcher.
        girafIntent = getIntent();
        guardianID = girafIntent.getIntExtra("currentGuardianID", -1);
        childID = girafIntent.getIntExtra("currentChildID", -1);

        ApplicationController applicationController = new ApplicationController(getApplicationContext());

        app = applicationController.getApplicationByPackageName();

        PARROTDataLoader dataLoader = new PARROTDataLoader(this, true, this.getApplicationContext());

        if(guardianID == -1 )
        {
            ProfileController profileController = new ProfileController(this.getApplicationContext());
            GToast toastMessage = GToast.makeText(this.getApplicationContext(), "Kunne ikke finde en brugerprofil.", 15);
            toastMessage.show();
            outsideGIRAF = true;
            try
            {
                parrotUser = dataLoader.loadProfile((int) profileController.getProfilesByName("Offentlig Bruger").get(0).getId(), app.getId());
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
                parrotUser = dataLoader.loadProfile((int) childID, app.getId());
            }
            else
            {
                parrotUser = dataLoader.loadProfile((int) guardianID, app.getId());
            }
        }

        if(parrotUser != null)
        {
            Log.v("No in sentence", ""+ parrotUser.getNumberOfSentencePictograms());
            Log.v("MessageParrot", "returned");

            // Create new fragment and transaction
            Fragment newFragment = new SpeechBoardFragment(this.getApplicationContext());
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.add(R.id.main, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
        else
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

                outsideGIRAF = false;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
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
    public static PARROTProfile getUser()
    {
        return parrotUser;
    }
    /**
     * set the current child user profile
     * @param user, a PARROTProfile that is a childs profile.
     */
    public static void setUser(PARROTProfile user) {
        parrotUser = user;
    }
    /**
     * @return the guardian/parents id.
     */
    public static long getGuardianID() {
        return guardianID;
    }

    public static int getChildID()
    {
        return childID;
    }

// --Commented out by Inspection START (16/04/15 12:25):
//    /**
//     * @return an instance of Helper.
//     */
//    public static Helper getHelp() {
//        return help;
//    }
// --Commented out by Inspection STOP (16/04/15 12:25)
    /**
     *
     * @return instance of App with this apps data
     */
    public static Application getApp()
    {
        return app;
    }

// --Commented out by Inspection START (16/04/15 12:25):
//    /**
//     * @return The Intent received from the Launcher
//     */
//    public static Intent getGirafIntent() {
//        return girafIntent;
//    }
// --Commented out by Inspection STOP (16/04/15 12:25)
}