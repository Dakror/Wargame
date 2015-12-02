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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import de.dakror.wargame.Building.Type;

/**
 * @author Maximilian Stark | Dakror
 */
public class Wargame extends Activity {
	public class WargameRenderer implements GLSurfaceView.Renderer, OnTouchListener, GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
		GestureDetector gestureDetector;
		ScaleGestureDetector scaleGestureDetector;
		
		SpriteRenderer spriteRenderer;
		TextRenderer textRenderer;
		public float[] mvMatrix, projMatrix, matrix, hudMatrix;
		
		int frames, fps = 60;
		long lastTimestamp;
		long lastFrame;
		
		float scale = 5;
		float ratio;
		
		float prevX, prevY, prevNum;
		
		float vX, vY;
		
		World map;
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent e) {
			gestureDetector.onTouchEvent(e);
			scaleGestureDetector.onTouchEvent(e);
			float x = e.getX();
			float y = e.getY();
			
			switch (e.getAction()) {
				case MotionEvent.ACTION_MOVE:
					
					float dx = x - prevX;
					float dy = y - prevY;
					
					if (e.getPointerCount() == prevNum) map.move(dx / scale * 60f / Math.max(fps, 25), dy / scale * 60f / Math.max(fps, 25));
			}
			
			prevX = x;
			prevY = y;
			prevNum = e.getPointerCount();
			
			return true;
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
			spriteRenderer = new SpriteRenderer();
			textRenderer = new TextRenderer("packed/font/copperplate.fnt");
			matrix = new float[16];
			mvMatrix = new float[16];
			projMatrix = new float[16];
			hudMatrix = new float[16];
			
			terrain = new TextureAtlas("packed/terrain.atlas");
			standing = new TextureAtlas("packed/standing/standing.atlas");
			//			animation = new TextureAtlas("packed/animation.atlas");
			//			System.out.println(animation.tiles.size());
			//			System.out.println(animation.tr);
			
			map = new World("maps/lake.map");
			
			Building b = new Building(5, 2, 7, 0, Type.City);
			map.addEntity(b);
			map.center(b);
			//			for (int i = 0; i < 8; i++)
			//				for (Type t : Type.values())
			//					map.addEntity(new Building(t.ordinal(), 2, i * 2, i, t));
			//					
			glClearColor(130 / 255f, 236 / 255f, 255 / 255f, 1);
		}
		
		@Override
		public void onSurfaceChanged(GL10 gl10, int width, int height) {
			Wargame.width = width;
			Wargame.height = height;
			glViewport(0, 0, width, height);
			ratio = (float) width / height;
			//			System.out.println("ratio: " + ratio);
			Matrix.orthoM(projMatrix, 0, -ratio, ratio, -1, 1, -10, 100);
			//			Matrix.rotateM(projMatrix, 0, -45, 0, 1, 0);
			//			Matrix.rotateM(projMatrix, 0, 25, 1, 0, 0);
			Matrix.orthoM(hudMatrix, 0, 0, ratio, 0, 1, 0, 10);
			
			//			printMatrix(projMatrix);
			
			//Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1, 1, 1, 1000);
		}
		
		@Override
		public void onDrawFrame(GL10 gl) {
			if (System.currentTimeMillis() - lastTimestamp >= 1000) {
				//				System.out.println("FPS: " + frames);
				fps = frames;
				frames = 0;
				lastTimestamp = System.currentTimeMillis();
			}
			
			float timeStep = Math.min(1.0f / fps, 1 / 60f);
			
			map.update(timeStep);
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			glEnable(GL_DEPTH_TEST);
			
			Matrix.setIdentityM(mvMatrix, 0);
			//			Matrix.setIdentityM(matrix, 0);
			
			Matrix.scaleM(mvMatrix, 0, 1f / 1024 * scale, 1f / 1024 * scale, 1f / 1024 * scale);
			
			Matrix.multiplyMM(matrix, 0, projMatrix, 0, mvMatrix, 0);
			
			spriteRenderer.begin(matrix);
			map.render(spriteRenderer);
			spriteRenderer.end();
			
			spriteRenderer.begin(hudMatrix);
			textRenderer.renderText(0f, 0.97f, 0, 0.00125f, "FPS: " + fps, spriteRenderer);
			textRenderer.renderText(0f, 0.93f, 0, 0.00125f, "R: " + map.rendered + " / " + map.all, spriteRenderer);
			textRenderer.renderText(0f, 0.89f, 0, 0.00125f, "E: " + map.rEntities + " / " + map.entities.size(), spriteRenderer);
			spriteRenderer.end();
			
			frames++;
		}
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scale *= detector.getScaleFactor();
			scale = Math.min(15, Math.max(0.3f, scale));
			return true;
		}
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}
		
		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {}
		
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {}
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			System.out.println(e);
			return false;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return false;
		}
	}
	
	GLSurfaceView glView;
	WargameRenderer renderer;
	public static Wargame instance;
	
	public static int width, height;
	public static TextureAtlas terrain, standing, animation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		instance = this;
		renderer = new WargameRenderer();
		glView = new GLSurfaceView(this);
		glView.setEGLContextClientVersion(2);
		glView.setRenderer(renderer);
		glView.setOnTouchListener(renderer);
		renderer.gestureDetector = new GestureDetector(this, renderer);
		renderer.scaleGestureDetector = new ScaleGestureDetector(this, renderer);
		setContentView(glView);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			glView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
	}
	
	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		glView.onResume();
	}
	
	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		glView.onPause();
	}
	
	public int createProgram(String vertexShaderFile, String fragmentShaderFile) {
		try {
			int vs = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vs, read(getAssets().open(vertexShaderFile)));
			glCompileShader(vs);
			String vsError = glGetShaderInfoLog(vs);
			if (vsError != null && vsError.length() > 0) Log.e("createProgram", vertexShaderFile + ": " + vsError);
			
			int fs = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fs, read(getAssets().open(fragmentShaderFile)));
			glCompileShader(fs);
			String fsError = glGetShaderInfoLog(fs);
			if (fsError != null && fsError.length() > 0) Log.e("createProgram", fragmentShaderFile + ": " + fsError);
			
			int program = glCreateProgram();
			glAttachShader(program, vs);
			glAttachShader(program, fs);
			glLinkProgram(program);
			
			String pError = glGetProgramInfoLog(program);
			if (pError != null && pError.length() > 0) {
				Log.e("createProgram", "LNK: " + pError);
				finish();
			}
			
			return program;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int loadTexture(String textureFile) {
		return loadTexture(textureFile, GL_NEAREST, GL_NEAREST);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public int loadTexture(String textureFile, int filterMin, int filterMag) {
		int[] id = new int[1];
		try {
			glGenTextures(1, id, 0);
			Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(textureFile));
			glBindTexture(GL_TEXTURE_2D, id[0]);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMin);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMag);
			
			GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, 0);
			
			bitmap.recycle();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return id[0];
	}
	
	public static String read(InputStream is) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		while ((line = br.readLine()) != null) {
			content.append(line);
			content.append("\r\n");
		}
		br.close();
		return content.toString();
	}
	
	public static void printMatrix(float[] m) {
		int len = 8;
		for (int i = 0; i < 4; i++) {
			System.out.println(l(m[i], len) + " " + l(m[i + 4], len) + " " + l(m[i + 8], len) + " " + l(m[i + 12], len));
		}
	}
	
	static String l(float f, int len) {
		String s = "" + f;
		while (s.length() < len)
			s = f % 1 == 0 ? " " + s : s + "0";
		return s;
	}
}
