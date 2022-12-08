#version 400 core

//
// All components are in the range [0…1], including hue.
vec3 rgb2hsv(vec3 c) {
	vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
	vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
	vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

	float d = q.x - min(q.w, q.y);
	float e = 1.0e-10;
	return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
//

in vec2 textureCoords;

in vec2 currentTextureCoords;
in vec2 nextTextureCoords;

in float blend;

uniform sampler2D particleTexture;

layout (location = 0) out vec4 out_Color;

uniform float isAdditive;

uniform sampler2D emissionMap;
uniform float emissionIntensity;

uniform vec3 fogColor;

void main() {
	vec4 currentFrameColor = texture(particleTexture, currentTextureCoords);
	vec4 nextFrameColor = texture(particleTexture, nextTextureCoords);

	out_Color = mix(currentFrameColor, nextFrameColor, blend);

	if (isAdditive > 0.5)
		out_Color += vec4(out_Color.rgb * 1.5, 0);
	else {
		vec3 mapInfo = texture(emissionMap, currentTextureCoords).rgb;
		float mapBrightness = rgb2hsv(mapInfo).b;
		if (mapBrightness > 0)
			out_Color += vec4(mapInfo * emissionIntensity, 0);
	}
}