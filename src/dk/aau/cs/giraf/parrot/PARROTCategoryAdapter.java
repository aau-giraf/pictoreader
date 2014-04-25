package dk.aau.cs.giraf.parrot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.oasis.lib.models.Application;
import dk.aau.cs.giraf.oasis.lib.models.Category;
import dk.aau.cs.giraf.pictogram.Pictogram;

/**
 * 
 * @author PARROT and edited by sw605f13-PARROT
 *This is the CategoryAdapter class. 
 * This class takes a list of categories and loads them into a GridView.
 */

public class PARROTCategoryAdapter extends BaseAdapter{

	private List<Category> categories;
	private Context context;
    private int ID;
    private Activity activity;
    PARROTProfile user;


	public PARROTCategoryAdapter(List<Category> categories, Activity activity, int ID, PARROTProfile user)
	{
		this.categories = categories;
		context = activity.getApplicationContext();
        this.ID = ID;
        this.activity = activity;
        this.user = user;
	}

	@Override
	public int getCount() {
		//return the number of categories
		return categories.size();
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
	 * Create an image view for each icon of the categories in the list.
	 */
	@Override
			public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			Bitmap pictogram = categories.get(position).getImage();
			if (convertView == null) {  // if it's not recycled, initialize some attributes
				imageView = new ImageView(context);
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
				imageView.setBackgroundColor(10);
			} 
			
			else {
				imageView = (ImageView) convertView;
			}
			
			//we then set the imageview to the icon of the category
			
			imageView.setImageBitmap(pictogram);
            imageView.setOnTouchListener(new pictogramTouchListener( position, ID, activity, user) );
			return imageView;
		}
	
}
