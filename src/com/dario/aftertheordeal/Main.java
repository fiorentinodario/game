package com.dario.aftertheordeal;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.input.touch.TouchEvent;




import org.andengine.opengl.texture.region.ITextureRegion;

import android.opengl.GLES20;




/**
 * (c) 2012 Dario Fiorentino
 *  
 *
 *@autore Dario Fiorentino
 *@anno 2012
 *@sito http://dariofiorentinodesign.com
 */
public class Main extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	// ===========================================================
	// Fields
	// ===========================================================

	private BoundCamera mBoundChaseCamera;

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mPlayerTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
//////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	private BitmapTextureAtlas mOnScreenControlTexture;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	
	private DigitalOnScreenControl mDigitalOnScreenControl;
	
	private int pWaypointIndex;
	
	private boolean mPlaceOnScreenControlsAtDifferentVerticalLocations = false;
	private IEntity tasto_A;
	
	
	
	
	/*						 
	 private BitmapTextureAtlas splashTextureAtlas;
	 private ITextureRegion splashTextureRegion;
	 private Sprite splash;
	
							 */
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////


	@Override
	public EngineOptions onCreateEngineOptions() {
		
		this.mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mBoundChaseCamera);
		

	}
	
	

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 72, 128, TextureOptions.DEFAULT);
		this.mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "player.png", 0, 0, 3, 4);
		this.mBitmapTextureAtlas.load();
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		

	}
	
	
	

	@Override
	public Scene onCreateScene() {
		

		
		
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////	 hud 	////////////////////////////////////////////		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*	
		HUD hud = new HUD();
		 hud.attachChild(tasto_A);
		 mBoundChaseCamera.setHUD(hud);
		
*/
////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        
		
		
		
		
		
		this.mEngine.registerUpdateHandler(new FPSLogger());
		Scene scene = new Scene();
		try {
			final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					/* We are going to count the tiles that have the property "cactus=true" set. */
					if(pTMXTileProperties.containsTMXProperty("cactus", "true")) {
						Main.this.mCactusCount++;
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
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		final PhysicsHandler physicsHandler = new PhysicsHandler(player);
		player.registerUpdateHandler(physicsHandler);
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		this.mBoundChaseCamera.setChaseEntity(player);

	/*	final Path path = new Path(5).to(0, 160).to(0, 500).to(600, 500).to(600, 160).to(0, 160); 

	//	player.registerEntityModifier(new LoopEntityModifier(new PathModifier(30, path, null, new IPathModifierListener() {  
			//@Override
			public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

			}  

			//@Override
			public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				switch(pWaypointIndex) {
					case 0:
						player.animate(new long[]{200, 200, 200}, 6, 8, true);
						break;
					case 1:
						player.animate(new long[]{200, 200, 200}, 3, 5, true);
						break;
					case 2:
						player.animate(new long[]{200, 200, 200}, 0, 2, true);
						break;
					case 3:
						player.animate(new long[]{200, 200, 200}, 9, 11, true);
						break;
				}
			}*/

		/*	@Override
			public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

			}*/

		//	@Override
		//	public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

	//}

	//	}))); 
		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////  (controller sinistro). /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		this.mDigitalOnScreenControl = new DigitalOnScreenControl
				(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(),
						this.mBoundChaseCamera, this.mOnScreenControlBaseTextureRegion,
						this.mOnScreenControlKnobTextureRegion, 0.1f,
						this.getVertexBufferObjectManager(), 
						new IOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				physicsHandler.setVelocity(pValueX * 100, pValueY * 100);
				
				//_______________________

				
				
				
				
				int frazione = (int) (pValueX/pValueY);
				if(pValueX >0 && frazione != 0){
					pWaypointIndex = 1;//destra
				}else if(pValueX <0 && frazione != 0){
					pWaypointIndex = 3;	//sinistra
				}else if(pValueY >0 && frazione == 0){
					pWaypointIndex = 0;//frotale
				}else if(pValueY<0 && frazione == 0){
					pWaypointIndex = 2;	//retro
				}else if(pValueY==0.0 && frazione == 0 && pValueX ==0.0){
					pWaypointIndex = 4;	//retro
				}

				switch(pWaypointIndex) {
					case 0:
						player.animate(new long[]{50, 50, 50},6,8, true);//frontale
						break;
					case 1:
						player.animate(new long[]{50, 50, 50}, 3, 5, true); //destra
						break;
					case 2:
						player.animate(new long[]{50, 50, 50}, 0, 2, true); //retro
						break;
					case 3:
						player.animate(new long[]{50, 50, 50}, 9, 11, true); //sinistra
						break;
					case 4:
						player.stopAnimation();
						break;
					
				} 
				
				/*System.out.println("_________________________");
				System.out.println("_________________________");
				System.out.println("frazione: "+ frazione);
				System.out.println("pWaypointIndex: "+ pWaypointIndex);
				System.out.println("pValueY: "+ pValueY);
				System.out.println("pValueX: "+ pValueX);
				System.out.println("_________________________");
				System.out.println("_________________________"); */

				//_______________________
			}

			/*//@Override
			public void onControlClick(
					DigitalOnScreenControl pDigitalOnScreenControl) {
				
				// TODO Auto-generated method stub
			} */
				
			

		});

		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setAlpha(0f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(this.mDigitalOnScreenControl);
		
		
		
	/*	//  (controller destro). 
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
		
		
		mDigitalOnScreenControl.setChildScene(rotationOnScreenControl);
		
		
		*/
		
		
		
		

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		

		/* Now we are going to create a rectangle that will  always highlight the tile below the feet of the pEntity. */
	//	final Rectangle currentTileRectangle = new Rectangle(0, 0, this.mTMXTiledMap.getTileWidth(), this.mTMXTiledMap.getTileHeight(), this.getVertexBufferObjectManager());
	//	currentTileRectangle.setColor(1, 0, 0, 0.25f);
	//	scene.attachChild(currentTileRectangle); 

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
					// tmxTile.setTextureRegion(null); //<-- Rubber-style removing of tiles =D 
		//			currentTileRectangle.setPosition(tmxTile.getTileX(), tmxTile.getTileY()); //___________________________________________________
				}
				
			}
			

		});
		scene.attachChild(player);
		
		return scene;

		
		
		
	}
	
	
	
	
	
	
	 @Override
	    public void onBackPressed() {
	            super.onBackPressed();
	            this.finish();
	    }
	 
	 
	 
	 
	 
	/* 
	 
	 private void createUnwalkableObjects(TMXTiledMap map){
         // Loop through the object groups
          for(final TMXObjectGroup group: this.mTMXTiledMap.getTMXObjectGroups()) {
                  if(group.getTMXObjectGroupProperties().containsTMXProperty("wall", "true")){
                          // This is our "wall" layer. Create the boxes from it
                          for(final TMXObject object : group.getTMXObjects()) {
                                 final Rectangle rect = new Rectangle(object.getX(), object.getY(),object.getWidth(), object.getHeight(), null, null);
                                 final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
                                 PhysicsFactory.createBoxBody(this.mPhysicsWorld, rect, BodyType.StaticBody, boxFixtureDef);
                                 rect.setVisible(false);
                                 mScene.attachChild(rect);
                          }
                  }
          }
 }
	 
	 
	 

 private void addBounds(float width, float height){
         final Shape bottom = new Rectangle(0, height - 2, width, 2, null, null);
         bottom.setVisible(false);
         final Shape top = new Rectangle(0, 0, width, 2, null, null);
         top.setVisible(false);
         final Shape left = new Rectangle(0, 0, 2, height, null, null);
         left.setVisible(false);
         final Shape right = new Rectangle(width - 2, 0, 2, height, null, null);
         right.setVisible(false);

         final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
         PhysicsFactory.createBoxBody(this.mPhysicsWorld, (IAreaShape) bottom, BodyType.StaticBody, wallFixtureDef);
         PhysicsFactory.createBoxBody(this.mPhysicsWorld, top, BodyType.StaticBody, wallFixtureDef);
         PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
         PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

         this.mScene.attachChild(bottom);
         this.mScene.attachChild(top);
         this.mScene.attachChild(left);
         this.mScene.attachChild(right);
 } */

	

}
