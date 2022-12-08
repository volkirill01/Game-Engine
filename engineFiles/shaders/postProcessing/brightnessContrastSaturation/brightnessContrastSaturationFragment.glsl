#version 400 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

uniform float brightness;
uniform float contrast;
uniform float saturation;

void main() {
    out_Colour = texture2D(colourTexture, textureCoords);

    vec3 color = out_Colour.rgb;
    vec3 colorContrasted = color * contrast;
    vec3 bright = colorContrasted + vec3(brightness);

    vec3 gray = vec3(dot(vec3(0.2126, 0.7152, 0.0722), bright));
    vec3 result = vec3(mix(bright, gray, 1 - saturation));

    out_Colour = vec4(result, out_Colour.a);
}
