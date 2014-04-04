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
	private Category category;
	private Context context;
	
	/**
	 * @param cat, a PARROTCategory
	 * @param c, the applications context
	 */
	public SentenceboardAdapter(Category cat, Context c)
	{
		super();
		this.category=cat;
		context = c;
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
		
		/**
		 * create an image view for each pictogram in the pictogram list from the PARROTCategory.
		 */
		@Override
		public View getView(int id, View convertView, ViewGroup parent)
		{
			
			ImageView imageView;
			TextView textView;
			
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.pictogramview, null);

			Pictogram pct = pictogramController.getPictogramById(id);
			
			imageView = (ImageView) view.findViewById(R.id.pictogrambitmap); 
			textView = (TextView) view.findViewById(R.id.pictogramtext);

			
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
			
			imageView.setLayoutParams(layoutParams);

			//load the Bitmap and set the setImageBitmap
			if(MainActivity.getUser().getShowText()==true)
			{
				
				textView.setTextSize(20);	//TODO this value should be customizable
				textView.setText(pct.getInlineText());
				
			}
			Bitmap bitmap;
			if(pct.getId()
                    == -1)
			{
	        	bitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.usynlig);
				
			}
			else
			{
				bitmap = pct.getImageData();
			}
			imageView.setImageBitmap(bitmap);
			view.setPadding(8, 8, 8, 8);
			

			return view;
		}

}
