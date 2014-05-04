
uniform mat4 proj;
uniform mat4 view;

attribute vec3 vertex;
attribute vec2 texCoord;

varying vec2 vTexCoord;

void main() {
	gl_PointSize = 10.0;
	vTexCoord = texCoord;
	gl_Position = proj * view * vec4(vertex, 1);
}
