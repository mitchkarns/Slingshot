package com.example.testtd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Shoot extends Activity {
	PelletShoot p;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		p = new PelletShoot(this);
        setContentView(p);
	}
}

class PelletShoot extends SurfaceView implements SurfaceHolder.Callback {
	GameThread thread;
	int score;
    int screenW; //Device's screen width
    int screenH; //Devices's screen height
    int pelX; //Pellet x position
    int pelY; //Pellet y position
    int oniL1X; //Left Onion 1 x position
    int oniL1Y; //Left Onion 1 y position
    int oniL2X; //Left Onion 2 x position
    int oniL2Y; //Left Onion 2 y position
    int oniL3X; //Left Onion 3 x position
    int oniL3Y; //Left Onion 3 y position
    int oniR1X; //Right Onion 1 x position
    int oniR1Y; //Right Onion 1 y position
    int oniR2X; //Right Onion 2 x position
    int oniR2Y; //Right Onion 2 y position
    int oniR3X; //Right Onion 3 x position
    int oniR3Y; //Right Onion 3 y position
    float dY; //Pellet vertical speed
    float dX; //Onion horizontal speed
    int pelW; //Pellet width
    int pelH; //Pellet height
    int onW; //Onion width
    int onH; //Onion height
    int bgrW;
    int bgrH;
    Bitmap pellet, bgr, onionL1, onionR1;
    boolean pMove; //to check if the pellet is moving
    
	public PelletShoot(Context context){
    	super(context);
    	pellet = BitmapFactory.decodeResource(getResources(),R.drawable.pellet);
    	onionL1 = BitmapFactory.decodeResource(getResources(),R.drawable.onion);
    	onionR1 = BitmapFactory.decodeResource(getResources(),R.drawable.onion);
    	bgr = BitmapFactory.decodeResource(getResources(),R.drawable.sky_bgr);
    	pMove = false;
    	score = 0;
    	
    	//Used as a buffer to make collisions easier
    	pelW = pellet.getWidth();
    	pelH = pellet.getHeight();
    	
    	//Setting the thread
        getHolder().addCallback(this);

        setFocusable(true);
    }
    
    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //This event-method provides the real dimensions of this custom view.
        screenW = w;
        screenH = h;
        
        bgr = Bitmap.createScaledBitmap(bgr, w, h, true); //Scaling the background
        bgrW = bgr.getWidth();
        bgrH = bgr.getHeight();
        
        
        dY = (float) 70.0;
        dX = (float) 30.0;
        pelX = (int) (screenW /2); //Make the pellets X be halfway through the screen
        pelY = (int) (screenH); //Make the pellets Y be at the bottom of the screen
        oniL1X = -50; //Onion L1 start X position
        oniL1Y = (int) (screenH /2); //Onion L1 start Y position
        oniR1X = screenW + 50;
        oniR1Y = (int) (screenH /3);
    }
    
    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                break;
            }

            case MotionEvent.ACTION_UP:
            	if (!pMove)
            		pelY = (int) (screenH); 
            		pMove = true;
                break;
            }
        return true;
    }
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        
        //Compute the pellets location
        if (pelY > -pelH){ //We only want to move the pellet if its not at the top of the screen
	        if (pMove) {
	            pelY -= (int)dY;
	        }
        }
        else //Now that the pellet is at Y = 0 it isnt moving anymore
        	pMove = false;
        
        //Onion Movement
        if(oniL1X < ((int)screenH + 40))
        	oniL1X += (int)dX;
        else
        	oniL1X = -50;
        
        if(oniR1X > (-40))
        	oniR1X -= (int)dX;
        else
        	oniR1X = screenW + 50;
        
        //How we break onions (checking collisions between pellet and onion)
        //The first part checks if the pellet is within the onions X range
        //The second part checks if its within its Y range
        //if((((pelX)  <= (oniL1X + onW)) && ((pelX + pelW) >= (oniL1X))) && ((pelY) <= (oniL1Y + onH) && ((pelY + pelH) >= (oniL1Y)))){
        if (withinRange(pelX, pelY, oniL1X, oniL1Y)){	
        	oniL1X = -1500;
        	score++;
        }
        
        //if((((pelX)  <= (oniR1X + onW)) && ((pelX + pelW) >= (oniR1X))) && ((pelY) <= (oniR1Y + onH) && ((pelY + pelH) >= (oniR1Y)))){
        if (withinRange(pelX, pelY, oniR1X, oniR1Y)){
        	oniR1X = screenW + 1500;
        	score++;
        }
        
        
	    canvas.drawBitmap(bgr, 0, 0, null);
        canvas.drawBitmap(pellet, pelX, pelY, null); //Draw the pellet with the updated Y value (X should always be the same)
        canvas.drawBitmap(onionL1, oniL1X, oniL1Y, null); //Draw the first left onion
        canvas.drawBitmap(onionR1, oniR1X, oniR1Y, null); //Draw the first left onion
        
        Paint paint = new Paint(); 
        paint.setColor(Color.BLACK); 
        if (score < 1)
        	paint.setTextSize(2);
        else if (score > 20)
        	paint.setTextSize(40);
        else
        	paint.setTextSize(score * 2);
        String s = "Score: " + score;
        canvas.drawText(s, 30, 30, paint); 
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }
    
    public boolean withinRange(int pX, int pY, int oX, int oY){
    	int Xdiff = oX - pX;
    	int Ydiff = oY - pY;
    	if (Xdiff < 30 && Xdiff > -30)
    		if(Ydiff < 30 && Ydiff > -30)
    			return true;
    	return false;
    }
    
    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private PelletShoot gameView;
        private boolean run = false;

        public GameThread(SurfaceHolder surfaceHolder, PelletShoot gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean run) {
            this.run = run;
        }

        public SurfaceHolder getSurfaceHolder() {
            return surfaceHolder;
        }

        @Override
        public void run() {
            Canvas c;
            
            while (run) {
                c = null;
                
                try {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                       //call methods to draw and process next fame
                        gameView.draw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
	
}