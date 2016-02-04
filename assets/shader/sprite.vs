attribute vec3 aPos;
attribute vec2 aTex;
attribute vec4 aCol;
attribute float aPal;
attribute vec4 aOff;
attribute vec4 aAdd;

uniform mat4 uMat;

varying vec4 vCol;
varying vec4 vAdd;
varying float vPal;
varying vec2 vTex;

void main() {
	vTex = aTex;
	vAdd = aAdd;
	vCol = aCol;
	vPal = aPal;
	gl_Position = uMat * vec4(aPos, 1.0);
}
