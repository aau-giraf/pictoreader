package dk.aau.cs.giraf.parrot;


import java.net.ContentHandler;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import dk.aau.cs.giraf.categorylib.CatLibHelper;
import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.controllers.ApplicationController;
import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.ProfileApplicationController;
import dk.aau.cs.giraf.oasis.lib.controllers.ProfileController;
import dk.aau.cs.giraf.oasis.lib.models.Application;
import dk.aau.cs.giraf.oasis.lib.models.Category;
import dk.aau.cs.giraf.oasis.lib.models.Profile;
import dk.aau.cs.giraf.oasis.lib.models.ProfileApplication;
import dk.aau.cs.giraf.oasis.lib.models.Setting;
import dk.aau.cs.giraf.pictogram.Pictogram;


/**
 * 
 * @author sw605f13-PARROT and PARROT spring 2013
 * The PARROT DataLoader is used for interacting with the admin functionality of the GIRAF Project,
 *  which gets the data from the database.
 *  
 * It is also interacting with the temporary categoryLib which handles categories, but this should 
 * be change when then category-table is put in to the database.  
 */
public class PARROTDataLoader {

	private Activity parent;
	private Helper help;
	private Application app;
    private ProfileApplication proApp;
	private CatLibHelper categoryHelper= null;
    private Helper oasisHelper = null;
    private Context _context;

	/**
	 * Constructor.
	 * @param activity An activity.
	 */
	public PARROTDataLoader(Activity activity, boolean categories, Context context)
	{
		this.parent = activity;
        try
        {
		    help = new Helper(parent);
        }
        catch(Exception e)
        {
            Log.v("Exception", e.getMessage());
        }
        ApplicationController applicationController = new ApplicationController(context);
        app = applicationController.getApplicationById(MainActivity.getApp().getId());

        _context = context;
		if(categories)
		{
			categoryHelper= new CatLibHelper(parent);
		}

	}


	/**
	 * TODO This is not used in PARROT, should it be deleted.
	 * gets all the children from guardian
	 * @param An profile id of a guardian.
	 * @return An ArrayList of all the children asociated with the guardian who is currently using the system.
	 */
	public ArrayList<PARROTProfile> getChildrenFromGuardian(int guardianID)
	{
		ArrayList<PARROTProfile> parrotChildren = new ArrayList<PARROTProfile>();
		Profile guardian = help.profilesHelper.getProfileById(guardianID);
		List<Profile> children = help.profilesHelper.getChildrenByGuardian(guardian);
		
		for(int i = 0;i<children.size();i++)
		{
			parrotChildren.add(loadProfile(children.get(i).getId(), app.getId()));
		}
		return parrotChildren;
	}


