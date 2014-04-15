	package dk.aau.cs.giraf.parrot;
	
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.Category;
import dk.aau.cs.giraf.oasis.lib.models.Pictogram;


    /**
	 * 
	 * @author PARROT spring 2012 and adapted by sw605f13-PARROT
	 * This is the SentenceboardAdapter class. 
	 * It is used to import the pictograms into a gridview where Async is not used eg. the sentenceboard, 
	 * its a copy of the original PictogramAdapter.
	 */
public class SentenceboardAdapter extends BaseAdapter {


    private PictogramController pictogramController;
    private Pictogram pictogram;
	private ArrayList<Pictogram> pictogramList;
	private Context context;
	
	/**
	 * @param cat, a PARROTCategory
	 * @param c, the applications context
	 */
	public SentenceboardAdapter(ArrayList<Pictogram> cat, Context c)
	{
		super();
		this.pictogramList=cat;
		context = c;
        this.pictogramController = new PictogramController(c);
	}

		@Override
		public int getCount() {
			//return the number of pictograms


			return pictogramController.getPictograms().size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

        public void addItem(Pictogram p)
        {
            this.pictogramList.add(p);
        }

        public void replaceItem(int index, Pictogram p)
        {
            this.pictogramList.get(index).setId(p.getId());
            this.pictogramList.get(index).setPub(p.getPub());
            this.pictogramList.get(index).setName(p.getName());
            this.pictogramList.get(index).setInlineText(p.getInlineText());
            this.pictogramList.get(index).setImage(p.getImage());
            this.pictogramList.get(index).setEditableImage(p.getEditableImage());
            this.pictogramList.get(index).setAuthor(p.getAuthor());
            this.pictogramList.get(index).setSoundDataBytes(p.getSoundData());
        }

        public void emptyPictogramList()
        {
            this.pictogramList.clear();
        }
		
		/**
		 * create an image view for each pictogram in the pictogram list from the PARROTCategory.
		 */
		@Override
		public View getView(int id, View convertView, ViewGroup parent)
		{
			
			ImageView imageView = null;
			TextView textView = null;
            Pictogram pct = null;
			
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.pictogramview, null);

            try {
                pct = this.pictogramList.get(id);
            }
            catch (Exception e)
            {
                e.getStackTrace();
            }


            try {
                imageView = (ImageView) view.findViewById(R.id.pictogrambitmap);
                textView = (TextView) view.findViewById(R.id.pictogramtext);
            }
            catch (Exception e)
            {
                e.getStackTrace();
            }


			
			//setup layout for imageView
			LinearLayout.LayoutParams layoutParams;
			if(MainActivity.getUser().getPictogramSize()== PARROTProfile.PictogramSize.LARGE)
			{
				layoutParams = new LinearLayout.LayoutParams(180, 180);
			}
			else
			{
				layoutParams = new LinearLayout.LayoutParams(145, 145);	
			}


            try {
			    imageView.setLayoutParams(layoutParams);
            }
            catch (Exception e)
            {
                e.getStackTrace();
            }

			//load the Bitmap and set the setImageBitmap
			if(MainActivity.getUser().getShowText())
			{
				
				textView.setTextSize(20);	//TODO this value should be customizable
                try{
				textView.setText(pct.getInlineText());
                }
                catch (Exception e)
                {
                    e.getStackTrace();
                }
			}

			Bitmap bitmap = null;
            try{
			if(pct.getId() == -1)
			{
	        	bitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.usynlig);
				
			}
			else
			{
				bitmap = pct.getImageData();
			}
            }
            catch (Exception e)
            {
                e.getStackTrace();
            }
			imageView.setImageBitmap(bitmap);
			view.setPadding(8, 8, 8, 8);
			

			return view;
		}

}
