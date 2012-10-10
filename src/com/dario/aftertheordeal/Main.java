package com.dario.aftertheordeal;

/**
 * (c) 2012 Dario Fiorentino
 *  
 *
 * @autore Dario Fiorentino
 * @anno 2012
 * @sito http://dariofiorentinodesign.com
 */



import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;


public class Main extends Activity {
   
	protected boolean _active = true;
	protected int _splashTime = 5000; // 10 secondi

	




	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    
	     MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.aftertheordeal);
	    mp.start();
	    

		
	    
	  
	    // thread for displaying the SplashScreen
	  Thread splashTread = new Thread() {

	        @Override
	        public void run() {
	        	 
	            try {

	                int waited = 0;
	                while(_active && (waited < _splashTime)) {

	                    sleep(100);
	                    if(_active) {
	                    	
	                        waited += 100;
	                    }

	                }

	            } catch(InterruptedException e) {

	                // do nothing
	            } finally {
	                	
	                finish();

	                	
	                	Intent i = new Intent(Main.this, game.class);
	                     startActivity(i);
	                     
	                	stop();
	                	
	            }

	        }

	    };

	    splashTread.start();	
	}


	 @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        	
	            _active = false;
	            finish();
	            Intent i = new Intent(Main.this, game.class);
                startActivity(i);
                
                
                
	        }
	        return true;
	    }   


	 @Override
	    public void onBackPressed() {
	            super.onBackPressed();
	            this.finish();
	    }
	


	
}
