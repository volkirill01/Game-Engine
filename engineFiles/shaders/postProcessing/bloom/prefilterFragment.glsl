#version 400 core

//
// All components are in the range [0…1], including hue.
vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}
//

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

uniform float threshold;

void main() {
    vec4 color = texture(colourTexture, textureCoords);
    float brightness = rgb2hsv(color.rgb).b;

    if (brightness > threshold)
        out_Colour = color;
    else
        out_Colour = vec4(vec3(0.0), color.a); // TODO ADD SMOOTH THRESHOLD
}
