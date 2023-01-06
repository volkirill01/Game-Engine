#version 400 core

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube cubeMap;
uniform vec3 backgroundColor;
uniform vec3 fogColor;
uniform float fogDensity;

const float lowerFogLimit = 0.0;
const float upperFogLimit = 60.0;

void main() {
//    vec4 finalColor = texture(cubeMap, textureCoords);

    float factor = (textureCoords.y - lowerFogLimit) / (upperFogLimit - lowerFogLimit);
    factor = clamp(factor, 0.0, 1.0);
    if (fogDensity > 0)
        out_Color = mix(vec4(fogColor, 1.0), vec4(backgroundColor, 1.0), factor);
    else
        out_Color = vec4(backgroundColor, 1.0);
//        out_Color = mix(vec4(fogColor, 1.0), finalColor, factor);
//        out_Color = finalColor;
}