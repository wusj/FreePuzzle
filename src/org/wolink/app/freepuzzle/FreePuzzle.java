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
		
		try 
		{
	        final Bitmap image = MediaStore.Images.Media.getBitmap(
	                getContentResolver(), uri);
	        int height = image.getHeight();
	        int width = image.getWidth();
	        int desiredHeight, desiredWidth;
	        
	        if (width > height) {
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        } else {
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        }
	        
	        getWindowManager().getDefaultDisplay().getMetrics(dm);
	        desiredHeight = dm.heightPixels;
	        desiredWidth = dm.widthPixels;
			Log.e("FreePuzzle", "Bitmap " + width + " "+ height);
			Log.e("FreePuzzle", "Screen " + desiredWidth + " "+ desiredHeight);
	        float scalex = (float)desiredWidth/width;
	        float scaley = (float)desiredHeight/height;
	        float scale = Math.min(scalex, scaley);
	        width = (int)(width * scale);
	        height = (int)(height * scale);
			Log.e("FreePuzzle", "genBitmap " + width + " "+ height);
	        bm = Bitmap.createScaledBitmap(image, width, height, false);
        } 
		catch (final IOException e1) {
            return null;
        }
        
        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        linLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        ImageView tv = new ImageView(this);
        tv.setImageBitmap(bm);
        linLayout.addView(tv, new LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
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