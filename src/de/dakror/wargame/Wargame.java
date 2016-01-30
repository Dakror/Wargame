/*******************************************************************************
  * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.dakror.wargame;

import static android.opengl.GLES20.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.badlogic.gdx.ai.GdxAI;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import de.dakror.wargame.entity.Building;
import de.dakror.wargame.entity.Building.Type;
import de.dakror.wargame.entity.Entity;
import de.dakror.wargame.render.Button;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.render.TextureAtlas;
import de.dakror.wargame.util.AndroidLogger;

/**
 * @author Maximilian Stark | Dakror
 */
public class Wargame extends Activity implements GLSurfaceView.Renderer, OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {
	public static TextureAtlas animation, standing, terrain, ui;
	public static int height, width;
	public static float scale = 2f;
	public static Wargame instance;
	public float[] viewMatrix, projMatrix, hudMatrix, viewProjMatrix;
	
	GestureDetector gestureDetector;
	ScaleGestureDetector scaleGestureDetector;
	GLSurfaceView glView;
	World world;
	
	SpriteRenderer spriteRenderer;
	TextRenderer textRenderer;
	
	Button buttonCity, buttonFactory;
	
	Entity previousEntity, selectedEntity;
	
	long lastFrame;
	long lastTimestamp;
	float prevX, prevY, prevNum;
	float vX, vY;
	int frames, fps = 60;
	
