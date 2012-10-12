package com.dario.aftertheordeal;

import java.util.HashMap;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.Shape;
import org.andengine.extension.physics.box2d.PhysicsFactory;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.extension.physics.box2d.PhysicsWorld;

import android.app.Activity;


public class Entities {
	  @SuppressWarnings("unused")
	private static Body body;
	private static PhysicsWorld mPhysicsWorld;
	  public static final Object LEVEL_TYPE_WALL = "wall";
	  public static void addEntity(
	    Activity pParent,
	    Scene pScene,
	    int pX,
	    int pY,
	    int pWidth,
	    int pHeight,
	    String pType,
	    HashMap<String,String> properties)
	  {
	    if(pType.equals(Entities.LEVEL_TYPE_WALL)) {
	      Entities.addWall(pParent, pScene, pX, pY, pWidth, pHeight,properties);
	    } 
	  }

	  private static void addWall(
	    Activity pParent,
	    Scene pScene,
	    int pX,
	    int pY,
	    int pWidth,
	    int pHeight,
	    HashMap<String,String> properties)
	  {
	    final Shape wall = new Rectangle(pX, pY, pWidth, pHeight, null, null);
	    wall.setVisible(false);
	    if(properties.containsKey("rotate")){
	      wall.setRotation(Float.parseFloat(properties.get("rotate")));
	    }
	    
	 
	    
	    FixtureDef FIXTURE_DEF = null;
		body = PhysicsFactory.createBoxBody(
	               mPhysicsWorld,
	               (IAreaShape) wall,
	               BodyType.StaticBody,
	               FIXTURE_DEF
	               );
	    pScene.getFirstChild().attachChild(wall);
	  }
	} 
