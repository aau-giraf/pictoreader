package dk.aau.cs.giraf.parrot;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;
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
        private final WeakReference<TextView>text;
        private Pictogram pictogram;

        /**
         * 
         * @param imv this is the ImageView in which the bitmap are to be shown. 
         * @param text this is the TextView in which the pictogram text are to be shown.
         * @param context this is the application context.
         */
        public LoadImage(ImageView imv, TextView text, Context context) {
        	//Log.v("LoadImage;Message","begin LoadImage");
        	 imageView = new WeakReference<ImageView>(imv);
        	 this.text = new WeakReference<TextView>(text);
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
    	pictogram = (Pictogram) params[0];
        Bitmap bitmap = null;
        
        //decode the into bitmap that there is to be shown
        if(pictogram.getPictogramID() == -1)
		{
        	//Log.v("LoadImage;Message","doInBackground usynlig");
        	bitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.usynlig);
			
		}
        else	
    	{
        	//Log.v("LoadImage;Message","doInBackground path" + pictogram.getImagePath());
        	bitmap = BitmapFactory.decodeFile(pictogram.getImagePath());
    	}
        //Log.v("LoadImage;Message","end doInBackground");
        return bitmap;
    }
    /**
     * This metode vil show the bitmap when it is posted on the screen.
     * @param Bitmap result. The result of the operation computed by doInBackground.
     */
    @Override
    protected void onPostExecute(Bitmap result) {
    	//Log.v("LoadImage;Message","begin onPostExecute");
        if(result != null && imageView != null){
    	     final ImageView imageView2 = imageView.get();
    	     final TextView textView = text.get();
             if (imageView2 != null) {
            	 //set the text in TextView
          		 if(PARROTActivity.getUser().getShowText()==true)//pct.getPictogramID() != -1 && PARROTActivity.getUser().getShowText()==true)
        		 {
        			textView.setTextSize(20);	//TODO this value should be customizable
        			textView.setText(pictogram.getTextLabel());
        		 }
          		 //set the bitmap into the ImageView
                 imageView2.setImageBitmap(result);
             }

        }
        //Log.v("LoadImage;Message","end onPostExecute");
    }

}
