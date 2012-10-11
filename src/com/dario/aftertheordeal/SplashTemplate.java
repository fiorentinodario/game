package com.dario.aftertheordeal;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;

import android.opengl.GLES20;
import android.view.KeyEvent;

/**
 * @author Matim Development
 * @version 1.0.0
 * <br><br>
 * https://sites.google.com/site/matimdevelopment/
 */
public class SplashTemplate extends BaseGameActivity
{
	private final int CAMERA_WIDTH = 720;
	private final int CAMERA_HEIGHT = 480;
	
	//private Camera camera;
	private Scene splashScene;
	private Scene mainScene;
	
    private BitmapTextureAtlas splashTextureAtlas;
    private ITextureRegion splashTextureRegion;
    private Sprite splash;
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BoundCamera mBoundChaseCamera;
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	private int pWaypointIndex;
	private boolean mPlaceOnScreenControlsAtDifferentVerticalLocations = false;
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
	private enum SceneType
	{
		SPLASH,
		MAIN,
		OPTIONS,
		WORLD_SELECTION,
		LEVEL_SELECTION,
		CONTROLLER
	}
	
	private SceneType currentScene = SceneType.SPLASH;
	
	
	
	
	
	
	
	
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		/*camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
		return engineOptions;*/
		
		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
		
	}
	
	
	
	
	
	

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        splashTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
        splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, this, "splash.png", 0, 0);
        splashTextureAtlas.load();
       
        pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	
	
	
	
	

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception
	{
		initSplashScene();
        pOnCreateSceneCallback.onCreateSceneFinished(this.splashScene);

        
	}
	
	
	
	
	
	
	

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception
	{
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() 
		{
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                loadResources();
                loadScenes();         
                splash.detachSelf();
                mEngine.setScene(mainScene);
                currentScene = SceneType.MAIN;
            }
		}));
  
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	
	
	
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
	    {	    	
	    	switch (currentScene)
	    	{
	    		case SPLASH:
	    			break;
	    		case MAIN:
	    			System.exit(0);
	    			break;
			case CONTROLLER:
				break;
			case LEVEL_SELECTION:
				break;
			case OPTIONS:
				break;
			case WORLD_SELECTION:
				break;
			default:
				break;
	    	}
	    }
	    return false; 
	}
	
	
	
	
	
	
	
	
	
	public void loadResources() 
	{
		// Load your game resources here!
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 72, 128, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "player.png", 0, 0, 3, 4);
		this.mBitmapTextureAtlas.load();
		
		
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	
	
	
	
	
	
	
	
	
	
	private void loadScenes()
	{
		// load your game here, you scenes
		
		/////////
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final Scene scene = new Scene();
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					/* We are going to count the tiles that have the property "cactus=true" set. */
					if(pTMXTileProperties.containsTMXProperty("cactus", "true")) {
						SplashTemplate.this.mCactusCount++;
					}
				}
			});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");

			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
			
				}
			});
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

		final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
		scene.attachChild(tmxLayer);

		/* per fare in modo che la camera non superi TMXTiledMap */
		this.mBoundChaseCamera.setBounds(0, 0, tmxLayer.getHeight(), tmxLayer.getWidth());
		this.mBoundChaseCamera.setBoundsEnabled(true);

		/* per calcolare le coordinate del player per centrarlo alla camera */
		final float centerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mPlayerTextureRegion.getHeight()) / 2;

		/* creare il player. */
		final AnimatedSprite player = new AnimatedSprite(centerX, centerY, this.mPlayerTextureRegion, this.getVertexBufferObjectManager());
		

		final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(physicsHandler);
		
		
		this.mBoundChaseCamera.setChaseEntity(player);


		
		
		
		
		
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////  (controller sinistro). /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl
				(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), 
						this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion, 
						this.mOnScreenControlKnobTextureRegion, 0.1f, 200, 
						this.getVertexBufferObjectManager(), 
						new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				physicsHandler.setVelocity(pValueX * 100, pValueY * 100);
				

				
				int frazione = (int) (pValueX/pValueY);
				if(pValueX >0 && frazione != 0){
					pWaypointIndex = 1;//destra
				}else if(pValueX <0 && frazione != 0){
					pWaypointIndex = 3;	//sinistra
				}else if(pValueY >0 && frazione == 0){
					pWaypointIndex = 0;//frotale
				}else if(pValueY<0 && frazione == 0){
					pWaypointIndex = 2;	//retro
				}

				switch(pWaypointIndex) {
					case 0:
						player.animate(new long[]{50, 50, 50},6,8, false);//frontale
						break;
					case 1:
						player.animate(new long[]{50, 50, 50}, 3, 5, false); //destra
						break;
					case 2:
						player.animate(new long[]{50, 50, 50}, 0, 2, false); //retro
						break;
					case 3:
						player.animate(new long[]{50, 50, 50}, 9, 11, false); //sinistra
						break;
					
				}


			}
			
			

			@Override
			public void onControlClick(
					AnalogOnScreenControl pAnalogOnScreenControl) {
				// TODO Auto-generated method stub
				
			}

		});

		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.2f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setScale(1.25f);
		analogOnScreenControl.getControlKnob().setAlpha(0.5f);
		analogOnScreenControl.refreshControlKnobPosition();
		scene.setChildScene(analogOnScreenControl);
		
		
		
		//  (controller destro). 
		// final float x1 = 0;
		final float y1 = CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight();
		final float y2 = (this.mPlaceOnScreenControlsAtDifferentVerticalLocations) ? 0 : y1;
		final float x2 = CAMERA_WIDTH - this.mOnScreenControlBaseTextureRegion.getWidth(); 
		final AnalogOnScreenControl rotationOnScreenControl = new AnalogOnScreenControl
				(x2, y2, 
						this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion, 
						this.mOnScreenControlKnobTextureRegion, 0.1f, 200, 
						this.getVertexBufferObjectManager(), 
						new IAnalogOnScreenControlListener() {

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				player.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.25f, 1, 2), new ScaleModifier(0.25f, 2, 1))); //0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1
			}

			@Override
			public void onControlChange(
					BaseOnScreenControl pBaseOnScreenControl, float pValueX,
					float pValueY) {
				// TODO Auto-generated method stub
				
			}

			
		});
		rotationOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		rotationOnScreenControl.getControlBase().setAlpha(0f);
		rotationOnScreenControl.getControlKnob().setAlpha(0.5f);
		
		
		analogOnScreenControl.setChildScene(rotationOnScreenControl);
		


		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				/* Get the scene-coordinates of the players feet. */
				final float[] playerFootCordinates = player.convertLocalToSceneCoordinates(12, 31);

				/* Get the tile the feet of the player are currently waking on. */
				final TMXTile tmxTile = tmxLayer.getTMXTileAt(playerFootCordinates[Constants.VERTEX_INDEX_X], playerFootCordinates[Constants.VERTEX_INDEX_Y]);
				if(tmxTile != null) {

				}
				
			}
			

		});
		scene.attachChild(player);
		
		//return scene;
		
		////////
		mainScene = new Scene();
		//mainScene.setBackground(new Background(50, 50, 50));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// ===========================================================
	// INITIALIZIE  
	// ===========================================================
	
	private void initSplashScene()
	{
    	splashScene = new Scene();
    	splash = new Sprite(0, 0, splashTextureRegion, mEngine.getVertexBufferObjectManager())
    	{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
    	};
    	
    	splash.setScale(1.5f);
    	splash.setPosition((CAMERA_WIDTH - splash.getWidth()) * 0.5f, (CAMERA_HEIGHT - splash.getHeight()) * 0.5f);
    	splashScene.attachChild(splash);
	}
	
	
	
	
	
	
	
}
