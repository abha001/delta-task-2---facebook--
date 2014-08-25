package com.test;

import java.io.File;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Eg extends Activity  {

	ImageView iv,i2;
	TextView tv;
	byte[] art;
	MediaMetadataRetriever metaRetriver= new MediaMetadataRetriever();
	Drawable img;
	Bitmap songImage  ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ab);
		tv = (TextView) findViewById(R.id.textView2);
		tv.setText(getIntent().getExtras().getString("song_name"));
		i2=(ImageView) findViewById(R.id.i2); 
		i2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//mp3Service.playSong(getBaseContext());
				
			}
		});
		
		iv=(ImageView) findViewById(R.id.iv);
		Toast.makeText(this, (getIntent().getExtras().getString("path")),Toast.LENGTH_LONG).show();
		pic(getIntent().getExtras().getString("path"));
	}

	 public void pic(String picp) {
	    	metaRetriver.setDataSource(picp);
			art = metaRetriver.getEmbeddedPicture();
			try{
				
				 songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
				iv.setImageBitmap(songImage);
				
			}
			catch(Exception e){
				img = getResources().getDrawable(R.drawable.music);
				iv.setImageDrawable(img);
		}

		}

	 @Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
}
