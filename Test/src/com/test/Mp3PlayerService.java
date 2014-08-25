package com.test;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Mp3PlayerService extends Service{

	 public final IBinder localBinder = new LocalBinder();
	 
	    private MediaPlayer mplayer= new MediaPlayer();
	    private boolean created = false;
	    @Override
	    public IBinder onBind(Intent intent) {
	        return localBinder;
	    }
	 
	    @Override
	    public void onCreate() {
	 
	    }
	 
	    public class LocalBinder extends Binder {
	        Mp3PlayerService getService() {
	            return Mp3PlayerService.this;
	        }
	    }
	    
	    public void playsd(String path) {

	    	try{
				this.mplayer.reset();
				this.mplayer.setDataSource(path);
				this.mplayer.prepare();
				this.mplayer.start();
				 created = true;
	    	}
			catch(IOException e)
			{
				Log.v(getString(R.string.app_name),e.getMessage());
			}  	
		}
	    
	    
	 
	    public void playSong(Context c) {
	        if(!created){
	        	Toast.makeText(getBaseContext(), "please select a song from the list ", Toast.LENGTH_LONG).show();   
	        }
	        else
	        	this.mplayer.start();
	        	created = true; }
	 
	    public void pauzeSong(Context c) {
	        if(mplayer.isPlaying())
	        		this.mplayer.pause();
	    }

}
