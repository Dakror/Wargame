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

package de.dakror.wargame.util;

import com.badlogic.gdx.ai.Logger;

import android.util.Log;

/**
 * @author Maximilian Stark | Dakror
 */
public class AndroidLogger implements Logger {
	@Override
	public void debug(String tag, String message, Throwable exception) {
		Log.d(tag, message, exception);
	}
	
	@Override
	public void debug(String tag, String message) {
		Log.d(tag, message);
	}
	
	@Override
	public void info(String tag, String message) {
		Log.i(tag, message);
	}
	
	@Override
	public void info(String tag, String message, Throwable exception) {
		Log.i(tag, message, exception);
	}
	
	@Override
	public void error(String tag, String message) {
		Log.e(tag, message);
	}
	
	@Override
	public void error(String tag, String message, Throwable exception) {
		Log.e(tag, message, exception);
	}
}
