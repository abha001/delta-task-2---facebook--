package com.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.test.Mp3PlayerService.LocalBinder;

import android.media.MediaMetadataEditor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

class Mp3Filter implements FilenameFilter{

	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".mp3"));
	}
}

public class MainActivity extends ListActivity implements SensorEventListener{

	ImageView i,iv;
	long lastUpdate = 0;
	float last_x =-1.0f;
	float last_y=-1.0f;
	float last_z=-1.0f;
	float SHAKE_THRESHOLD=60;
	float x,y,z;
	byte[] art;
	String song_name;
	boolean sensor_active=false;
	MediaMetadataRetriever metaRetriver= new MediaMetadataRetriever();
	Drawable img;
	Bitmap songImage  ;
	int count;
	SensorManager sm;
	Sensor s;
		static int a=0;
	    private Mp3PlayerService mp3Service;
	    private Eg eg ;
	    private static final String path = new String (Environment.getExternalStorageDirectory().getAbsolutePath());
		private List<String> song= new ArrayList<String>();
	 
	    private ServiceConnection mp3PlayerServiceConnection = new ServiceConnection() {
	        @Override
	        public void onServiceConnected(ComponentName arg0, IBinder binder) {
	            mp3Service = ((LocalBinder) binder).getService();
	 
	        }
	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	
	        }
	    };
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, Mp3PlayerService.class));
    	update();
        Intent connectionIntent = new Intent(this, Mp3PlayerService.class);
        bindService(connectionIntent, mp3PlayerServiceConnection,
                Context.BIND_AUTO_CREATE);
        Button stop =(Button) findViewById(R.id.button4);
        
        stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 mp3Service.stopSelf();
				 sm.unregisterListener(MainActivity.this, s);
				 sm.unregisterListener(MainActivity.this);
				 sensor_active = false;
			}
		});
 
        i= (ImageView) findViewById(R.id.i1);
        i.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent i =new Intent (MainActivity.this,Eg.class);
				i.putExtra("path",path + File.separator + song.get(a));
				i.putExtra("song_name", song_name);
				startActivity(i);
			}
		});
        
      final Button play_button = (Button) findViewById(R.id.button1);
        play_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mp3Service.playSong(getBaseContext());
            }
        }); 
      
        sm= (SensorManager) getSystemService(SENSOR_SERVICE);
        s =sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        final Button pre = (Button) findViewById(R.id.button3);
        pre.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				a--;
				if (a >=0)	
				{	pic(a);
					mp3Service.playsd(path + File.separator + song.get(a));
					
				}
				else if(a==-1)
					{mp3Service.pauzeSong(getBaseContext());
					Toast.makeText(MainActivity.this, "no more songs", Toast.LENGTH_LONG).show();
					}
				else
					Toast.makeText(MainActivity.this, "choose a song from the list ...plzz", Toast.LENGTH_LONG).show();
			}
		});
 
        final Button post = (Button) findViewById(R.id.button5);
        post.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				a++;
				if (a <= count-1)	
				{
					pic(a);
					mp3Service.playsd(path + File.separator + song.get(a));
					
				}
				else 
					{mp3Service.pauzeSong(getBaseContext());
					Toast.makeText(MainActivity.this, "no more songs ", Toast.LENGTH_LONG).show();
					}
			}
		});
        
        final Button pauze_button = (Button) findViewById(R.id.button2);
        pauze_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mp3Service.pauzeSong(getBaseContext());
            }
        });
    }
  /*  public void onCompletion(MediaPlayer mpPlayer) {
    	Toast.makeText(getBaseContext(), "song complete", Toast.LENGTH_SHORT).show();
    	a++;
		if (a <= count-1)	
		{
			pic(a);
			mp3Service.playsd(path + File.separator + song.get(a));
			
		}
		else 
			{mp3Service.pauzeSong(getBaseContext());
			Toast.makeText(MainActivity.this, "no more songs ", Toast.LENGTH_LONG).show();
			}
       
    }*/
    
    
    public void onListItemClick(ListView l, View v, int position, long id) {
    	a= position;
    	l = getListView();
    	count = l.getCount();
    	pic(a);
    	if(!sensor_active){
    		sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    		sensor_active = true;
    	}
    	mp3Service.playsd(path + File.separator + song.get(position));
    	
    	 	
    } 

   
    private void update() {
		File home = new File(path);
		if(home.listFiles(new Mp3Filter()).length>0){
			for (File f : home.listFiles( new  Mp3Filter())){
				song.add(f.getName());
			}
			ArrayAdapter<String> sl = new ArrayAdapter<String>(this, R.layout.song_list,song);
			setListAdapter(sl);
		}	
	}
  
@Override
	protected void onResume() {
	super.onResume();
}

    @Override
    protected void onDestroy() {
        unbindService(this.mp3PlayerServiceConnection);
        sensor_active=false;
        sm.unregisterListener(this);
        super.onDestroy();
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		
	}
	
	public void pic(int a) {
		metaRetriver.setDataSource(path + File.separator + song.get(a));
		art = metaRetriver.getEmbeddedPicture();
		song_name = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)+"\n"+ metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)+"\n"+metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
		try{
			 songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
			i.setImageBitmap(songImage);
			
		}
		catch(Exception e){
			img = getResources().getDrawable(R.drawable.music);
			i.setImageDrawable(img);
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		long curTime = System.currentTimeMillis();
		 if (event.values[0]>=5 || event.values[0]<=-4) {
			 
			
			if( curTime - lastUpdate >1000)
			{
				 lastUpdate = curTime;
				if(event.values[0]<=5)
				{
					a++;
					if (a <= count-1)	
					{
						pic(a);
						mp3Service.playsd(path + File.separator + song.get(a));
						
					}
					else 
						{mp3Service.pauzeSong(getBaseContext());
						Toast.makeText(MainActivity.this, "no more songs ", Toast.LENGTH_LONG).show();
						}
					Toast.makeText(this, "next" +event.values[0] , Toast.LENGTH_SHORT).show();}
				else if(event.values[0]>=-4)
				{ 
					a--;
					if (a >=0)	
					{	pic(a);
						mp3Service.playsd(path + File.separator + song.get(a));
						
					}
					else if(a==-1)
						{mp3Service.pauzeSong(getBaseContext());
						Toast.makeText(MainActivity.this, "no more songs", Toast.LENGTH_LONG).show();
						}
					else
						Toast.makeText(MainActivity.this, "choose a song from the list ...plzz", Toast.LENGTH_LONG).show();
						Toast.makeText(this, "previous" +event.values[0] , Toast.LENGTH_SHORT).show();
					}
			}
			
			
		     /* long diffTime = (curTime - lastUpdate);
		      lastUpdate = curTime;

		      x = event.values[0];
		      y = event.values[1];
		      z = event.values[2];

		      float speed = (x -last_x+y -last_y+z -last_z ) / diffTime * 1000;

		      if (speed > SHAKE_THRESHOLD) {
		       
		      
              Toast.makeText(this, "great lastupdate " + lastUpdate+"x"+x,Toast.LENGTH_SHORT).show();
		      }
		      else if (-speed >-SHAKE_THRESHOLD)
		      {
		      
		     Toast.makeText(this, "less last update " + lastUpdate +"x"+x, Toast.LENGTH_SHORT).show();
				      }
		      last_x = x;
		      last_y = y;
		      last_z = z;
		      */
		    }
		
	}

	
}
    

