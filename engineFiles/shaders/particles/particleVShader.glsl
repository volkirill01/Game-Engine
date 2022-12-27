#version 400 core

in vec2 position;

in mat4 modelViewMatrix;
in vec4 textureOffsets;
in float blendFactor;

out vec2 currentTextureCoords;
out vec2 nextTextureCoords;
out float blend;

uniform mat4 projectionMatrix;

uniform int numberOfRows;
uniform int numberOfColumns;

void main() {
	vec2 textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y;
	textureCoords.x /= numberOfRows;
	textureCoords.y /= numberOfColumns;
	currentTextureCoords = textureCoords + textureOffsets.xy;
	nextTextureCoords = textureCoords + textureOffsets.zw;

	blend = blendFactor;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position.x, -position.y, 0.0, 1.0);
}