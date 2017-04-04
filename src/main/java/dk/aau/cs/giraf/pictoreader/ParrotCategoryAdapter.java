package dk.aau.cs.giraf.pictoreader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import dk.aau.cs.giraf.dblib.controllers.BaseImageControllerHelper;
import dk.aau.cs.giraf.dblib.models.Category;
import dk.aau.cs.giraf.utilities.GirafScalingUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PARROT and edited by sw605f13-PARROT
 *         This is the CategoryAdapter class.
 *         This class takes a list of categories and loads them into a GridView.
 */

public class ParrotCategoryAdapter extends BaseAdapter {

    private List<Category> categories;
    /**
     * List of booleans.
     */
    public List<Boolean> marked;
    private Context context;
    private int id;
    private Activity activity;
    PictoreaderProfile user;

    /**
     * Class Parrot Category mangager.
     *
     * @param categories categories
     * @param activity   activity
     * @param id         id
     * @param user       user
     * @param marked     marked
     */
    public ParrotCategoryAdapter(List<Category> categories, Activity activity,
                                 int id, PictoreaderProfile user, int marked)
    {
        this.categories = categories;
        this.marked = new ArrayList<Boolean>();
        for (int i = 0; i < categories.size(); i++) {
            if (i == marked) {
                this.marked.add(true);
            } else {
                this.marked.add(false);
            }
        }
        context = activity.getApplicationContext();
        this.id = id;
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

    public void setMarked(boolean marked, int position) {

    }

    /**
     * Create an image view for each icon of the categories in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView textView;

        BaseImageControllerHelper helper = new BaseImageControllerHelper(context);
        Bitmap bitmap = helper.getImage(categories.get(position));


        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.categoryview, null);

        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(
                (int) GirafScalingUtilities.convertDpToPixel(this.context, 150),
                (int) GirafScalingUtilities.convertDpToPixel(this.context, 150)));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundColor(10);

            textView = new TextView(context);
        } else {
            imageView = (ImageView) view.findViewById(R.id.categorybitmap);
            imageView.getLayoutParams().width = (int) GirafScalingUtilities.convertDpToPixel(this.context, 150);
            imageView.getLayoutParams().height = (int) GirafScalingUtilities.convertDpToPixel(this.context, 150);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            textView = (TextView) view.findViewById(R.id.categorytext);
        }

        if (MainActivity.getUser().getShowText()) {
            textView.setTextSize(15);    //TODO this value should be customizable
            try {
                textView.setText(categories.get(position).getName());
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        //we then set the imageview to the icon of the category
        imageView.setImageBitmap(bitmap);
        imageView.setMaxHeight(5);
        imageView.setMaxWidth(5);

        imageView.setOnClickListener(new pictogramClickListener(position, id, activity, user)); //

        return view;
    }
}
