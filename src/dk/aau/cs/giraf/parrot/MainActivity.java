package dk.aau.cs.giraf.parrot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GToast;
import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.controllers.ApplicationController;
import dk.aau.cs.giraf.oasis.lib.models.Application;

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
    private static ActionBar actionBar = null;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //GComponent.SetBaseColor(Color.rgb(255, 160, 0));

        Helper help = new Helper(this.getApplicationContext());

        //help.CreateDummyData();




        //These lines get the intent from the launcher //TODO use us when testing with the launcher.
        girafIntent = getIntent();
        guardianID = girafIntent.getIntExtra("currentGuardianID", -1);
        childID = girafIntent.getIntExtra("currentChildID", -1);

        ApplicationController applicationController = new ApplicationController(getApplicationContext());

        app = applicationController.getApplicationByPackageName();
        //app = new Application(1, "myapp", "1.0", null, "hah", "Main", "mysecr", 1);

		/*don't delete this is for lisbeth and anders when running on our own device*/
        guardianID = 1;
        childID=11;




        if(guardianID == -1 )
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("guardianID");
            alertDialog.setMessage("Could not find guardian.");
            alertDialog.show();
        }
        else
        {
            dataLoader = new PARROTDataLoader(this, true, this.getApplicationContext());

            if (dataLoader != null)
            {
                parrotUser = dataLoader.loadProfile((int)childID, app.getId());
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
                Fragment newFragment = new SpeechBoardFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.add(R.id.main, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
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

    /**
     * A menu is created upon creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parrot_settings, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
        getFragmentManager().beginTransaction().add(new SpeechBoardFragment(), "1").commit();
        }

    /**
     * this activating a new  Activity class which handles the settings which can be changed.
     */
    public void switchTabs(){
        Log.v("","switchTabs begin");
        if(actionBar!=null)
        {
            Log.v("","switchTabs in 1 if");
            int index = actionBar.getSelectedNavigationIndex();
            if(index == 0)
            {
                Log.v("","switchTabs in 2 if");
                actionBar.selectTab(actionBar.getTabAt(1));
            }
            else
            {
                Log.v("","switchTabs in else");
                actionBar.selectTab(actionBar.getTabAt(0));
            }
        }
        Log.v("","switchTabs end");
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