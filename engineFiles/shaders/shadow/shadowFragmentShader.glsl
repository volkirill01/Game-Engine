#version 400 core

in vec2 textureCoords;

layout (location = 0) out vec4 out_Color;

uniform sampler2D modelTexture;//will use this next week

void main() {

	float alpha = texture(modelTexture, textureCoords).a;

//	if (alpha < 0.5)
//		discard;

	out_Color = vec4(1.0, 1.0, 1.0, alpha);
}