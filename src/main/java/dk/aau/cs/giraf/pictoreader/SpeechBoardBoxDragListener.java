 package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridView;

import dk.aau.cs.giraf.gui.GGridView;
import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.Category;
import dk.aau.cs.giraf.oasis.lib.models.Pictogram;
import dk.aau.cs.giraf.oasis.lib.models.PictogramCategory;

 /**
 * 
 * @author PARROT spring 2012 and small adaption made by SW605f13-PARROT
 * This is the BoxDragListener class.
 * It handles drag and drop functionality with objects in the SpeechboardFragment.
 */

public class SpeechBoardBoxDragListener implements OnDragListener 
{
	private Activity parrent;
	public dk.aau.cs.giraf.oasis.lib.models.Pictogram draggedPictogram = null;
	private PARROTProfile profile = MainActivity.getUser();
	int numberOfSentencePictograms = profile.getNumberOfSentencePictograms();
	boolean insideOfMe = false;
    private Context _context;
    private PictogramController pictogramController;
    private PictogramCategoryController pictogramCategoryController;
    private CategoryController categoryController;

    private PARROTProfile user = null;

	/**
	 * @param active
	 */
	public SpeechBoardBoxDragListener(Activity active, Context c, PARROTProfile user) {
		parrent = active;
        _context = c;
        this.user = user;
        this.pictogramController = new PictogramController(_context);
        this.pictogramCategoryController = new PictogramCategoryController(_context);
        this.categoryController = new CategoryController(_context);
	}

