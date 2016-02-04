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

package de.dakror.wargame.util;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public abstract class ActivityStub extends Activity implements GLSurfaceView.Renderer, OnTouchListener, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		return false;
	}
	
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return false;
	}
	
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {}
	
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
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent e) {}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
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
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
