package org.wolink.app.freepuzzle;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class FreePuzzle extends Activity implements PuzzleView.OnActionListener{
    private SoundPool mSoundPool;
    private int swapSound, winSound;

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
	        
	        int minW = Math.min(dm.widthPixels, dm.heightPixels);
	        if (minW <= 240) blockWidth = 40;
	        else if (minW <= 320) blockWidth = 60;
	        else blockWidth = 80;	
	        
			blockHeight = blockWidth;
	        int targetWidth = (dm.widthPixels / blockWidth) * blockWidth;
	        int targetHeight = (dm.heightPixels / blockHeight) * blockHeight;
			
	        float scalex = (float)targetWidth/width;
	        float scaley = (float)targetHeight/height;
	        float scale = Math.min(scalex, scaley);
	        width = (int)(width * scale);
	        height = (int)(height * scale);
			
			width = (width + (blockWidth - 1)) / blockWidth * blockWidth;
			height = (height + (blockHeight - 1)) / blockHeight * blockHeight;
			
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
        tv.SetOnActionListener(this);
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
                 * Load Sound Resource
                 */
                mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                swapSound = mSoundPool.load(this, R.raw.swap, 1);
                winSound = mSoundPool.load(this, R.raw.win, 1);
                
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
    
	@Override
	public void onFinish(PuzzleView v) {
    	playSound(winSound);
	}
	
	@Override
	public void onSwap(PuzzleView v) {
		playSound(swapSound);
 	}
	
	private void playSound(int id) {
		AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		float streamVolume = 0.0f;
		streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume /= audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		mSoundPool.play(id, streamVolume, streamVolume, 1, 0, 1.0f);
	}
}