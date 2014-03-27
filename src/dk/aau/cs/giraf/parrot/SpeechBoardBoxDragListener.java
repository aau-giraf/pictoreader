 package dk.aau.cs.giraf.parrot;

import android.app.Activity;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridView;
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
	private Pictogram draggedPictogram = null;
	private PARROTProfile profile = MainActivity.getUser();
	int numberOfSentencePictograms = profile.getNumberOfSentencePictograms();
	boolean insideOfMe = false;

	/**
	 * @param active
	 */
	public SpeechBoardBoxDragListener(Activity active) {
		parrent = active;
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
				draggedPictogram = SpeechBoardFragment.speechBoardCategory.getPictogramAtIndex(SpeechBoardFragment.draggedPictogramIndex);
				//Do not allow dragging empty pictograms, show do nothing
				if(draggedPictogram.getPictogramID()!=-1)
				{
					GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
					
					//remove pictogram from sentenceboard and add an empty pictogram 
					SpeechBoardFragment.speechBoardCategory.removePictogram(SpeechBoardFragment.draggedPictogramIndex);	
					SpeechBoardFragment.speechBoardCategory.addPictogram(new Pictogram(1,"#emptyPictogram#", -1, null, null, "#emptyPictogram#", -1, parrent.getApplicationContext()));
					
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
						draggedPictogram = SpeechBoardFragment.displayedCategory.getPictogramAtIndex(SpeechBoardFragment.draggedPictogramIndex);


						//Replaces a pictogram already in the sentencebord
						if(SpeechBoardFragment.speechBoardCategory.getPictogramAtIndex(index).getPictogramID() != -1) 
						{
							SpeechBoardFragment.speechBoardCategory.removePictogram(index); //Removes the pictogram at the specific index
							SpeechBoardFragment.speechBoardCategory.addPictogramAtIndex(draggedPictogram, index); //add the pictogram at the specific position
						}
						//place the dragged pictogram into an empty filled
						else 
						{
							int count = 0;
							//place the new pictogram in the first empty filled
							while (count < numberOfSentencePictograms) 
							{
								if (SpeechBoardFragment.speechBoardCategory.getPictogramAtIndex(count).getPictogramID() == -1) 
								{
									SpeechBoardFragment.speechBoardCategory.removePictogram(count); //Removes the pictogram at the specific index
									SpeechBoardFragment.speechBoardCategory.addPictogramAtIndex(draggedPictogram, count); //add the pictogram at the specific position
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
						if(SpeechBoardFragment.speechBoardCategory.getPictogramAtIndex(index).getPictogramID() == -1)
						{
							//if it is empty, there might be empty spaces to the left of it too
							int count = 0;
							while (count < numberOfSentencePictograms) 
							{

								if (SpeechBoardFragment.speechBoardCategory.getPictogramAtIndex(count).getPictogramID() == -1) 
								{
									SpeechBoardFragment.speechBoardCategory.removePictogram(count); //Removes the pictogram at the specific index
									SpeechBoardFragment.speechBoardCategory.addPictogramAtIndex(draggedPictogram, count); //add the pictogram at the specific position
									break;
								} 
								count++;
							}
						}
						else
						{
							SpeechBoardFragment.speechBoardCategory.addPictogramAtIndex(draggedPictogram, index);
						}


						speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.speechBoardCategory, parrent));
						speech.invalidate();
						draggedPictogram = null;
					}

					while(SpeechBoardFragment.speechBoardCategory.getPictograms().size() > profile.getNumberOfSentencePictograms())
					{
						SpeechBoardFragment.speechBoardCategory.removePictogram(SpeechBoardFragment.speechBoardCategory.getPictograms().size()-1);
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



 

