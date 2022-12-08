#version 400 core

in vec2 position;

out vec2 texCoord;

void main()
{
    gl_Position = vec4(position, 0.0, 1.0);
    texCoord = position * 0.5 + 0.5;
}
