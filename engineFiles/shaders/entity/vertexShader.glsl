#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
out vec3 toCameraVector;
out vec3 reflectionVector; // TODO FIX REFLECTIONS
out float visibility;

uniform vec2 tiling;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float fogDensity;
uniform float fogGradient;

uniform vec3 lightPosition[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform vec3 attenuation[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform vec3 lightRotation[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY

uniform float useFakeLighting;

uniform float numberOfRows;
uniform float numberOfColumns;
uniform vec2 textureOffset;

void main() {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRealitiveToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRealitiveToCamera;
    pass_textureCoords = ((textureCoordinates * tiling) / vec2(numberOfRows, numberOfColumns) + textureOffset);

    vec3 actualNormal = normal;
    if (useFakeLighting > 0.5) {
        actualNormal = vec3(0.0, 1.0, 0.0);
    }

    surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
    for (int i = 0; i < 4; i++) {
        if (attenuation[i] == vec3(1, 0, 0))
            toLightVector[i] = lightRotation[i];
        else
            toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }
    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    // reflectionVector
    vec3 viewVector = normalize(toCameraVector);
    reflectionVector = reflect(viewVector, normalize(actualNormal));
    // reflectionVector

    float distance = length(positionRealitiveToCamera.xyz);
    if (fogDensity > 0) {
        visibility = exp(-pow((distance * fogDensity), fogGradient));
        visibility = clamp(visibility, 0.0, 1.0);
    } else {
        visibility = 1.0;
    }
}
