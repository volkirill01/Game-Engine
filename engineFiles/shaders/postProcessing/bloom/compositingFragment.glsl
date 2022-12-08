#version 400 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;
uniform sampler2D bloomTexture;

uniform float bloomIntensity;

// ----------------------------------------------------------------------------------------
// Compose
vec4 Compose(vec4 mainColor, vec3 bloomColor) {
    return vec4((mainColor.rgb + (bloomColor * bloomIntensity)), mainColor.a);
}

// ----------------------------------------------------------------------------------------
// Main
void main() {
    vec4 color = texture(colourTexture, textureCoords);
    vec3 bloomColor = texture(bloomTexture, textureCoords).rgb;
    out_Colour = Compose(color, bloomColor);
}