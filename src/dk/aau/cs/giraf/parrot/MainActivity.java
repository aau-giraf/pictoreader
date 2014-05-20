package dk.aau.cs.giraf.parrot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.google.analytics.tracking.android.EasyTracker;

import java.util.List;

import dk.aau.cs.giraf.gui.GCancelButton;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GToast;
import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.controllers.ApplicationController;
import dk.aau.cs.giraf.oasis.lib.controllers.ProfileController;
import dk.aau.cs.giraf.oasis.lib.models.Application;
import dk.aau.cs.giraf.oasis.lib.models.Profile;

/**
 *
 * @author SW605f13-PARROT and PARROT spring 2012.
 *	This is the main Activity Class in Parrot.
 */
public class MainActivity extends Activity {

    private static PARROTProfile parrotUser;
    private static int guardianID;
    private static int childID;
    private PARROTDataLoader dataLoader;
    private static Application app;
    private static Helper help;
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.main, null);

        //Set the background
        v.setBackgroundColor(GComponent.GetBackgroundColor());

        setContentView(v);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //GComponent.SetBaseColor(Color.rgb(255, 160, 0));

        Helper help = new Helper(this.getApplicationContext());
        boolean outsideGIRAF = false;
        //help.CreateDummyData();


        //These lines get the intent from the launcher //TODO use us when testing with the launcher.
        girafIntent = getIntent();
        guardianID = girafIntent.getIntExtra("currentGuardianID", -1);
        childID = girafIntent.getIntExtra("currentChildID", -1);

        ApplicationController applicationController = new ApplicationController(getApplicationContext());

        app = applicationController.getApplicationByPackageName();
        //app = new Application(1, "myapp", "1.0", null, "hah", "Main", "mysecr", 1);


        dataLoader = new PARROTDataLoader(this, true, this.getApplicationContext());

        if (dataLoader != null)
        {
            if(guardianID == -1 )
            {
                ProfileController profileController = new ProfileController(this.getApplicationContext());
                GToast toastMessage = GToast.makeText(this.getApplicationContext(), "Kunne ikke finde en brugerprofil.", 15);
                toastMessage.show();
                outsideGIRAF = true;
                try
                {
                    parrotUser = dataLoader.loadProfile((int)profileController.getProfilesByName("Offentlig Bruger").get(0).getId(), app.getId());
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
                    parrotUser = dataLoader.loadProfile((int)childID, app.getId());
                }
                else
                {
                    parrotUser = dataLoader.loadProfile((int) guardianID, app.getId());
                }
            }
        }
        else
        {
            Log.v("dataLoader is Null","dataLoader is Null");
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
            if (outsideGIRAF == true)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                GCancelButton gCancelButton = new GCancelButton(this.getApplicationContext());

                alertDialog.setTitle("Fejl");
                alertDialog.setMessage("Ikke åbnet gennem Launcher.");
                alertDialog.show();
                outsideGIRAF = false;
            }
        }

    }

    /**
     * This is called when exitting the activity
     */
    @Override
    protected void onPause() {
        //AudioPlayer.close();
        super.onPause();
    }

    /**
     * This is called when upon returning to the activity or after onCreate
     */
    @Override
    protected void onResume() {
        //AudioPlayer.open();
        super.onResume();

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

        //super.onBackPressed();

        //getFragmentManager().popBackStack();
        //getFragmentManager().beginTransaction().add(new SpeechBoardFragment(this.getApplicationContext()), "1").commit();
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

    /**
     * @return an instance of Helper.
     */
    public static Helper getHelp() {
        return help;
    }
    /**
     *
     * @return instance of App with this apps data
     */
    public static Application getApp()
    {
        return app;
    }

    /**
     * @return The Intent received from the Launcher
     */
    public static Intent getGirafIntent() {
        return girafIntent;
    }


}