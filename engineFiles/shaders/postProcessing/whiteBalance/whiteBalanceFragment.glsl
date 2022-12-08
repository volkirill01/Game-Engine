#version 400 core

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

uniform float temperature;
uniform float tint;

vec3 unityWhiteBalance(vec3 In) {
    // Range ~[-1.67;1.67] works best
    float t1 = temperature * 10 / 6;
    float t2 = tint * 10 / 6;

    // Get the CIE xy chromaticity of the reference white point.
    // Note: 0.31271 = x value on the D65 white point
    float x = 0.31271 - t1 * (t1 < 0 ? 0.1 : 0.05);
    float standardIlluminantY = 2.87 * x - 3 * x * x - 0.27509507;
    float y = standardIlluminantY + t2 * 0.05;

    // Calculate the coefficients in the LMS space.
    vec3 w1 = vec3(0.949237, 1.03542, 1.08728); // D65 white point

    // CIExyToLMS
    float Y = 1;
    float X = Y * x / y;
    float Z = Y * (1 - x - y) / y;
    float L = 0.7328 * X + 0.4296 * Y - 0.1624 * Z;
    float M = -0.7036 * X + 1.6975 * Y + 0.0061 * Z;
    float S = 0.0030 * X + 0.0136 * Y + 0.9834 * Z;
    vec3 w2 = vec3(L, M, S);

    vec3 balance = vec3(w1.x / w2.x, w1.y / w2.y, w1.z / w2.z);

    mat3 LIN_2_LMS_MAT = mat3(
        vec3(3.90405e-1, 5.49941e-1, 8.92632e-3),
        vec3(7.08416e-2, 9.63172e-1, 1.35775e-3),
        vec3(2.31082e-2, 1.28021e-1, 9.36245e-1)
    );

    mat3 LMS_2_LIN_MAT = mat3(
        vec3(2.85847e+0, -1.62879e+0, -2.48910e-2),
        vec3(-2.10182e-1,  1.15820e+0,  3.24281e-4),
        vec3(-4.18120e-2, -1.18169e-1,  1.06867e+0)
    );

    vec3 lms = LIN_2_LMS_MAT * In;
    lms *= balance;
    return LMS_2_LIN_MAT * lms;
}

void main() {
    out_Colour = texture(colourTexture, textureCoords);
    out_Colour = vec4(unityWhiteBalance(out_Colour.rgb), out_Colour.a);
}
