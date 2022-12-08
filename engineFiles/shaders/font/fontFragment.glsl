#version 400 core

in vec2 pass_textureCoords;

out vec4 out_colour;

uniform vec4 color;
uniform sampler2D fontAtlas;

uniform float width;
uniform float edge;

uniform vec4 borderColor;
uniform float borderWidth;
uniform float borderEdge;

uniform vec4 dropShadowColor;
uniform float dropShadowWidth;
uniform float dropShadowEdge;
uniform vec2 dropShadowOffset;

void main() {
    float fontDistance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
    float fontAlpha = 1.0 - smoothstep(width, width + edge, fontDistance);
    float overallAlpha = 0;

    float shadowDistance = 1.0 - texture(fontAtlas, pass_textureCoords + dropShadowOffset).a;
    float shadowAlpha = 1.0 - smoothstep(width + borderWidth + dropShadowWidth, width + borderWidth + dropShadowWidth + dropShadowEdge, shadowDistance);
    overallAlpha += fontAlpha + (1.0 - fontAlpha) * shadowAlpha * dropShadowColor.a;

    float borderDistance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
    float borderAlpha = 1.0 - smoothstep(width + borderWidth, width + borderWidth + borderEdge, borderDistance);
    overallAlpha += fontAlpha + (1.0 - fontAlpha) * borderAlpha * borderColor.a;

    overallAlpha *= color.a;

    vec3 borderAndDropShadowColor = mix(dropShadowColor.xyz, borderColor.xyz, borderAlpha);
    vec3 overallColor = mix(borderAndDropShadowColor.xyz, color.xyz, fontAlpha / shadowAlpha);

    out_colour = vec4(overallColor, overallAlpha);
}

//#version 400 core
//
//in vec2 pass_textureCoords;
//
//out vec4 out_colour;
//
//uniform vec4 color;
//uniform sampler2D fontAtlas;
//
//const float width = 0.5;
//const float edge = 0.1;
//
//const float borderWidth = 0.1;
//const float borderEdge = 0.1;
//const vec4 borderColor = vec4(0.2, 0.2, 0.2, 1);
//
//const float shadowWidth = 0.04;
//const float shadowEdge = 0.2;
//const vec4 shadowColor = vec4(0, 0, 0, 0.4);
//const vec2 dropShadowOffset = vec2(-0.001, -0.001);
//
//void main() {
//    float fontDistance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
//    float fontAlpha = 1.0 - smoothstep(width, width + edge, fontDistance);
//    float overallAlpha = 0;
//
//    float shadowDistance = 1.0 - texture(fontAtlas, pass_textureCoords + dropShadowOffset).a;
//    float shadowAlpha = 1.0 - smoothstep(width + borderWidth + shadowWidth, width + borderWidth + shadowWidth + shadowEdge, shadowDistance);
//    overallAlpha += fontAlpha + (1.0 - fontAlpha) * shadowAlpha * shadowColor.a;
//
//    float borderDistance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
//    float borderAlpha = 1.0 - smoothstep(width + borderWidth, width + borderWidth + borderEdge, borderDistance);
//    overallAlpha += fontAlpha + (1.0 - fontAlpha) * borderAlpha * borderColor.a;
//
//    overallAlpha *= color.a;
//
//    vec3 borderAndDropShadowColor = mix(shadowColor.xyz, borderColor.xyz, borderAlpha);
//    vec3 overallColor = mix(borderAndDropShadowColor.xyz, color.xyz, fontAlpha / shadowAlpha);
//
//    out_colour = vec4(overallColor, overallAlpha);
//}

//#version 400 core
//
//in vec2 pass_textureCoords;
//
//out vec4 out_colour;
//
//uniform vec3 color;
//uniform sampler2D fontAtlas;
//
//const float width = 0.5;
//const float edge = 0.1;
//
//const float borderWidth = 0.7;
//const float borderEdge = 0.1;
//const vec3 borderColor = vec3(1, 0, 0);
//
//void main() {
//
//    float distance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
//    float alpha = 1.0 - smoothstep(width, width + edge, distance);
//
//    float borderDistance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
//    float borderAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, borderDistance);
//
//    float overallAlpha = alpha + (1.0 - alpha) * borderAlpha;
//    vec3 overallColor = mix(borderColor, color, alpha / overallAlpha);
//
//    out_colour = vec4(overallColor, overallAlpha);
//}