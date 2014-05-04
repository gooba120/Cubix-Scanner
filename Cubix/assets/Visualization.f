
uniform sampler2D tex;
uniform mediump float useTexture;

varying mediump vec2 vTexCoord;

void main() {
	gl_FragColor = 
			useTexture*texture2D(tex, vTexCoord)
		 +	(1.0-useTexture)*vec4(1, 0, 1, 1);
}
