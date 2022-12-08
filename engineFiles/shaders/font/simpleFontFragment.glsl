#version 400 core

in vec2 pass_textureCoords;

out vec4 out_colour;

uniform vec3 color;
uniform sampler2D fontAtlas;

void main() {
//    out_colour = vec4(color.xyz, texture(fontAtlas, pass_textureCoords).a * color.a); // using color alpha
    out_colour = vec4(color, texture(fontAtlas, pass_textureCoords).a);
//    out_colour = texture(fontAtlas, pass_textureCoords); // test for debugging
}