package dk.aau.cs.giraf.pictoreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import dk.aau.cs.giraf.pictogram.Pictogram;

/**
 * 
 * @author Lisbeth Nielsen, SW605f13 Parrot-group
 * This class is used to loading the bitmaps into memory and displaying them in the pictogramGrid when 
 * they are to be posted. This is happending off the UI Thread via AsyncTask.
 */
class LoadImage extends AsyncTask<Object, Void, Bitmap>{

		private final WeakReference<ImageView> imageView;
        private Context context;
        private final WeakReference<TextView> textView;
        private dk.aau.cs.giraf.oasis.lib.models.Pictogram pictogram;

        /**
         * 
         * @param imageView this is the ImageView in which the bitmap are to be shown.
         * @param textView this is the TextView in which the pictogram text are to be shown.
         * @param context this is the application context.
         */
        public LoadImage(ImageView imageView, TextView textView, Context context) {
        	//Log.v("LoadImage;Message","begin LoadImage");
        	 this.imageView = new WeakReference<ImageView>(imageView);
        	 this.textView = new WeakReference<TextView>(textView);
             this.context= context;
             //Log.v("LoadImage;Message","end LoadImage");
             
        }
	/**
	 * This method needs a pictogram as input, to make the image into a bitmap and creep it in memory
	 * * @param params The parameters of the task. In this case a pictogram
	 */
    @Override
    protected Bitmap doInBackground(Object... params) {
    	//Log.v("LoadImage;Message","begin doInBackground");
    	pictogram = (dk.aau.cs.giraf.oasis.lib.models.Pictogram) params[0];
        Bitmap bitmap = null;
        
        //decode the into bitmap that there is to be shown
        if(pictogram.getId() == -1)
		{
        	//Log.v("LoadImage;Message","doInBackground usynlig");
        	bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.usynlig);

        }
        else	
    	{
        	//Log.v("LoadImage;Message","doInBackground path" + pictogram.getImagePath());
        	bitmap = pictogram.getImageData();
    	}
        //Log.v("LoadImage;Message","end doInBackground");
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
    	//Log.v("LoadImage;Message","begin onPostExecute");
        if(result != null && imageView != null){
    	     final ImageView imageViewTemp = imageView.get();
    	     final TextView textViewTemp = textView.get();
             if (imageViewTemp != null) {
            	 //set the text in TextView
          		 if(MainActivity.getUser().getShowText() == true)//pct.getPictogramID() != -1 && MainActivity.getUser().getShowText()==true)
        		 {
        			textViewTemp.setTextSize(20);	//TODO this value should be customizable
        			textViewTemp.setText(pictogram.getInlineText());
        		 }
          		 //set the bitmap into the ImageView
                 imageViewTemp.setImageBitmap(result);
             }

        }
        //Log.v("LoadImage;Message","end onPostExecute");
    }

}
