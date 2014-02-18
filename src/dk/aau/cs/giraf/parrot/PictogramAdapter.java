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
import dk.aau.cs.giraf.categorylib.PARROTCategory;
import dk.aau.cs.giraf.pictogram.Pictogram;

/**
 * 
 * @author PARROT spring 2012 and adapted by sw605f13-PARROT
 * This is the Pictogram Adapter class. It is used to import the pictograms into a the GridView pictogramgrid.
 */
public class PictogramAdapter extends BaseAdapter {

	private PARROTCategory cat;
	private Context context;
	private Activity activity;
	
	/**
	 * 
	 * @param cat, a PARROTCategory
	 * @param c, the applications context
	 */
	public PictogramAdapter(PARROTCategory cat, Context c, Activity act)
	{
		super();
		this.cat=cat;
		context = c;
		activity= act;
	}

	@Override
	public int getCount() {
		//return the number of pictograms
		return cat.getPictograms().size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return cat.getPictogramAtIndex(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return cat.getPictogramAtIndex(arg0).getPictogramID();
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
		Pictogram pct=cat.getPictogramAtIndex(position);

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.pictogramview, null);
		view.setOnDragListener(new SpeechBoardBoxDragListener(activity));
		view.setOnTouchListener(new pictogramTouchListener( position) );

		//setup views
		imageView = (ImageView) view.findViewById(R.id.pictogrambitmap); 
		textView = (TextView) view.findViewById(R.id.pictogramtext);
		
		//setup layout for imageView
		LinearLayout.LayoutParams layoutParams;
		if(PARROTActivity.getUser().getPictogramSize()== PARROTProfile.PictogramSize.LARGE)
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
