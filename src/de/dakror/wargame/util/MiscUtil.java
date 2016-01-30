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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Maximilian Stark | Dakror
 *
 */
public class MiscUtil {
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
			System.out.println(lengthenFloat(m[i], len) + " " + lengthenFloat(m[i + 4], len) + " " + lengthenFloat(m[i + 8], len) + " " + lengthenFloat(m[i + 12], len));
		}
	}
	
	public static String lengthenFloat(float f, int len) {
		String s = "" + f;
		while (s.length() < len)
			s = f % 1 == 0 ? " " + s : s + "0";
		return s;
	}
}
