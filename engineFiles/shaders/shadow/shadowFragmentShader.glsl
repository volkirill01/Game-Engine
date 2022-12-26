#version 400 core

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D modelTexture;

void main() {

	float alpha = texture(modelTexture, pass_textureCoords).a;

	if (alpha < 0.5)
		discard;

	out_Color = vec4(1.0, 1.0, 1.0, alpha);
}