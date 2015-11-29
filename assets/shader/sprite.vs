attribute vec3 aPos;
attribute vec2 aTex;
attribute vec4 aCol;
attribute float aPal;

uniform mat4 uMat;

varying vec4 vCol;
varying float vPal;
varying vec2 vTex;

void main() {
	vTex = aTex;
	vCol = aCol;
	vPal = aPal;
	gl_Position = uMat * vec4(aPos, 1.0);
}
