#version 400 core

in vec3 position;
in vec2 textureCoordinates;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRealitiveToCamera = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRealitiveToCamera;
}
