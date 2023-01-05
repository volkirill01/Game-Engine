#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform mat4 toShadowMapSpace;

uniform float fogDensity;
uniform float fogGradient;

uniform float shadowDistance;
const float transitionDistance = 10.0; // TODO LOAD SHMOOTHNESS OF TRANSITION UNIFORM

uniform vec3 lightPosition[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY

void main() {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRealitiveToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRealitiveToCamera;
    pass_textureCoords = textureCoordinates;

    shadowCoords = toShadowMapSpace * worldPosition;

    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
    for (int i = 0; i < 9; i++)
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;

    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length(positionRealitiveToCamera.xyz);

    if (fogDensity > 0) {
        visibility = exp(-pow((distance * fogDensity), fogGradient));
        visibility = clamp(visibility, 0.0, 1.0);
    } else {
        visibility = 1.0;
    }

    distance = distance - (shadowDistance - transitionDistance);
    distance = distance / transitionDistance;
    shadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);
}
