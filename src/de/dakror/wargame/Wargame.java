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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import de.dakror.wargame.entity.building.Building;
import de.dakror.wargame.entity.building.Building.Type;
import de.dakror.wargame.entity.building.City;
import de.dakror.wargame.render.Sprite;
import de.dakror.wargame.render.SpriteRenderer;
import de.dakror.wargame.render.TextRenderer;
import de.dakror.wargame.render.TextureAtlas;
import de.dakror.wargame.ui.Button;
import de.dakror.wargame.ui.Panel;
import de.dakror.wargame.ui.UI;
import de.dakror.wargame.util.ActivityStub;
import de.dakror.wargame.util.AndroidLogger;
import de.dakror.wargame.util.Listeners.ButtonListener;

/**
 * @author Maximilian Stark | Dakror
 */
public class Wargame extends ActivityStub {
	public static final Color HALFWHITE = new Color(1, 1, 1, 0.5f);
	public static final Color HALFRED = new Color(1, 0, 0, 0.5f);
	
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
	
	Button[] buyButtons;
	
	Panel detailsPanel;
	
	Building placeBuilding;//@off
	public synchronized Building getPlaceBuilding(){ return placeBuilding; }
	public synchronized void setPlaceBuilding(Building b){ placeBuilding = b; }//@on
	
	long lastFrame;
	long lastTimestamp;
	float prevX, prevY, prevNum;
	float vX, vY;
	int frames, fps = 60;
	
	public static Player player, enemy;
	
	boolean hudEvents = false;
	
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
	
	public void initHud() {
		if (height == 0) return;
		buyButtons = new Button[Building.Type.values().length];
		detailsPanel = new Panel(-width / 2 + 5, -height / 2 + 5, 500, 300, UI.BEIGE);
		
		final ButtonListener bl = new ButtonListener() {
			
			@Override
			public void onDown(Button b) {
				boolean t = b.isToggled();
				for (Button bt : buyButtons)
					bt.setToggled(false);
				b.setToggled(t);
			}
			
			@Override
			public void onUp(Button b) {
				if (b.isToggled()) {
					Building pb = Building.create(-5000, 0, player, (Type) b.getPayload());
					pb.setColor(HALFWHITE);
					pb.setWorld(world);
					setPlaceBuilding(pb);
				} else setPlaceBuilding(null);
			}
		};
		
		for (int i = 0; i < buyButtons.length; i++) {
			Button b = new Button(width / 2 - UI.BTN_SQUARE_WIDTH * (buyButtons.length - i) - 15, -height / 2 + 15, UI.BROWN, UI.BTN_SQUARE).setToggle(UI.BEIGE);
			b.setForeground(new Sprite(player.color, standing.getTile("palette99_" + Building.Type.values()[i].name() + "_Large_face0").regions.get(0)));
			b.addListener(bl);
			b.setPayload(Building.Type.values()[i]);
			buyButtons[i] = b;
		}
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
		
		player = new Player("Player", 0);
		enemy = new Player("CPU", 1);
		
		Building myCity = new City(5, 7, player);
		player.setMainCity(myCity);
		world.addEntity(myCity);
		world.center(myCity);
		
		Building theirCity = new City(46, 23, enemy);
		enemy.setMainCity(theirCity);
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
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		Wargame.width = width;
		Wargame.height = height;
		
		initHud();
		clampScale();
		world.clampNewPosition();
		glViewport(0, 0, width, height);
		Matrix.orthoM(projMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, -100, 1000);
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
		Matrix.multiplyMM(viewProjMatrix, 0, projMatrix, 0, viewMatrix, 0);
		System.arraycopy(viewProjMatrix, 0, hudMatrix, 0, 16);
	}
	
	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		if (System.currentTimeMillis() - lastTimestamp >= 1000) {
			fps = frames;
			frames = 0;
			lastTimestamp = System.currentTimeMillis();
		}
		
		float timeStep = Math.min(1.0f / fps, 1 / 60f);
		GdxAI.getTimepiece().update(timeStep);
		
		if (vX != 0 && vY != 0) {
			final float stop = 0.00001f;
			
			world.move(vX / scale, vY / scale);
			if (vX != 0.0f) vX -= vX * 0.185f;
			vX = Math.abs(vX) < stop ? 0 : vX;
			if (vY != 0.0f) vY -= vY * 0.185f;
			vY = Math.abs(vY) < stop ? 0 : vY;
		}
		
