package dk.aau.cs.giraf.parrot;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.oasis.lib.controllers.CategoryController;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.Category;
import dk.aau.cs.giraf.pictogram.Pictogram;

/**
 * 
 * @author PARROT spring 2012 and adapted by sw605f13-PARROT
 * This is the Pictogram Adapter class. It is used to import the pictograms into a the GridView pictogramgrid.
 */
public class PictogramAdapter extends BaseAdapter {

	private Category cat;
	private Context context;
	private Activity activity;
    private PictogramController catController;
    PictogramController pictogramController;
    private List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> pics;
    private PARROTProfile user;
	

	public PictogramAdapter(List<dk.aau.cs.giraf.oasis.lib.models.Pictogram> pics, Context c, Activity act, PARROTProfile user)
	{
		super();
		this.pics=pics;
		context = c;
		activity= act;
        this.user = user;
        this.pictogramController = new PictogramController(context);
	}

	@Override
	public int getCount() {
		//return the number of pictograms
		return pics.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
        return pics.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return pics.get(arg0).getId();
	}
	/**
	 * create an image view for each pictogram in the pictogram list from the PARROTCategory.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		
		ImageView imageView;
		View view = convertView;
		TextView textView;
		//view.setTag(position);
        int sizeLarge = GComponent.DpToPixel(180, context);
        int sizeSmall = GComponent.DpToPixel(145, context);


        dk.aau.cs.giraf.oasis.lib.models.Pictogram pct = pics.get(position);

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.pictogramview, null);
		view.setOnDragListener(new SpeechBoardBoxDragListener(activity, context, user));
		view.setOnTouchListener(new pictogramTouchListener( position, R.id.pictogramgrid, activity, user) );

		//setup views
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
		LoadImage task = new LoadImage(imageView,textView, context);
	    task.execute(pct);
        view.setPadding(4, 4, 4, 4);

		return view;
	}
}