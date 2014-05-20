package dk.aau.cs.giraf.parrot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GSelectableContent;
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
    private List<Boolean> marked;
	private Context context;
    private int ID;
    private Activity activity;
    PARROTProfile user;


	public PARROTCategoryAdapter(List<Category> categories, Activity activity, int ID, PARROTProfile user, int marked)
	{
		this.categories = categories;
        this.marked = new ArrayList<Boolean>();
        for (int i = 0; i < categories.size(); i++)
        {
            if(i == marked)
            {
                this.marked.add(true);
            } else
            {
                this.marked.add(false);
            }
        }
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

    public void setMarked(boolean marked, int position)
    {

    }

	/**
	 * Create an image view for each icon of the categories in the list.
	 */
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textView;
        Bitmap bitmap = categories.get(position).getImage();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.categoryview, null);
        ((GSelectableContent)view).SetSelected(marked.get(position));

       if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(GComponent.DpToPixel(150, this.context), GComponent.DpToPixel(150, this.context)));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundColor(10);

            textView = new TextView(context);
        }

        else {
            imageView = (ImageView) view.findViewById(R.id.categorybitmap);
            imageView.getLayoutParams().width = GComponent.DpToPixel(135, this.context);
            imageView.getLayoutParams().height = GComponent.DpToPixel(135, this.context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            textView = (TextView) view.findViewById(R.id.categorytext);
        }

        if(MainActivity.getUser().getShowText())
        {
            textView.setTextSize(15);	//TODO this value should be customizable
            try{
                textView.setText(categories.get(position).getName());
            }
            catch (Exception e)
            {
                e.getStackTrace();
            }
        }

        //we then set the imageview to the icon of the category
        imageView.setImageBitmap(bitmap);
        imageView.setMaxHeight(5);
        imageView.setMaxWidth(5);

        imageView.setOnTouchListener(new pictogramTouchListener(position, ID, activity, user));

        return view;
	}
}
