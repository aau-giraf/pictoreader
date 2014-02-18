package dk.aau.cs.giraf.parrot;

import java.util.ArrayList;

import android.graphics.Color;
import dk.aau.cs.giraf.categorylib.PARROTCategory;
import dk.aau.cs.giraf.pictogram.Pictogram;



/**
 * 
 * @author PARROT spring 2012 and edited by SW605f13-PARROT
 * This is the PARROT Profile class. It handles the information associated with the current user, such as available pictograms
 * organized in categories and settings, as well as personal information whenever it is needed.
 */
public class PARROTProfile {
	private String name;
	private Pictogram icon;
	private ArrayList<PARROTCategory> categories = new ArrayList<PARROTCategory>();
	private long profileID =-1;
	private int noOfboxesInSentenceboard = 1;
	private int sentenceBoardColor = Color.WHITE;
	private PictogramSize pictogramSize = PictogramSize.MEDIUM; 
	private boolean showText = true;
			
	//TODO add all applicable settings here.
	public static enum PictogramSize{MEDIUM, LARGE}
	
	/**
	 * Creates a PARROTProfile.
	 * @param name, the name of the person. 
	 * @param icon, a pictogram.
	 */
	public PARROTProfile(String name, Pictogram icon)
	{
		this.setName(name);
		this.setIcon(icon);
	}

	/**
	 * @return this PARROTProfile's categories.
	 */
	public ArrayList<PARROTCategory> getCategories() {
		return categories;
	}
	
	/**
	 * 
	 * @param i, index to get the Category at that location.
	 * @return a PARROTCategory.
	 */
	public PARROTCategory getCategoryAt(int i)
	{
		return categories.get(i);
	}
	
	/**
	 * 
	 * @param i, index to where the category should be located.
	 * @param cat, the category to be saved.
	 */
	public void setCategoryAt(int i, PARROTCategory cat)
	{
		this.categories.set(i, cat);
	}
	/**
	 * Add a new PARROTCategory to categories.
	 * @param cat, the category to be added.
	 */
	public void addCategory(PARROTCategory cat)
	{
		categories.add(cat);
	}
	
	/**
	 * remove the category at index i.
	 * @param i, index to where the category should be located.
	 */
	public void removeCategory(int i)
	{
		categories.remove(i);
	}
	
	/**
	 * 
	 * @return the name of this PARROTProfile.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of this PARROTProfile.
	 * @param name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return PARROTProfile's icon, that should be a image of the person. 
	 */
	public Pictogram getIcon() {
		return icon;
	}

	/**
	 * get icon from PARROTProfile
	 * @param icon, a pictogram with a image of the person.
	 */
	public void setIcon(Pictogram icon) {
		this.icon = icon;
	}

	/**
	 * get ProfileID
	 * @return profileID
	 */
	public long getProfileID() {
		return profileID;
	}
	
	/**
	 * set ProfileID
	 * @param l, profileID 
	 */
	public void setProfileID(long l) {
		this.profileID = l;
	}
	
	/**
	 * get number of boxes in sentenceboard, which the child can handle
	 * @return int, the number of boxes in sentenceboard
	 */
	public int getNumberOfSentencePictograms() {
		return noOfboxesInSentenceboard;
	}
	/**
	 * set number of boxes in sentenceboard, which the child can handle
	 * @param numberOfSentencePictograms
	 */
	public void setNumberOfSentencePictograms(int numberOfSentencePictograms) {
		noOfboxesInSentenceboard = numberOfSentencePictograms;
	}

	/**
	 * 
	 * @return the color of the sentenceboard
	 */
	public int getSentenceBoardColor() {
		return sentenceBoardColor;
	}
	
	/**
	 * set the color of the sentenceboard
	 * @param sentenceBoardColor
	 */
	public void setSentenceBoardColor(int sentenceBoardColor) {
		this.sentenceBoardColor = sentenceBoardColor;
	}
	
	/**
	 * sets, whether the child needs a medium or large size of an image
	 * @param size
	 */
	public void setPictogramSize(PictogramSize size){
		this.pictogramSize = size;
	}
	
	/**
	 * 
	 * @return PictogramSize, whether the child needs a medium or large size of an image
	 */
	public PictogramSize getPictogramSize()
	{
		return pictogramSize;
	}
	
	/**
	 * set whether or not the child can handle text
	 * @param showText
	 */
	public void setShowText(boolean showText){
		this.showText = showText;
	}
	
	/**
	 * 
	 * @return whether or not the child can handle text
	 */
	public boolean getShowText()
	{
		return showText;
	}
}