		player.money += timeStep;
		enemy.money += timeStep;
		
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
		if (getPlaceBuilding() != null) {
			getPlaceBuilding().setColor(player.money >= getPlaceBuilding().getBuildCosts() && world.canBuildOn((int) getPlaceBuilding().getRealX(), (int) getPlaceBuilding().getRealZ()) ? HALFWHITE : HALFRED);
			spriteRenderer.render(getPlaceBuilding());
		}
		world.updatePos();
		
		spriteRenderer.end();
		
		glDisable(GL_DEPTH_TEST);
		
		spriteRenderer.begin(hudMatrix);
		textRenderer.renderText(-width / 2, height / 2 - 30, 0, 0.5f, "FPS: " + fps, spriteRenderer);
		textRenderer.renderText(-width / 2, height / 2 - 60, 0, 0.5f, "E: " + world.rEntities + " / " + (world.buildings.size() + world.units.size()), spriteRenderer);
		
		textRenderer.renderText(-width / 2, height / 2 - 100, 0, 0.5f, "Player: " + player.money, spriteRenderer);
		textRenderer.renderText(-width / 2, height / 2 - 130, 0, 0.5f, "CPU: " + enemy.money, spriteRenderer);
		
		textRenderer.setFont(1);
		textRenderer.renderText(-200, height / 2 - 80, 0, 1f, "$ " + Math.round(player.money), spriteRenderer);
		textRenderer.setFont(0);
		
		if (getPlaceBuilding() != null) {
			detailsPanel.render(spriteRenderer);
			getPlaceBuilding().renderDetails(detailsPanel, spriteRenderer, textRenderer);
		}
		
		for (Button b : buyButtons)
			b.render(spriteRenderer);
			
		spriteRenderer.end();
		frames++;
	}
	
	public void tryToPlaceBuilding(MotionEvent e, boolean single) {
		if (!hudEvents) {
			if (getPlaceBuilding() != null) {
				Vector2 pos = world.getMappedCoords(e.getX() - width / 2, height - e.getY() - height / 2);
				if (pos.x >= 0 && pos.y >= 0) {
					if (!single || (getPlaceBuilding().getRealX() == (int) pos.x && getPlaceBuilding().getRealZ() == (int) pos.y)) {
						if (world.canBuildOn((int) getPlaceBuilding().getRealX(), (int) getPlaceBuilding().getRealZ()) && player.money >= getPlaceBuilding().getBuildCosts()) {
							player.money -= getPlaceBuilding().getBuildCosts();
							world.addEntity(Building.create((int) getPlaceBuilding().getRealX(), (int) getPlaceBuilding().getRealZ(), player, getPlaceBuilding().getType()));
							getPlaceBuilding().setColor(HALFRED);
						}
					} else {
						getPlaceBuilding().setX(pos.x);
						getPlaceBuilding().setZ(pos.y);
					}
				}
			}
		}
	}
	
	public void clampScale() {
		float width = world.getWidth() * World.WIDTH / 2 + world.getDepth() * World.WIDTH / 2;
		float height = world.getWidth() * World.DEPTH / 2 + world.getDepth() * World.DEPTH / 2 + World.HEIGHT;
		scale = Math.max(Math.max(Wargame.width / width, Wargame.height / height), Math.min(7.5f, scale));
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
				for (Button b : buyButtons)
					if (b.onDown(e)) hudEvents = true;
				break;
			case MotionEvent.ACTION_UP:
				for (Button b : buyButtons)
					if (b.onUp(e)) hudEvents = false;
					
				break;
			case MotionEvent.ACTION_MOVE:
				
				float dx = x - prevX;
				float dy = y - prevY;
				
				if (e.getPointerCount() == prevNum && !hudEvents) {
					vX = dx;
					vY = dy;
				}
				break;
		}
		
		prevX = x;
		prevY = y;
		prevNum = e.getPointerCount();
		
		return true;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		tryToPlaceBuilding(e, true);
		
		return false;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		tryToPlaceBuilding(e, false);
		return false;
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		scale *= detector.getScaleFactor();
		clampScale();
		world.clampNewPosition();
		return true;
	}
	
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			glView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
	}
}