	/**
	 * it handles drag and drop functionality with objects in the SpeechboardFragment
	 * @param self
	 * @param event
	 */
	@Override
	public boolean onDrag(View self, DragEvent event) {
        //Do not allow dragging empty pictograms, show do nothing

		if (event.getAction() == DragEvent.ACTION_DRAG_STARTED){
			//When pictogram is dragged from sentenceboard
			if(self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard)
			{
                draggedPictogram = SpeechBoardFragment.pictogramList.get(SpeechBoardFragment.draggedPictogramIndex);
				//Do not allow dragging empty pictograms, show do nothing
				/*if(draggedPictogram != null)
                {
                    if(draggedPictogram.getId() !=-1)
                    {
                        GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);

                        //remove pictogram from sentenceboard and add an empty pictogram

                        categoryController.removePictogramCategory(-1, SpeechBoardFragment.draggedPictogramIndex);


                        dk.aau.cs.giraf.oasis.lib.models.Pictogram p = new dk.aau.cs.giraf.oasis.lib.models.Pictogram();

                        p.setId(1);
                        p.setAuthor(-1);
                        p.setInlineText("#emptyPictogram#");
                        p.setName("#emptyPictogram");
                        p.setPub(-1);


                        PictogramCategory newCat = new PictogramCategory(p.getId(), -1);
                        categoryController.insertPictogramCategory(newCat);

                        speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.pictogramList, parrent));
                    }
                }*/
			}
		} else if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED){
			insideOfMe = true;
		} else if (event.getAction() == DragEvent.ACTION_DRAG_EXITED){
			insideOfMe = false;

		} else if (event.getAction() == DragEvent.ACTION_DROP){
			if (insideOfMe){

				//We want to drop a view into the sentenceboard
	//1
				if( self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID != R.id.sentenceboard)	
				{
					GGridView speech = (GGridView) parrent.findViewById(R.id.sentenceboard);
					int x = (int)event.getX();
					int y = (int)event.getY();
					int index = speech.pointToPosition(x, y);
					if(index <0 || SpeechBoardFragment.draggedPictogramIndex < 0)	//If the pictorgram is dropped at an illegal index
					{
						//Do nothing
						return false;
						//TODO improve this situation.
					}
					else
					{
                        if(SpeechBoardFragment.dragOwnerID == R.id.pictogramgrid)
                        {
                            draggedPictogram = SpeechBoardFragment.speechboardPictograms.get(SpeechBoardFragment.draggedPictogramIndex);
                        }
                        else if(SpeechBoardFragment.dragOwnerID == R.id.supercategory )
                        {
                            Category cat = SpeechBoardFragment.displayedCategory;
                            draggedPictogram = new Pictogram();
                            draggedPictogram.setName(cat.getName());
                            draggedPictogram.setInlineText(cat.getName());
                            draggedPictogram.setImage(cat.getImage());
                            draggedPictogram.setId(cat.getId()*-1);
                        }
                        else
                        {
                            return false; //TODO improve this situation
                        }


                        if(SpeechBoardFragment.pictogramList.size() <= index)
                        {
                            try
                            {
                                //Adds the dragged pictogram if it is released in the end of the sentence.
                                SpeechBoardFragment.pictogramList.add(draggedPictogram);
                            }
                            catch (Exception e)
                            {
                                e.getStackTrace();
                            }
                        }
                        else if (SpeechBoardFragment.pictogramList.size() > index)
                        {
                            try
                            {
                                //Replaces a pictogram if the dragged pictogram is released untop.
                                SpeechBoardFragment.pictogramList.set(index, draggedPictogram);
                            }
                            catch (Exception e)
                            {
                                e.getStackTrace();
                            }
                        }

						speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.pictogramList, parrent));
						speech.invalidate();
					}
				}
	//2			
				else if(self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) //We are rearanging the position of pictograms on the sentenceboard
				{

					GGridView speech = (GGridView) parrent.findViewById(R.id.sentenceboard);
					int x = (int)event.getX();
					int y = (int)event.getY();
					int index = speech.pointToPosition(x, y);
					if(index <0)//if the pictogram is dropped at an illegal position
					{
						//do nothing, let the pictogram be removed
						//TODO improve this

					}
					else
					{
                        int i = SpeechBoardFragment.draggedPictogramIndex;

                        while (index != i)
                        {
                            int j;
                            if(i < index)
                            {
                                j = i +1;
                            }
                            else
                            {
                                j = i -1;
                            }
                            SpeechBoardFragment.pictogramList.set(i, SpeechBoardFragment.pictogramList.get(j));
                            i = j;
                        }
                        SpeechBoardFragment.pictogramList.set(index, draggedPictogram);


						speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.pictogramList, parrent));
						speech.invalidate();
						draggedPictogram = null;
					}

					/*while(SpeechBoardFragment.pictogramList.size() > profile.getNumberOfSentencePictograms())
					{
                        categoryController.removePictogramCategory(-1, SpeechBoardFragment.pictogramList.size()-1);
					}*/

				}
		//3
				else if(self.getId() != R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) //If we drag something from the sentenceboard to somewhere else
				{
                    GGridView speech = (GGridView) parrent.findViewById(R.id.sentenceboard);
                    /*int x = (int)event.getX();
                    int y = (int)event.getY();
                    int index = speech.pointToPosition(x, y);
                    if(index <0)//if the pictogram is dropped at an illegal position
                    {
                        //do nothing, let the pictogram be removed
                        //TODO improve this

                    }
                    else
                    {*/
                    SpeechBoardFragment.pictogramList.set(SpeechBoardFragment.draggedPictogramIndex, null);
					speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.pictogramList, parrent));
					speech.invalidate();
                    //}
				}

			}

            /*
            GGridView pictogramGrid = (GGridView) parrent.findViewById(R.id.pictogramgrid);

            int x = (int)event.getX();
            int y = (int)event.getY();

            int index = pictogramGrid.pointToPosition(x, y);
            if(index >= 0)
            {
                if(index == SpeechBoardFragment.draggedPictogramIndex)
                {
                    int i = 0;
                    boolean placed = false;
                    while(i < SpeechBoardFragment.pictogramList.size() && !placed)
                    {
                        if(SpeechBoardFragment.pictogramList.get(i) == null)
                        {
                            placed = true;
                            draggedPictogram = SpeechBoardFragment.speechboardPictograms.get(SpeechBoardFragment.draggedPictogramIndex);
                            SpeechBoardFragment.pictogramList.set(i,draggedPictogram);
                            GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
                            speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.pictogramList, parrent));
                            speech.invalidate();
                        }
                        i++;
                    }
                }
            }*/

            SpeechBoardFragment.dragOwnerID = -1;
		} else if (event.getAction() == DragEvent.ACTION_DRAG_ENDED){
			insideOfMe = false;
            if(self.getId() != R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) //If we drag something from the sentenceboard to somewhere else
            {
                GGridView speech = (GGridView) parrent.findViewById(R.id.sentenceboard);
                if(SpeechBoardFragment.draggedPictogramIndex >= 0)
                {
                    SpeechBoardFragment.pictogramList.set(SpeechBoardFragment.draggedPictogramIndex, null);
                    speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.pictogramList, parrent));
                    speech.invalidate();
                }
                SpeechBoardFragment.dragOwnerID = -1;
                SpeechBoardFragment.draggedPictogramIndex = -1;
            }


		}
		return true;
	}
}




 

