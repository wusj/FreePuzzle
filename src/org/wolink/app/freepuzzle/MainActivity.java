package org.wolink.app.freepuzzle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
    final int CHOOSE_AN_IMAGE_REQUEST = 2910;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.main);
        ((Button)findViewById(R.id.choosepic)).setOnClickListener(
        		new View.OnClickListener() {
					@Override
					public void onClick(View v) {
				        final Intent intent = new Intent();
				        intent.setType("image/*");
				        intent.setAction(Intent.ACTION_PICK);
				        startActivityForResult(Intent.createChooser(intent,
				            getString(R.string.choose_a_pic)), CHOOSE_AN_IMAGE_REQUEST);					
				    }
				}
        );
    }
    
    @Override
    protected void onActivityResult(final int requestCode,
        final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // we got an Image now upload it :D
            if (requestCode == CHOOSE_AN_IMAGE_REQUEST) {
                final Uri chosenImageUri = data.getData();
                final Intent intent = new Intent();
                // intent.setData(chosenImageUri);
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, chosenImageUri);
                intent.setClass(this, FreePuzzle.class);
                startActivity(intent);
            }
        }
    }
}