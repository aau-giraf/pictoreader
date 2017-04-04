package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridView;

import dk.aau.cs.giraf.dblib.controllers.BaseImageControllerHelper;
import dk.aau.cs.giraf.dblib.controllers.CategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramCategoryController;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;
import dk.aau.cs.giraf.dblib.models.Category;
import dk.aau.cs.giraf.dblib.models.Pictogram;

/**
 * @author PARROT spring 2012 and small adaption made by SW605f13-PARROT
 *         This is the BoxDragListener class.
 *         It handles drag and drop functionality with objects in the SpeechboardFragment.
 */

public class SpeechBoardBoxDragListener implements OnDragListener {
    private Activity parrent;
    public dk.aau.cs.giraf.dblib.models.Pictogram draggedPictogram = null;
    private PictoreaderProfile profile = MainActivity.getUser();
    int numberOfSentencePictograms = profile.getNumberOfSentencePictograms();
    boolean insideOfMe = false;
    private Context context;
    private PictogramController pictogramController;
    private PictogramCategoryController pictogramCategoryController;
    private CategoryController categoryController;

    private PictoreaderProfile user = null;

    /**
     *  The constructor.
     * @param active the activity.
     */
    public SpeechBoardBoxDragListener(Activity active, Context context, PictoreaderProfile user) {
        parrent = active;
        this.context = context;
        this.user = user;
        this.pictogramController = new PictogramController(this.context);
        this.pictogramCategoryController = new PictogramCategoryController(this.context);
        this.categoryController = new CategoryController(this.context);
    }

    /**
     * it handles drag and drop functionality with objects in the SpeechboardFragment-
     *
     * @param self The view.
     * @param event The event.
     */
    @Override
    public boolean onDrag(View self, DragEvent event) {
        //Do not allow dragging empty pictograms, show do nothing

        if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
            //When pictogram is dragged from sentenceboard
            if (self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) {
                draggedPictogram = SpeechBoardFragment.sentencePictogramList.get(
                    SpeechBoardFragment.draggedPictogramIndex);
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

                        speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.sentencePictogramList, parrent));
                    }
                }*/
            }
        } else if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
            insideOfMe = true;
        } else if (event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
            insideOfMe = false;

        } else if (event.getAction() == DragEvent.ACTION_DROP) {
            if (insideOfMe) {

                //We want to drop a view into the sentenceboard
                if (self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID != R.id.sentenceboard) {
                    GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
                    int coordX = (int) event.getX();
                    int coordY = (int) event.getY();
                    int index = speech.pointToPosition(coordX, coordY);
                    //If the pictorgram is dropped at an illegal index
                    if (index < 0 || SpeechBoardFragment.draggedPictogramIndex < 0) {    
                        //Do nothing
                        return false;
                        //TODO improve this situation.
                    } else {
                        if (SpeechBoardFragment.dragOwnerID == R.id.pictogramgrid) {
                            draggedPictogram = SpeechBoardFragment.speechboardPictograms.get(
                                SpeechBoardFragment.draggedPictogramIndex);
                        } else if (SpeechBoardFragment.dragOwnerID == R.id.category) {
                            Category cat = SpeechBoardFragment.displayedCategory;
                            draggedPictogram = new Pictogram();
                            draggedPictogram.setName(cat.getName());
                            draggedPictogram.setInlineText(cat.getName());
                            BaseImageControllerHelper helper = new BaseImageControllerHelper(context);
                            Bitmap bitmap = helper.getImage(cat);
                            helper.setImage(cat, bitmap);
                            draggedPictogram.setId(cat.getId() * -1);
                        } else {
                            return false; //TODO improve this situation
                        }


                        if (SpeechBoardFragment.sentencePictogramList.size() <= index) {
                            try {
                                //Adds the dragged pictogram if it is released in the end of the sentence.
                                SpeechBoardFragment.sentencePictogramList.add(draggedPictogram);
                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                        } else if (SpeechBoardFragment.sentencePictogramList.size() > index) {
                            try {
                                //Replaces a pictogram if the dragged pictogram is released untop.
                                SpeechBoardFragment.sentencePictogramList.set(index, draggedPictogram);
                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                        }

                        speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.sentencePictogramList, parrent));
                        speech.invalidate();
                    }
                } else if (self.getId() == R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) {
                    //2 We are rearanging the position of pictograms on the sentenceboard
                    GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
                    int coordX = (int) event.getX();
                    int coordY = (int) event.getY();
                    int index = speech.pointToPosition(coordX, coordY);
                    if (index < 0) { //if the pictogram is dropped at an illegal position
                        //do nothing, let the pictogram be removed
                        //TODO improve this

                    } else {
                        int pictogramIndex = SpeechBoardFragment.draggedPictogramIndex;

                        while (index != pictogramIndex) {
                            int newPictogramIndex;
                            if (pictogramIndex < index) {
                                newPictogramIndex = pictogramIndex + 1;
                            } else {
                                newPictogramIndex = pictogramIndex - 1;
                            }
                            SpeechBoardFragment.sentencePictogramList.set(pictogramIndex,
                                SpeechBoardFragment.sentencePictogramList.get(newPictogramIndex));
                            pictogramIndex = newPictogramIndex;
                        }
                        SpeechBoardFragment.sentencePictogramList.set(index, draggedPictogram);


                        speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.sentencePictogramList, parrent));
                        speech.invalidate();
                        draggedPictogram = null;
                    }

					/*while(SpeechBoardFragment.sentencePictogramList.size() > profile.getNumberOfSentencePictograms())
					{
                        categoryController.removePictogramCategory(-1, 
                        SpeechBoardFragment.sentencePictogramList.size()-1);
					}*/

                } else if (self.getId() != R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) {
                    //3 If we drag something from the sentenceboard to somewhere else
                    GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
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
                    SpeechBoardFragment.sentencePictogramList.set(SpeechBoardFragment.draggedPictogramIndex, null);
                    speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.sentencePictogramList, parrent));
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
                    while(i < SpeechBoardFragment.sentencePictogramList.size() && !placed)
                    {
                        if(SpeechBoardFragment.sentencePictogramList.get(i) == null)
                        {
                            placed = true;
                            draggedPictogram = SpeechBoardFragment.speechboardPictograms.get(
                            SpeechBoardFragment.draggedPictogramIndex);
                            SpeechBoardFragment.sentencePictogramList.set(i,draggedPictogram);
                            GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
                            speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.sentencePictogramList,
                             parrent));
                            speech.invalidate();
                        }
                        i++;
                    }
                }
            }*/

            SpeechBoardFragment.dragOwnerID = -1;
        } else if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
            insideOfMe = false;
            //If we drag something from the sentenceboard to somewhere else
            if (self.getId() != R.id.sentenceboard && SpeechBoardFragment.dragOwnerID == R.id.sentenceboard) {
                GridView speech = (GridView) parrent.findViewById(R.id.sentenceboard);
                if (SpeechBoardFragment.draggedPictogramIndex >= 0) {
                    SpeechBoardFragment.sentencePictogramList.set(SpeechBoardFragment.draggedPictogramIndex, null);
                    speech.setAdapter(new SentenceboardAdapter(SpeechBoardFragment.sentencePictogramList, parrent));
                    speech.invalidate();
                }
                SpeechBoardFragment.dragOwnerID = -1;
                SpeechBoardFragment.draggedPictogramIndex = -1;
            }


        }
        return true;
    }
}




 

