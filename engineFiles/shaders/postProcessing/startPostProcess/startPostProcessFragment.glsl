#version 400 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

uniform float gamma;
uniform float exposure;

const float startBrightness = 0.04;
const float startContrast = 1.235;
const float startSaturation = 1.2;

void main() {
    vec4 color = texture(colourTexture, textureCoords);
    vec3 toneMapped = vec3(1.0) - exp(-color.rgb * exposure);

    out_Colour = vec4(pow(toneMapped, vec3(1.0 / gamma)), color.a);

    // Apply default postEffects
    vec3 _color = out_Colour.rgb;
    vec3 colorContrasted = _color * startContrast;
    vec3 bright = colorContrasted + vec3(startBrightness);

    vec3 gray = vec3(dot(vec3(0.2126, 0.7152, 0.0722), bright));
    vec3 result = vec3(mix(bright, gray, 1 - startSaturation));

    float brightness = (result.r * 0.2126) + (result.g * 0.7152) + (result.b * 0.0722);

    out_Colour = vec4(result, out_Colour.a);
}
