package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import dk.aau.cs.giraf.dblib.Helper;
import dk.aau.cs.giraf.dblib.controllers.ApplicationController;
import dk.aau.cs.giraf.dblib.controllers.CategoryController;
import dk.aau.cs.giraf.dblib.controllers.ProfileApplicationController;
import dk.aau.cs.giraf.dblib.controllers.ProfileController;
import dk.aau.cs.giraf.dblib.models.Application;
import dk.aau.cs.giraf.dblib.models.Category;
import dk.aau.cs.giraf.dblib.models.Profile;
import dk.aau.cs.giraf.dblib.models.ProfileApplication;
import dk.aau.cs.giraf.dblib.models.Setting;
import dk.aau.cs.giraf.pictogram.Pictogram;

import java.util.List;


/**
 * @author sw605f13-PARROT and PARROT spring 2013
 *         The PARROT DataLoader is used for interacting with the admin functionality of the GIRAF Project,
 *         which gets the data from the database.
 *         It is also interacting with the temporary categoryLib which handles categories, but this should
 *         be change when then category-table is put in to the database.
 */
public class ParrotDataLoader {

    private Activity parent;
    private Helper help;
    private Application app;
    private ProfileApplication proApp;

    private Helper oasisHelper = null;
    private Context context;

    /**
     * Constructor.
     *
     * @param activity An activity.
     */
    public ParrotDataLoader(Activity activity, boolean categories, Context context) {
        this.parent = activity;
        try {
            help = new Helper(parent);
        } catch (Exception e) {
            Log.v("Exception", e.getMessage());
        }
        ApplicationController applicationController = new ApplicationController(context);
        app = applicationController.getById(MainActivity.getApp().getId()); // Lasse

        this.context = context;


    }

    /**
     * This method loads a specific PARROTProfile, which are to be shown in PARROT
     *
     * @param childId child id
     * @param appId application id
     * @return PictoreaderProfile or null.
     */
    public PictoreaderProfile loadProfile(long childId, long appId) {
        Profile prof = null;
        List<Category> categories = null;
        CategoryController categoryController = new CategoryController(context);


        if (childId != -1 && appId >= 0) {
            //Get the childs profile and setup the PictoreaderProfile.
            ProfileController profileController = new ProfileController(context);
            prof = profileController.getProfileById(childId);
            Pictogram pic = new Pictogram(5, "name", 1, null, null, "inline", -1, parent.getApplicationContext());
            PictoreaderProfile parrotUser = new PictoreaderProfile(prof.getName(), pic);
            parrotUser.setProfileID(prof.getId());
            ApplicationController applicationController = new ApplicationController(context);


            ProfileApplicationController profileApplicationController = new ProfileApplicationController(context);

            Setting<String, String, String> specialSettings = new Setting<String, String, String>();

            try {
                Setting settings = profileApplicationController.getProfileApplicationByProfileIdAndApplicationId(
                    applicationController.getApplicationById(appId),
                    profileController.getProfileById(childId)).getSettings();

                specialSettings = settings;
            } catch (Exception e) {
                specialSettings.addValue("test", "test", "text");
            }
            //Load the settings
            parrotUser = loadSettings(parrotUser, specialSettings);

            categories = categoryController.getCategoriesByProfileId(prof.getId());

            //Get the child's categories. This return null if the child does not exist.
            categories = help.categoryHelper.getCategoriesByProfileId(prof.getId());

            if (categories != null) {
                for (Category c : categories) {
                    parrotUser.addCategory(c);
                }

                return parrotUser;
            }
        }
        /* No categories were found or childId == -1 && appId<0 then show error message*/
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
     * loads the specific settings from the database into a PictoreaderProfile,
     *
     * @param PARROTProfile parrotUser
     * @return PictoreaderProfile, an updated PictoreaderProfile of parrotUser.
     */
    private PictoreaderProfile loadSettings(PictoreaderProfile parrotUser,
                                            Setting<String, String, String> profileSettings)
    {

        try {
            //get the Setting from the profileSettings
            int noOfBoxes = Integer.valueOf(profileSettings.get("SentenceboardSettings").get("NoOfBoxes"));
            String pictogramSize = String.valueOf(profileSettings.get("PictogramSettings").get("PictogramSize"));
            int sentenceColour = Integer.valueOf(profileSettings.get("SentenceboardSettings").get("Color"));

            //load it into PictoreaderProfile
            parrotUser.setSentenceBoardColor(sentenceColour);
            parrotUser.setNumberOfSentencePictograms(noOfBoxes);
            if (pictogramSize.equalsIgnoreCase("MEDIUM")) {
                parrotUser.setPictogramSize(PictoreaderProfile.PictogramSize.MEDIUM);
            } else if (pictogramSize.equalsIgnoreCase("LARGE")) {
                parrotUser.setPictogramSize(PictoreaderProfile.PictogramSize.LARGE);
            }

            boolean showText = Boolean.valueOf(profileSettings.get("PictogramSettings").get("ShowText"));

            if (showText) {
                parrotUser.setShowText(true);
            } else {
                parrotUser.setShowText(false);
            }
        } catch (Exception e) {

        }

        return parrotUser;
    }

    /**
     * Saves the settings from a PictoreaderProfile into the database.
     *
     * @param user the child's PictoreaderProfile
     */
    public void saveChanges(PictoreaderProfile user) {

        Setting<String, String, String> profileSetting = new Setting<String, String, String>();

        profileSetting.remove("SentenceboardSettings");
        profileSetting.remove("PictogramSettings");

        //save profile settings
        profileSetting.addValue("SentenceboardSettings", "Color", String.valueOf(user.getSentenceBoardColor()));
        profileSetting.get("SentenceboardSettings").put("NoOfBoxes",
                            String.valueOf(user.getNumberOfSentencePictograms()));
        profileSetting.addValue("PictogramSettings", "PictogramSize", String.valueOf(user.getPictogramSize()));
        profileSetting.get("PictogramSettings").put("ShowText", String.valueOf(user.getShowText()));

        Profile prof = profileController.getProfileById(user.getProfileID());
        ProfileController profileController = new ProfileController(context);

        ProfileApplicationController profileApplicationController = new ProfileApplicationController(context);
        proApp = profileApplicationController.getProfileApplicationByProfileIdAndApplicationId(app, prof);
    }


}
