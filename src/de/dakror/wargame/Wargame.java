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
import de.dakror.wargame.World.CanBuildResult;
import de.dakror.wargame.entity.building.Building;
import de.dakror.wargame.entity.building.Building.Buildings;
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
	
	Building placeBuilding, selectedBuilding;
	long lastFrame;
	long lastTimestamp;
	float prevX, prevY, prevNum;
	float vX, vY;
	int frames, fps = 60;
	
	public static Player player, enemy;
	
	boolean hudEvents = false;
	
	MotionEvent lastTouchEvent, lastSingleTap, lastDoubleTap;
	
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
		buyButtons = new Button[Building.Buildings.values().length];
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
					Building pb = Building.create(-5000, 0, player, (Buildings) b.getPayload());
					pb.setColor(HALFWHITE);
					pb.setWorld(world);
					placeBuilding = pb;
				} else placeBuilding = null;
			}
		};
		
		for (int i = 0; i < buyButtons.length; i++)
			buyButtons[i] = new Button(buyButtons.length - i, new Sprite(player.color, standing.getTile("palette99_" + Buildings.values()[i].name() + "_Large_face0").regions.get(0)), bl, Building.Buildings.values()[i], true);
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
		
		player = new Player("Player", true, 0);
		enemy = new Player("CPU", false, 1);
		
		Building myCity = new City(2, 3, player);
		player.setMainCity(myCity);
		world.addEntity(myCity);
		world.center(myCity);
		
		Building theirCity = new City(18, 19, enemy);
		enemy.setMainCity(theirCity);
		world.addEntity(theirCity);
		//		for (int i = 0; i < 15; i++) {
		//			Unit u = new Unit(2 + i / 5f, 3 - (i % 2) * 0.5f, 0, Unit.Type.Infantry);
		//			world.addEntity(u);
		//		}
		
		//		Unit v = new Unit(0, 2, player, Units.Infantry);
		
		//		SteeringBehavior<Vector2> sb = new Pursue<Vector2>(u, v, 0.3f)/*.setTarget(new WorldLocation(new Vector3(2, 2, 0), 0))/*.setArrivalTolerance(u.getZeroLinearSpeedThreshold()).setDecelerationRadius(1f)*/;
		//		u.setSteeringBehavior(sb);
		//		v.setSteeringBehavior(new Arrive<Vector2>(v).setTarget(new WorldLocation(new Vector2(6, 6), 0)).setArrivalTolerance(u.getZeroLinearSpeedThreshold()).setDecelerationRadius(1f));
		//		world.addEntity(v);
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
	public void onDrawFrame(GL10 gl) {
		if (System.currentTimeMillis() - lastTimestamp >= 1000) {
			fps = frames;
			frames = 0;
			lastTimestamp = System.currentTimeMillis();
		}
		
		handleInput();
		
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
		
		world.render(spriteRenderer, null);
		
		CanBuildResult cbr = null;
		if (placeBuilding != null) {
			cbr = world.canBuildOn((int) placeBuilding.getRealX(), (int) placeBuilding.getRealZ(), player);
			placeBuilding.setColor(player.money >= placeBuilding.getBuildCosts() && cbr.result ? HALFWHITE : HALFRED);
			spriteRenderer.render(placeBuilding);
		}
		world.updatePos();
		
		spriteRenderer.end();
		
		glDisable(GL_DEPTH_TEST);
		
		spriteRenderer.begin(hudMatrix);
		textRenderer.renderText(-width / 2, height / 2 - 30, 0, 0.5f, "FPS: " + fps, spriteRenderer);
		textRenderer.renderText(-width / 2, height / 2 - 60, 0, 0.5f, "E: " + world.rEntities + " / " + (world.buildings.size() + world.units.size()), spriteRenderer);
		
		textRenderer.renderText(-width / 2, height / 2 - 100, 0, 0.5f, "CPU: " + (int) Math.floor(enemy.money), spriteRenderer);
		
		textRenderer.setFont(1);
		textRenderer.renderText(-200, height / 2 - 80, 0, 1f, "$ " + (int) Math.floor(player.money), spriteRenderer);
		textRenderer.setFont(0);
		
		if (placeBuilding != null) {
			detailsPanel.render(spriteRenderer, null);
			placeBuilding.renderDetails(detailsPanel, spriteRenderer, textRenderer);
			if ((!cbr.result || player.money < placeBuilding.getBuildCosts()) && placeBuilding.getRealX() >= 0) {
				//@off
				String[] reason = (
						cbr.reason == 3 ? 
								new String[]{ "Too far away from", "nearest own City." } : 
								(cbr.reason == 2 ? 
										new String[]{ "Occupied by", "existing building." } :
										cbr.reason == 1 ? 
												new String[]{ "Can't place on", "non-solid ground." } : 
													new String[]{ "Not enough money." }));
				//@on
				for (int i = 0; i < reason.length; i++)
					// (x1 + x2) / 2
					textRenderer.renderTextCentered((-width / 2 + detailsPanel.getWidth() + width / 2 - buyButtons.length * UI.BTN_SQUARE_WIDTH + 10) / 2, -height / 2 + 80 - i * 30, 0, 0.6f, Color.RED, reason[i], spriteRenderer);
			}
		} else if (selectedBuilding != null) {
			selectedBuilding.renderContextMenu(spriteRenderer, textRenderer);
		}
		
		if (selectedBuilding == null) {
			for (Button b : buyButtons)
				b.render(spriteRenderer, textRenderer);
		}
		
		spriteRenderer.end();
		frames++;
	}
	
	public void handleInput() {
		if (lastTouchEvent != null) {
			gestureDetector.onTouchEvent(lastTouchEvent);
			scaleGestureDetector.onTouchEvent(lastTouchEvent);
		}
		
		if (lastSingleTap != null) {
			if (!tryToPlaceBuilding(lastSingleTap, true) && placeBuilding == null) {
				if (!hudEvents) {
					Vector2 pos = world.getMappedCoords(lastSingleTap.getX() - width / 2, height - lastSingleTap.getY() - height / 2);
					Building b = world.getBuildingAt((int) pos.x, (int) pos.y, null);
					
					if (selectedBuilding != null) selectedBuilding.onDeselect();
					if (b != null) b.onSelect();
					selectedBuilding = b;
				}
			} else {
				if (selectedBuilding != null) selectedBuilding.onDeselect();
				selectedBuilding = null;
			}
			lastSingleTap = null;
		}
		
		if (lastDoubleTap != null) {
			tryToPlaceBuilding(lastDoubleTap, false);
			lastDoubleTap = null;
		}
		
		if (lastTouchEvent != null) {
			float x = lastTouchEvent.getX();
			float y = lastTouchEvent.getY();
			
			switch (lastTouchEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (selectedBuilding == null) {
						for (Button b : buyButtons)
							if (b.onDown(lastTouchEvent)) hudEvents = true;
					}
					if (selectedBuilding != null && selectedBuilding.onDown(lastTouchEvent)) hudEvents = true;
					break;
				case MotionEvent.ACTION_UP:
					if (selectedBuilding == null) {
						for (Button b : buyButtons)
							if (b.onUp(lastTouchEvent)) hudEvents = false;
					}
					if (selectedBuilding != null && selectedBuilding.onUp(lastTouchEvent)) hudEvents = false;
					break;
				case MotionEvent.ACTION_MOVE:
					
					float dx = x - prevX;
					float dy = y - prevY;
					
					if (lastTouchEvent.getPointerCount() == prevNum && !hudEvents) {
						vX = dx;
						vY = dy;
					}
					break;
			}
			
			prevX = x;
			prevY = y;
			prevNum = lastTouchEvent.getPointerCount();
			lastTouchEvent = null;
		}
	}
	
	public boolean tryToPlaceBuilding(MotionEvent e, boolean single) {
		if (!hudEvents) {
			if (placeBuilding != null) {
				Vector2 pos = world.getMappedCoords(e.getX() - width / 2, height - e.getY() - height / 2);
				if (pos.x >= 0 && pos.y >= 0 && pos.x < world.getWidth() && pos.y < world.getDepth()) {
					if (!single || (placeBuilding.getRealX() == (int) pos.x && placeBuilding.getRealZ() == (int) pos.y)) {
						if (world.canBuildOn((int) placeBuilding.getRealX(), (int) placeBuilding.getRealZ(), player).result && player.money >= placeBuilding.getBuildCosts()) {
							player.money -= placeBuilding.getBuildCosts();
							world.addEntity(Building.create((int) placeBuilding.getRealX(), (int) placeBuilding.getRealZ(), player, placeBuilding.getType()));
							placeBuilding.setColor(HALFRED);
							return true;
						}
					} else {
						placeBuilding.setX(pos.x);
						placeBuilding.setZ(pos.y);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public void clampScale() {
		float width = world.getWidth() * World.WIDTH / 2 + world.getDepth() * World.WIDTH / 2;
		float height = world.getWidth() * World.DEPTH / 2 + world.getDepth() * World.DEPTH / 2 + World.HEIGHT;
		scale = Math.max(Math.max(Wargame.width / width, Wargame.height / height), Math.min(7.5f, scale));
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		lastTouchEvent = e;
		return true;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		lastSingleTap = e;
		return true;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		lastDoubleTap = e;
		return true;
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
	
	@Override
	public void onLongPress(MotionEvent e) {
		player.money += 1000;
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
