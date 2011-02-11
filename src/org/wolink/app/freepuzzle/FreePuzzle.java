package org.wolink.app.freepuzzle;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FreePuzzle extends Activity {
	
	private View genRootView(final Uri uri)
	{
        DisplayMetrics dm = new DisplayMetrics();
		Bitmap bm = null;
		int blockWidth, blockHeight;
		
		try 
		{
	        final Bitmap image = MediaStore.Images.Media.getBitmap(
	                getContentResolver(), uri);
	        int height = image.getHeight();
	        int width = image.getWidth();
	        
	        if (width > height) {
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        } else {
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        }
	        
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
			Log.e("FreePuzzle", "Bitmap " + width + " "+ height);
			
			blockWidth = 160;
			blockHeight = 160;
			
	        float scalex = (float)dm.widthPixels/width;
	        float scaley = (float)dm.heightPixels/height;
	        float scale = Math.min(scalex, scaley);
	        width = (int)(width * scale);
	        height = (int)(height * scale);
			
			width = (width + (blockWidth - 1)) / blockWidth * blockWidth;
			height = (height + (blockHeight - 1)) / blockHeight * blockHeight;
			
			Log.e("FreePuzzle", "genBitmap " + width + " "+ height);
			
	        bm = Bitmap.createScaledBitmap(image, width, height, false);
        } 
		catch (final IOException e1) {
            return null;
        }
        
        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        linLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        linLayout.addView(new View(this), new LinearLayout.LayoutParams(
        		LayoutParams.FILL_PARENT, 0, 1));

        LinearLayout hl = new LinearLayout(this);
        hl.setOrientation(LinearLayout.HORIZONTAL);
        
        hl.addView(new View(this), new LinearLayout.LayoutParams(
        		0, LayoutParams.FILL_PARENT, 1));

        PuzzleView tv = new PuzzleView(this, bm, blockWidth, blockHeight);
        hl.addView(tv, new LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        hl.addView(new View(this), new LinearLayout.LayoutParams(
        		0, LayoutParams.FILL_PARENT, 1));
        
        linLayout.addView(hl, new LayoutParams(
        		LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        
        linLayout.addView(new View(this), new LinearLayout.LayoutParams(
        		LayoutParams.FILL_PARENT,0, 1));
        
        return linLayout;
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();

        if (Intent.ACTION_SEND.equals(intent.getAction()) && (extras != null)
            && extras.containsKey(Intent.EXTRA_STREAM)) {

            final Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
            if (uri != null) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                requestWindowFeature(Window.FEATURE_NO_TITLE); 
                /* 
                 * Another method for fullscreen
                 * android:theme="@android:style/Theme.NoTitleBar.Fullscreen
                 */
                setContentView(genRootView(uri));
                return;
            }
        }
        
        finish();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
    	super.onConfigurationChanged(newConfig);
    }
}