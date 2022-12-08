#version 400 core

in vec3 textureCoords;
layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_BrightColor;

uniform samplerCube cubeMap;
uniform vec3 fogColor;

const float lowerFogLimit = 0.0;
const float upperFogLimit = 30.0;

void main() {
    vec4 finalColor = texture(cubeMap, textureCoords);

    float factor = (textureCoords.y - lowerFogLimit) / (upperFogLimit - lowerFogLimit);
    factor = clamp(factor, 0.0, 1.0);
    out_Color = mix(vec4(fogColor, 1.0), finalColor, factor);
    out_BrightColor = vec4(0.0);
}