	/**
	 * This method loads a specific PARROTProfile, which are to be shown in PARROT
	 * 
	 * @param childId, The ID of the child using the app.
	 * @param appId,  The ID of the app.
	 * @return PARROTProfile or null.
	 */
	public PARROTProfile loadProfile(int childId,int appId)
	{
		Profile prof =null;
		List<Category> categories = null;
        CategoryController categoryController = new CategoryController(_context);

		
		if(childId != -1 && appId >=0)
		 {
			//Get the childs profile and setup the PARROTProfile.
            ProfileController profileController = new ProfileController(_context);
			prof = profileController.getProfileById(childId);
			Pictogram pic = new Pictogram(5, "name",1, null,null, "inline", -1, parent.getApplicationContext());
			PARROTProfile parrotUser = new PARROTProfile(prof.getName(), pic);
			parrotUser.setProfileID(prof.getId());
            ApplicationController applicationController = new ApplicationController(_context);


            ProfileApplicationController profileApplicationController = new ProfileApplicationController(_context);

             Setting<String, String, String> specialSettings = new Setting<String, String, String>();

             try
            {
                Setting settings = profileApplicationController.getProfileApplicationByProfileIdAndApplicationId(applicationController.getApplicationById(appId), profileController.getProfileById(childId)).getSettings();

                specialSettings = settings;
            }
            catch (Exception e)
            {
                specialSettings.addValue("test", "test", "text");
            }
			//Load the settings
			parrotUser = loadSettings(parrotUser, specialSettings);

            categories =  categoryController.getCategoriesByProfileId(prof.getId());



			//Get the child's categories. This return null if the child does not exist.
			categories = categoryHelper.getCategoriesFromProfile(prof);



			if(categories!=null)
			{
				for(Category c : categories)
				{
					parrotUser.addCategory(c);
				}
				
				return parrotUser;
			}
		}
		/* No categories were found or childId == -1 && appId<0 
		  then show error message*/
			// 1. Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder = new AlertDialog.Builder(parent);
	
			// 2. Chain together various setter methods to set the dialog characteristics
			builder.setMessage(R.string.dialog_message)
	       .setTitle(R.string.dialog_title)
	       .setNegativeButton(R.string.returnItem, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                   // User clicked OK, so save the mSelectedItems results somewhere
                   // or return them to the component that opened the dialog
            	   parent.finish();
               }
	       });
			// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				dialog.show();
	
		
		return null;
	}
	
	/**
	 * loads the specific settings from the database into a PARROTProfile,  
	 * @param PARROTProfile parrotUser
	 * @param Setting<String, String, String> profileSettings
	 * @return PARROTProfile, an updated PARROTProfile of parrotUser.
	 */
	private PARROTProfile loadSettings(PARROTProfile parrotUser, Setting<String, String, String> profileSettings) {

		try{
			//get the Setting from the profileSettings
			int noOfBoxes = Integer.valueOf(profileSettings.get("SentenceboardSettings").get("NoOfBoxes"));
			boolean showText = Boolean.valueOf(profileSettings.get("PictogramSettings").get("ShowText"));
			String PictogramSize = String.valueOf(profileSettings.get("PictogramSettings").get("PictogramSize"));
			int sentenceColour = Integer.valueOf(profileSettings.get("SentenceboardSettings").get("Color"));	
			
			//load it into PARROTProfile
			parrotUser.setSentenceBoardColor(sentenceColour);
			parrotUser.setNumberOfSentencePictograms(noOfBoxes);
			if(PictogramSize.equalsIgnoreCase("MEDIUM"))
			{ 
				parrotUser.setPictogramSize(PARROTProfile.PictogramSize.MEDIUM);
			}
			else if(PictogramSize.equalsIgnoreCase("LARGE"))
			{
				parrotUser.setPictogramSize(PARROTProfile.PictogramSize.LARGE);
			}
			
			if(showText)
			{
				parrotUser.setShowText(true);
			}
			else
			{
				parrotUser.setShowText(false);
			}
		}
		catch(Exception e)
		{
			
		}
		
		return parrotUser;
	}
	/**
	 * Saves the settings from a PARROTProfile into the database
	 * @param user, the child's PARROTProfile
	 */
	public void saveChanges(PARROTProfile user)
	{
		ProfileController profileController = new ProfileController(_context);
        Profile prof = profileController.getProfileById(user.getProfileID());
		Setting<String, String, String> profileSetting = new Setting<String, String, String>();


//		profileSetting = help.profileApplicationHelper.getProfileApplicationByProfileIdAndApplicationId(app, prof).getSettings();

		profileSetting.remove("SentenceboardSettings");
		profileSetting.remove("PictogramSettings");
		
		//save profile settings
		profileSetting.addValue("SentenceboardSettings", "Color", String.valueOf(user.getSentenceBoardColor()));
		profileSetting.get("SentenceboardSettings").put("NoOfBoxes", String.valueOf(user.getNumberOfSentencePictograms()));
		profileSetting.addValue("PictogramSettings","PictogramSize", String.valueOf(user.getPictogramSize()));
		profileSetting.get("PictogramSettings").put("ShowText", String.valueOf(user.getShowText()));

        ProfileApplicationController profileApplicationController = new ProfileApplicationController(_context);
        proApp = profileApplicationController.getProfileApplicationByProfileIdAndApplicationId(app, prof);
	}


}