	public float money = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GdxAI.setLogger(new AndroidLogger());
		instance = this;
		glView = new GLSurfaceView(this);
		glView.setEGLContextClientVersion(2);
		//		glView.setEGLConfigChooser(new MultisampleConfigChooser());
		glView.setRenderer(this);
		glView.setOnTouchListener(this);
		gestureDetector = new GestureDetector(this, this);
		gestureDetector.setOnDoubleTapListener(this);
		scaleGestureDetector = new ScaleGestureDetector(this, this);
		setContentView(glView);
	}
	
	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		glView.onPause();
	}
	
	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		glView.onResume();
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			glView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
	}
	
	public void initHud() {
		buttonCity = new Button(0, 0, Button.BLUE, Button.SQUARE);
		buttonFactory = new Button(Button.SQUARE_WIDTH + 6, 0, Button.BLUE, Button.SQUARE);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
		spriteRenderer = new SpriteRenderer();
		textRenderer = new TextRenderer("packed/font/copperplate.fnt", "packed/font/bling.fnt");
		viewMatrix = new float[16];
		projMatrix = new float[16];
		hudMatrix = new float[16];
		viewProjMatrix = new float[16];
		
		terrain = new TextureAtlas("packed/terrain.atlas");
		standing = new TextureAtlas("packed/standing/standing.atlas");
		ui = new TextureAtlas("packed/ui.atlas");
		//			animation = new TextureAtlas("packed/animation.atlas");
		//			System.out.println(animation.tiles.size());
		//			System.out.println(animation.tr);
		
		world = new World("maps/lake.map");
		//		for (int i = 0; i < 5; i++)
		//			for (int j = 0; j < 5; j++) {
		//				Building myCity = new Building(8 + i, 8 + j, 0, Type.values()[(int) (Math.random() * Type.values().length)]);
		//				world.addEntity(myCity);
		//			}
		//			
		//		for (int i = 0; i < 5; i++)
		//			for (int j = 0; j < 5; j++) {
		//				Building myCity = new Building(20 - i, 12 - j, 1, Type.values()[(int) (Math.random() * Type.values().length)]);
		//				world.addEntity(myCity);
		//			}
		
		Building myCity = new Building(5, 7, 0, Type.City);
		world.addEntity(myCity);
		world.center(myCity);
		
		Building theirCity = new Building(46, 23, 1, Type.City);
		world.addEntity(theirCity);
		//		for (int i = 0; i < 15; i++) {
		//			Unit u = new Unit(2 + i / 5f, 3 - (i % 2) * 0.5f, 0, Unit.Type.Infantry);
		//			world.addEntity(u);
		//		}
		
		//		Unit v = new Unit(0, 2, 0, Unit.Type.Infantry);
		//		
		//		SteeringBehavior<Vector2> sb = new Pursue<Vector2>(u, v, 0.3f)/*.setTarget(new WorldLocation(new Vector3(2, 2, 0), 0))/*.setArrivalTolerance(u.getZeroLinearSpeedThreshold()).setDecelerationRadius(1f)*/;
		//		u.setSteeringBehavior(sb);
		//		v.setSteeringBehavior(new Arrive<Vector2>(v).setTarget(new WorldLocation(new Vector2(6, 6), 0)).setArrivalTolerance(u.getZeroLinearSpeedThreshold()).setDecelerationRadius(1f));
		//		map.addEntity(v);
		//		map.addEntity(u);
		
		initHud();
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		Wargame.width = width;
		Wargame.height = height;
		clampScale();
		world.clampNewPosition();
		glViewport(0, 0, width, height);
		Matrix.orthoM(projMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, -100, 1000);
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
		Matrix.multiplyMM(viewProjMatrix, 0, projMatrix, 0, viewMatrix, 0);
		System.arraycopy(viewProjMatrix, 0, hudMatrix, 0, 16);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		if (System.currentTimeMillis() - lastTimestamp >= 1000) {
			fps = frames;
			frames = 0;
			lastTimestamp = System.currentTimeMillis();
		}
		
		float timeStep = Math.min(1.0f / fps, 1 / 60f);
		GdxAI.getTimepiece().update(timeStep);
		money += timeStep;
		world.update(timeStep);
		
		glClearColor(130 / 255f, 236 / 255f, 255 / 255f, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
		Matrix.scaleM(viewMatrix, 0, scale, scale, scale);
		
		Matrix.multiplyMM(viewProjMatrix, 0, projMatrix, 0, viewMatrix, 0);
		
		spriteRenderer.begin(viewProjMatrix);
		world.render(spriteRenderer);
		spriteRenderer.end();
		
		glDisable(GL_DEPTH_TEST);
		
		spriteRenderer.begin(hudMatrix);
		textRenderer.renderText(-width / 2, height / 2 - 30, 0, 0.5f, "FPS: " + fps, spriteRenderer);
		textRenderer.renderText(-width / 2, height / 2 - 60, 0, 0.5f, "E: " + world.rEntities + " / " + (world.buildings.size + world.units.size), spriteRenderer);
		
		textRenderer.setFont(1);
		textRenderer.renderText(-200, height / 2 - 80, 0, 1f, "$ " + Math.round(money), spriteRenderer);
		textRenderer.setFont(0);
		
		buttonCity.render(spriteRenderer);
		buttonFactory.render(spriteRenderer);
		
		spriteRenderer.end();
		
		frames++;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		gestureDetector.onTouchEvent(e);
		scaleGestureDetector.onTouchEvent(e);
		float x = e.getX();
		float y = e.getY();
		
		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				buttonCity.onDown(e);
				buttonFactory.onDown(e);
				break;
			case MotionEvent.ACTION_UP:
				buttonCity.onUp(e);
				buttonFactory.onUp(e);
				break;
			case MotionEvent.ACTION_MOVE:
				
				float dx = x - prevX;
				float dy = y - prevY;
				
				if (e.getPointerCount() == prevNum) world.move(dx / scale * 60f / Math.max(fps, 25), dy / scale * 60f / Math.max(fps, 25));
				break;
		}
		
		prevX = x;
		prevY = y;
		prevNum = e.getPointerCount();
		
		return true;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		//world.dirty = true;
		/*
		Entity entity = map.getEntityAt(e.getX() - width / 2, height - e.getY() - height / 2, true);
		previousEntity = selectedEntity;
		if (previousEntity != null) previousEntity.onDeselect();
		selectedEntity = entity;
		if (entity != null) entity.onSelect();
		
		return entity != null;
		*/
		
		return false;
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		scale *= detector.getScaleFactor();
		clampScale();
		world.clampNewPosition();
		return true;
	}
	
	public void clampScale() {
		float width = world.getWidth() * World.WIDTH / 2 + world.getDepth() * World.WIDTH / 2;
		float height = world.getWidth() * World.DEPTH / 2 + world.getDepth() * World.DEPTH / 2 + World.HEIGHT;
		scale = Math.max(Math.max(Wargame.width / width, Wargame.height / height), Math.min(7.5f, scale));
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {}
	
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent e) {}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return false;
	}
	
	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}
}
