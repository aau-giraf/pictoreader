package dk.aau.cs.giraf.pictoreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.aau.cs.giraf.dblib.controllers.BaseImageControllerHelper;
import dk.aau.cs.giraf.dblib.controllers.PictogramController;
import dk.aau.cs.giraf.dblib.models.Pictogram;
import dk.aau.cs.giraf.utilities.GirafScalingUtilities;

import java.util.ArrayList;


/**
 * @author PARROT spring 2012 and adapted by sw605f13-PARROT
 *         This is the SentenceboardAdapter class.
 *         It is used to import the pictograms into a gridview where Async is not used eg. the sentenceboard,
 *         its a copy of the original PictogramAdapter.
 */
public class SentenceboardAdapter extends BaseAdapter {


    private PictogramController pictogramController;
    private Pictogram pictogram;
    private ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram> pictogramList;
    private Context context;

    /**
     * The constructer.
     * @param cat a PARROTCategory
     * @param context  the applications context
     */
    public SentenceboardAdapter(ArrayList<dk.aau.cs.giraf.dblib.models.Pictogram> cat, Context context) {
        super();
        this.pictogramList = cat;
        this.context = context;
        this.pictogramController = new PictogramController(context);
    }

    @Override
    public int getCount() {
        //return the number of pictograms

        return pictogramList.size();
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

    public void addItem(Pictogram pictogram) {
        this.pictogramList.add(pictogram);
    }

    public void replaceItem(int index, Pictogram pictogram) {
        this.pictogramList.set(index, pictogram);
    }

    public void emptyPictogramList() {
        this.pictogramList.clear();
    }

    /**
     * create an image view for each pictogram in the pictogram list from the PARROTCategory.
     */
    @Override
    public View getView(int id, View convertView, ViewGroup parent) {

        ImageView imageView = null;
        TextView textView = null;
        Pictogram pct = null;
        LinearLayout picView = null;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pictogramview, null);

        try {
            pct = this.pictogramList.get(id);
        } catch (Exception e) {
            e.getStackTrace();
        }


        try {
            imageView = (ImageView) view.findViewById(R.id.pictogrambitmap);
            textView = (TextView) view.findViewById(R.id.pictogramtext);
            picView = (LinearLayout) view.findViewById(R.id.pictogramView);
        } catch (Exception e) {
            e.getStackTrace();
        }

        //setup layout for imageView
        GridView sentenceBoard = (GridView) view.findViewById(R.id.sentenceboard);
        LinearLayout.LayoutParams imageLayoutParams;
        LinearLayout.LayoutParams picLayoutParams;

        //size adapted using dp to calculate pixels
        int size = (int) GirafScalingUtilities.convertDpToPixel(context, 100);

        imageLayoutParams = new LinearLayout.LayoutParams(size, size);


        view.setOnTouchListener(new SentenceboardTouchListener(id));

        try {
            imageView.setLayoutParams(imageLayoutParams);
        } catch (Exception e) {
            e.getStackTrace();
        }

        //load the Bitmap and set the setImageBitmap
        if (MainActivity.getUser().getShowText()) {

            textView.setTextSize(15);    //TODO this value should be customizable
            try {
                textView.setText(pct.getName());
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        Bitmap bitmap = null;
        try {
            if (pct.getId() == -1) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.usynlig);
            } else {
                BaseImageControllerHelper helper = new BaseImageControllerHelper(context);
                bitmap = helper.getImage(pct);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        view.setPadding(8, 8, 8, 8);

        return view;
    }

}
