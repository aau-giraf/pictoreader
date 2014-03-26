package dk.aau.cs.giraf.parrot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

import dk.aau.cs.giraf.oasis.lib.Helper;
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
		setContentView(R.layout.main);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		//These lines get the intent from the launcher //TODO use us when testing with the launcher.
		girafIntent = getIntent();
		guardianID = girafIntent.getIntExtra("currentGuardianID", -1);
		childID = girafIntent.getIntExtra("currentChildID", -1);
		Helper help = new Helper(this);
		//app = help.applicationHelper.getAppByPackageName();
        app = null;

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
			dataLoader = new PARROTDataLoader(this, true);

			parrotUser = dataLoader.loadProfile((int)childID, app.getId());

			if(parrotUser != null)
			{
                Log.v("No in sentence", ""+ parrotUser.getNumberOfSentencePictograms());
                Log.v("MessageParrot", "returned");
						
				/* Here all the Tabs in the system is initialized based on whether or not a user
				 * is allowed to use them. If not they will not be initialized.
				 * We wish not make users aware that there exists functionality that they are not
				 * entitled to.
				 * Remember: Make sure the order of the Taps is consistent with the order of their rights in the
				 * 			 Rights array.
				 */
                /*
				actionBar = getActionBar();
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				//Creating a new Tab, setting the text it is to show and construct and attach a Tab Listener to control it.
				Tab tab = actionBar.newTab() 
						.setTabListener(new TabListener<SpeechBoardFragment>(this,"speechboard",SpeechBoardFragment.class));
				actionBar.addTab(tab, 0);
				Tab tab2 = actionBar.newTab()
						.setTabListener(new TabListener<OptionFragment>(this,"options",OptionFragment.class));
				actionBar.addTab(tab2, 1);
				*/
				
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
		// TODO Auto-generated method stub
		super.onBackPressed();
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