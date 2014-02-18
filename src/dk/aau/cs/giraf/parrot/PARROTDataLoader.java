package dk.aau.cs.giraf.parrot;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import dk.aau.cs.giraf.categorylib.CategoryHelper;
import dk.aau.cs.giraf.categorylib.PARROTCategory;
import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.models.App;
import dk.aau.cs.giraf.oasis.lib.models.Profile;
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
	private App app;
	private CategoryHelper categoryHelper= null;


	/**
	 * Constructor.
	 * @param activity An activity.
	 */
	public PARROTDataLoader(Activity activity, boolean categories)
	{
		this.parent = activity;
		help = new Helper(parent); 
		app = help.appsHelper.getAppById(PARROTActivity.getApp().getId()); 
		if(categories)
		{
			categoryHelper= new CategoryHelper(parent);
		}
		

	}


	/**
	 * TODO This is not used in PARROT, should it be deleted.
	 * gets all the children from guardian
	 * @param An profile id of a guardian.
	 * @return An ArrayList of all the children asociated with the guardian who is currently using the system.
	 */
	public ArrayList<PARROTProfile> getChildrenFromGuardian(long guardianID)
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
	public PARROTProfile loadProfile(long childId,long appId)	
	{
		Profile prof =null;
		List<PARROTCategory> categories = null;
		
		if(childId != -1 && appId >=0)
		 {
			//Get the childs profile and setup the PARROTProfile.	
			prof = help.profilesHelper.getProfileById(childId);	
			Pictogram pic = new Pictogram(parent.getApplicationContext(), "","", null,null, false, -1);
			PARROTProfile parrotUser = new PARROTProfile(prof.getFirstname(), pic);
			parrotUser.setProfileID(prof.getId());
			Setting<String, String, String> specialSettings = help.appsHelper.getSettingByIds(appId, childId);

			//Load the settings
			parrotUser = loadSettings(parrotUser, specialSettings);

			//Get the child's categories. This return null if the child does not exist.
			categories = categoryHelper.getChildsCategories(prof.getId());	
			
			if(categories!=null)
			{
				Log.v("MessageXML", "xmlChild does exist");
				for(PARROTCategory c : categories)
				{
					for(PARROTCategory sc : c.getSubCategories())
					{
						Log.v("something", "subcategory"+ sc.getCategoryName()+ "super: " +sc.getSuperCategory().getCategoryName());
					}
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
		
		Profile prof = help.profilesHelper.getProfileById(user.getProfileID());
		Setting<String, String, String> profileSetting = new Setting<String, String, String>();
		profileSetting = help.appsHelper.getSettingByIds(app.getId(), prof.getId());
		
		profileSetting.remove("SentenceboardSettings");
		profileSetting.remove("PictogramSettings");
		
		//save profile settings
		profileSetting.addValue("SentenceboardSettings", "Color", String.valueOf(user.getSentenceBoardColor()));
		profileSetting.get("SentenceboardSettings").put("NoOfBoxes", String.valueOf(user.getNumberOfSentencePictograms()));
		profileSetting.addValue("PictogramSettings","PictogramSize", String.valueOf(user.getPictogramSize()));
		profileSetting.get("PictogramSettings").put("ShowText", String.valueOf(user.getShowText()));
		
		app.setSettings(profileSetting);
		help.appsHelper.modifyAppByProfile(app, prof);
		
	}


}
