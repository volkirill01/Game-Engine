#version 400 core

in vec2 position;

out vec2 blurTextureCoords[11];

uniform float targetWidth;
uniform float targetHeight;

uniform float isVertical;

void main() {
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTexCoords = position * 0.5 + 0.5;

	if (isVertical >= 0.5) {
		float pixelSize = 1.0 / targetHeight;

		for (int i = -5; i <= 5; i++)
			blurTextureCoords[i + 5] = centerTexCoords + vec2(0.0, pixelSize * i);
	} else {
		float pixelSize = 1.0 / targetWidth;

		for (int i = -5; i <= 5; i++)
			blurTextureCoords[i + 5] = centerTexCoords + vec2(pixelSize * i, 0.0);
	}
}