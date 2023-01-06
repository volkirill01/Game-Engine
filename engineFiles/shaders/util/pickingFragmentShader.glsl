#version 400 core

uniform int entityId;

out vec4 out_Color;

void main() {
    out_Color = vec4(entityId, 0.0, 0.0, 1.0);
}
