 package dk.aau.cs.giraf.parrot;

import android.app.Activity;
import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridView;

import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.PictogramCategory;
import dk.aau.cs.giraf.pictogram.Pictogram;

/**
 * 
 * @author PARROT spring 2012 and small adaption made by SW605f13-PARROT
 * This is the BoxDragListener class.
 * It handles drag and drop functionality with objects in the SpeechboardFragment.
 */

public class SpeechBoardBoxDragListener implements OnDragListener 
{
	private Activity parrent;
	private dk.aau.cs.giraf.oasis.lib.models.Pictogram draggedPictogram = null;
	private PARROTProfile profile = MainActivity.getUser();
	int numberOfSentencePictograms = profile.getNumberOfSentencePictograms();
	boolean insideOfMe = false;
    private Context _context;
    private PictogramController pictogramController = new PictogramController(_context);
    private PictogramCategoryController categoryController = new PictogramCategoryController(_context);

	/**
	 * @param active
	 */
	public SpeechBoardBoxDragListener(Activity active, Context c) {
		parrent = active;
        _context = c;
	}

	/**
	 * it handles drag and drop functionality with objects in the SpeechboardFragment
	 * @param self
	 * @param event
	 */
	@Override
	public boolean onDrag(View self, DragEvent event) {
		if (event.getAction() == DragEvent.ACTION_DRAG_STARTED){
			//When pictogram is dragged from sentenceboard
			if(self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard)
			{
				draggedPictogram = pictogramController.getPictogramsByCategory(SpeechBoardFragment.speechBoardCategory).get(SpeechBoardFragment.draggedPictogramIndex);
				//Do not allow dragging empty pictograms, show do nothing
				if(draggedPictogram.getId() !=-1)
				{
					GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
					
					//remove pictogram from sentenceboard and add an empty pictogram

                    categoryController.removePictogramCategory(SpeechBoardFragment.speechBoardCategory.getId(), SpeechBoardFragment.draggedPictogramIndex);


                    dk.aau.cs.giraf.oasis.lib.models.Pictogram p = new dk.aau.cs.giraf.oasis.lib.models.Pictogram();

                    p.setId(1);
                    p.setAuthor(-1);
                    p.setInlineText("#emptyPictogram#");
                    p.setName("#emptyPictogram");
                    p.setPub(-1);


                    PictogramCategory newCat = new PictogramCategory(p.getId(), SpeechBoardFragment.speechBoardCategory.getId());
                    categoryController.insertPictogramCategory(newCat);
					
					speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.speechBoardCategory, parrent));
				}
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
					GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
					int x = (int)event.getX();
					int y = (int)event.getY();
					int index = speech.pointToPosition(x, y);
					if(index <0)	//If the pictorgram is dropped at an illegal index
					{
						//Do nothing
						return false;
						//TODO improve this situation.
					}
					else
					{
                        draggedPictogram = pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory).get(SpeechBoardFragment.draggedPictogramIndex);

						//Replaces a pictogram already in the sentencebord

						if(pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory).get(index).getId() != -1)
						{

                            categoryController.removePictogramCategory(SpeechBoardFragment.displayedCategory.getId(), index); //Removes the pictogram at the specific index
                            categoryController.insertPictogramCategory(new PictogramCategory(SpeechBoardFragment.displayedCategory.getId(), SpeechBoardFragment.speechBoardCategory.getId())); //add the pictogram at the specific position-
						}
						//place the dragged pictogram into an empty filled
						else 
						{
							int count = 0;
							//place the new pictogram in the first empty filled
							while (count < numberOfSentencePictograms) 
							{
								if (pictogramController.getPictogramsByCategory(SpeechBoardFragment.displayedCategory).get(count).getId() == -1)
								{
                                    categoryController.removePictogramCategory(SpeechBoardFragment.speechBoardCategory.getId(), count); //Removes the pictogram at the specific index
									categoryController.insertPictogramCategory(new PictogramCategory(draggedPictogram.getId(), SpeechBoardFragment.speechBoardCategory.getId()));  //add the pictogram at the specific position
									break;
								} 
								count++;
							}
						}


						speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.speechBoardCategory, parrent));
						speech.invalidate();
					}
				}
	//2			
				else if(self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) //We are rearanging the position of pictograms on the sentenceboard
				{

					GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
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
						if(pictogramController.getPictogramsByCategory(SpeechBoardFragment.speechBoardCategory).get(index).getId() == -1)
						{
							//if it is empty, there might be empty spaces to the left of it too
							int count = 0;
							while (count < numberOfSentencePictograms) 
							{

								if (pictogramController.getPictogramsByCategory(SpeechBoardFragment.speechBoardCategory).get(count).getId() == -1)
								{
                                    categoryController.removePictogramCategory(SpeechBoardFragment.speechBoardCategory.getId(), count); //Removes the pictogram at the specific index
                                    categoryController.insertPictogramCategory(new PictogramCategory(draggedPictogram.getId(), SpeechBoardFragment.speechBoardCategory.getId()));  //add the pictogram at the specific position
                                    break;
								} 
								count++;
							}
						}
						else
						{
                            categoryController.insertPictogramCategory(new PictogramCategory(draggedPictogram.getId(), SpeechBoardFragment.speechBoardCategory.getId() ));
						}


						speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.speechBoardCategory, parrent));
						speech.invalidate();
						draggedPictogram = null;
					}

					while(pictogramController.getPictogramsByCategory(SpeechBoardFragment.speechBoardCategory).size() > profile.getNumberOfSentencePictograms())
					{
                        categoryController.removePictogramCategory(SpeechBoardFragment.speechBoardCategory.getId(), pictogramController.getPictogramsByCategory(SpeechBoardFragment.speechBoardCategory).size()-1);
					}

				}
		//3
				else if(self.getId() != R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) //If we drag something from the sentenceboard to somewhere else
				{

					GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
					speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.speechBoardCategory, parrent));
					speech.invalidate();
				}

			}
			
		} else if (event.getAction() == DragEvent.ACTION_DRAG_ENDED){
			insideOfMe = false;
			//To ensure that no wrong references will be made, the index is reset to -1
			SpeechBoardFragment.draggedPictogramIndex = -1;
			SpeechBoardFragment.dragOwnerID = -1;
			draggedPictogram = null;
			/*try {
				Thread.currentThread().sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//Dummy				
		}
		return true;
	}
}



 

