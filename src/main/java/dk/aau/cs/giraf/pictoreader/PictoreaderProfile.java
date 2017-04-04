package dk.aau.cs.giraf.pictoreader;

import android.graphics.Color;

import dk.aau.cs.giraf.dblib.models.Category;
import dk.aau.cs.giraf.dblib.models.Category;
import dk.aau.cs.giraf.pictogram.Pictogram;

import java.util.ArrayList;


/**
 * @author PARROT spring 2012 and edited by SW605f13-PARROT
 *         This is the PARROT Profile class.
 *         It handles the information associated with the current user, such as available pictograms
 *         organized in categories and settings, as well as personal information whenever it is needed.
 */
public class PictoreaderProfile {
    private String name;
    private Pictogram icon;
    private ArrayList<Category> categories = new ArrayList<Category>();
    private long profileId = -1;
    private int noOfboxesInSentenceboard = 25;
    private int sentenceBoardColor = Color.WHITE;
    private PictogramSize pictogramSize = PictogramSize.MEDIUM;
    private boolean showText = true;


    //TODO add all applicable settings here.
    public static enum PictogramSize {
        MEDIUM, LARGE
    }

    /**
     * Creates a PictoreaderProfile.
     *
     * @param name the name of the person.
     * @param icon a pictogram.
     */
    public PictoreaderProfile(String name, Pictogram icon) {
        this.setName(name);
        this.setIcon(icon);
    }

    /**
     * @return this PictoreaderProfile's categories.
     */
    public ArrayList<Category> getCategories() {
        return categories;
    }

    /**
     * @param index index to get the Category at that location.
     * @return a PARROTCategory.
     */
    public Category getCategoryAt(int index) {
        if (categories.size() == 0 && index == 0) {
            return null;
        } else {
            return categories.get(index);
        }
    }

    /**
     * @param index   index to where the category should be located.
     * @param cat the category to be saved.
     */
    public void setCategoryAt(int index, Category cat) {
        this.categories.set(index, cat);
    }

    /**
     * Add a new PARROTCategory to categories.
     *
     * @param cat the category to be added.
     */
    public void addCategory(Category cat) {
        categories.add(cat);
    }

    /**
     * remove the category at index index.
     *
     * @param index index to where the category should be located.
     */
    public void removeCategory(int index) {
        categories.remove(index);
    }

    /**
     * Returns the name of this pictoreaderProfile.
     * @return the name of this PictoreaderProfile.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this PictoreaderProfile.
     *
     * @param name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return PictoreaderProfile's icon, that should be a image of the person.
     */
    public Pictogram getIcon() {
        return icon;
    }

    /**
     * get icon from PictoreaderProfile
     *
     * @param icon a pictogram with a image of the person.
     */
    public void setIcon(Pictogram icon) {
        this.icon = icon;
    }

    /**
     * get ProfileID.
     *
     * @return profileId
     */
    public long getProfileId() {
        return profileId;
    }

    /**
     * set ProfileID.
     *
     * @param id profileId
     */
    public void setProfileId(long id) {
        this.profileId = id;
    }

    /**
     * get number of boxes in sentenceboard, which the child can handle.
     *
     * @return int, the number of boxes in sentenceboard
     */
    public int getNumberOfSentencePictograms() {
        return noOfboxesInSentenceboard;
    }

    /**
     * set number of boxes in sentenceboard, which the child can handle.
     *
     * @param numberOfSentencePictograms number of sentence pictograms.
     */
    public void setNumberOfSentencePictograms(int numberOfSentencePictograms) {
        noOfboxesInSentenceboard = numberOfSentencePictograms;
    }

    /**
     * Retunrs the color of the sentenceboard.
     * @return the color of the sentenceboard.
     */
    public int getSentenceBoardColor() {
        return sentenceBoardColor;
    }

    /**
     * set the color of the sentenceboard.
     *
     * @param sentenceBoardColor the color to be set.
     */
    public void setSentenceBoardColor(int sentenceBoardColor) {
        this.sentenceBoardColor = sentenceBoardColor;
    }

    /**
     * sets, whether the child needs a medium or large size of an image.
     *
     * @param size the size.
     */
    public void setPictogramSize(PictogramSize size) {
        this.pictogramSize = size;
    }

    /**
     * @return PictogramSize, whether the child needs a medium or large size of an image.
     */
    public PictogramSize getPictogramSize() {
        return pictogramSize;
    }

    /**
     * set whether or not the child can handle text.
     *
     * @param showText the boolean.
     */
    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    /**
     * Returns whether or not the citizen can handle text.
     * @return whether or not the child can handle text
     */
    public boolean getShowText() {
        return showText;
    }
}
