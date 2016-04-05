/*******************************************************************************
 * Copyright 2016 Maximilian Stark | Dakror <mail@dakror.de>
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

package de.dakror.wargameV2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import de.dakror.wargameV2.graphics.SpriteRenderer;
import de.dakror.wargameV2.graphics.TextRenderer;
import de.dakror.wargameV2.graphics.TextureAtlas;
import de.dakror.wargameV2.util.ActivityStub;
import de.dakror.wargameV2.world.World;

/**
 * @author Maximilian Stark | Dakror
 */
public class Wargame extends ActivityStub {
	public static Wargame instance;
	public static TextureAtlas animation, standing, terrain, ui;
	
	public float[] viewMatrix, projMatrix, hudMatrix, viewProjMatrix;
	
	SpriteRenderer spriteRenderer;
	TextRenderer textRenderer;
	
	GestureDetector gestureDetector;
	ScaleGestureDetector scaleGestureDetector;
	GLSurfaceView glView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		glView = new GLSurfaceView(this);
		glView.setEGLContextClientVersion(2);
		// glView.setEGLConfigChooser(new MultisampleConfigChooser());
		glView.setRenderer(this);
		glView.setOnTouchListener(this);
		gestureDetector = new GestureDetector(this, this);
		gestureDetector.setOnDoubleTapListener(this);
		scaleGestureDetector = new ScaleGestureDetector(this, this);
		setContentView(glView);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		spriteRenderer = new SpriteRenderer();
		textRenderer = new TextRenderer("packed/font/copperplate.fnt", "packed/font/bling.fnt");
		viewMatrix = new float[16];
		projMatrix = new float[16];
		hudMatrix = new float[16];
		viewProjMatrix = new float[16];
		
		terrain = new TextureAtlas("packed/terrain.atlas");
		standing = new TextureAtlas("packed/standing/standing.atlas");
		ui = new TextureAtlas("packed/ui.atlas");
		// animation = new TextureAtlas("packed/animation.atlas");
		
		world = new World("maps/lake.map");
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {}
	
	@Override
	public void onDrawFrame(GL10 gl) {}
}
