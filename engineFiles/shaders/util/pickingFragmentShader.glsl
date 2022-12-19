#version 400 core

uniform int entityId;

out vec4 out_Color;

void main() {
    out_Color = vec4(entityId * 100, entityId, entityId, 1.0);
}
