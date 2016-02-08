//#define LOWP lowp
precision mediump float;

uniform sampler2D uTex;

uniform sampler2D color0;
uniform sampler2D color1;
uniform sampler2D color2;
uniform sampler2D color3;
uniform sampler2D color4;
uniform sampler2D color5;
uniform sampler2D color6;
uniform sampler2D color7;
uniform sampler2D terrain;

varying vec2 vTex;
varying vec4 vCol;
varying vec4 vAdd;
varying float vPal;

void main() {
	vec4 col = vec4(1.0);
	vec4 c = texture2D(uTex, vTex);
	if(c.a == 0.0) discard;
	vec2 c99 = vec2(c.r, 0);
	
	if (vPal == -1.0) col = c;
	else if(vPal == 0.0) col = texture2D(color0, c99);
	else if(vPal == 1.0) col = texture2D(color1, c99);
	else if(vPal == 2.0) col = texture2D(color2, c99);
	else if(vPal == 3.0) col = texture2D(color3, c99);
	else if(vPal == 4.0) col = texture2D(color4, c99);
	else if(vPal == 5.0) col = texture2D(color5, c99);
	else if(vPal == 6.0) col = texture2D(color6, c99);
	else if(vPal == 7.0) col = texture2D(color7, c99);
	else if(vPal == 8.0) col = texture2D(terrain, c99);
	else if(vPal == 255.0) { // for debugging 
		//if(vTex.x < 0.01 || vTex.x > 0.99 || vTex.y <0.01 || vTex.y > 0.99) col = vCol;
		//else 
		col = vec4(0.0); 
	}
	gl_FragColor = col * vCol + vec4(vAdd.rgb,0);
}
