#version 400 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

uniform float targetWidth;
uniform float targetHeight;

uniform float isSquared;
uniform float isRounded;
uniform vec2 center;
uniform float intensity;
uniform float radius;
uniform float softness;

vec4 applyVignette(vec4 color) {
    vec2 uv = (gl_FragCoord.xy / vec2(targetWidth, targetHeight)) - center;
    float dist = 0;

    if (isRounded > 0.5)
        dist = length(uv * vec2(targetWidth / targetHeight, 1.0));
    else
        dist = length(uv);

    float vignette = smoothstep(radius, radius - softness, dist);

    color.rgb = color.rgb - ((1.0 - vignette) * intensity);

    return color;
}

vec4 applySquaredVignette(vec4 color) {
    vec2 uv = textureCoords.xy;

    uv *= 1.0 - uv.yx;

    float vignette = uv.x * uv.y * radius; // multiply with sth for intensity

    vignette = pow(vignette, softness); // change pow for modifying the extend of the  vignette

    color.rgb = color.rgb - ((1 - vignette) * intensity);

    return color;
}

void main() {
    out_Colour = texture2D(colourTexture, textureCoords);

    if (isSquared > 0.5)
        out_Colour = applySquaredVignette(out_Colour);
    else
        out_Colour = applyVignette(out_Colour);
}
