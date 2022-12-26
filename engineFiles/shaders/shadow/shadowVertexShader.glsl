#version 400 core

in vec3 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 mvpMatrix;

void main() {
	gl_Position = mvpMatrix * vec4(position, 1.0);
	pass_textureCoords = textureCoords;
}