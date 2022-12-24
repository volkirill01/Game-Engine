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

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
in vec3 toCameraVector;
in vec3 reflectionVector;
in float visibility;

layout (location = 0) out vec4 out_Color;

uniform sampler2D textureSampler;
uniform samplerCube enviromentMap;

uniform sampler2D specularMap;
uniform float specularIntensity;

uniform sampler2D emissionMap;
uniform float emissionIntensity;
uniform float useAlbedoEmission;

uniform vec3 lightColor[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform float lightIntensity[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform vec3 attenuation[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY

uniform vec3 color;

uniform vec3 fogColor;
uniform vec3 ambientLightColor;
uniform float ambientLightIntensity;

uniform float shineDamper;

uniform float alphaClip;

void main() {

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < 4; i++) {
        float distance = length(toLightVector[i]);
        float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);

        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse = totalDiffuse + ((brightness * lightColor[i] * lightIntensity[i]) / attenuationFactor);
        totalSpecular = totalSpecular + ((dampedFactor * specularIntensity * lightColor[i] * lightIntensity[i]) / attenuationFactor);
    }
    totalDiffuse = max(totalDiffuse, ambientLightIntensity);

    vec4 textureColor = texture(textureSampler, pass_textureCoords);
    if (textureColor.a < alphaClip) {
        discard;
    }

    if (specularIntensity > 0) {
        vec4 mapInfo = texture(specularMap, pass_textureCoords);
        float mapBrightness = rgb2hsv(mapInfo.rgb).b * specularIntensity;
        totalSpecular *= mapBrightness;
    }

    out_Color = vec4(totalDiffuse * ambientLightColor, 1.0) * vec4(textureColor.xyz * color, 1.0) + vec4(totalSpecular, 1.0);

    if (emissionIntensity > 0) {
        vec4 mapInfo = texture(emissionMap, pass_textureCoords);
        float mapBrightness = rgb2hsv(mapInfo.rgb).b;

        if (mapBrightness > 0) {
            if (useAlbedoEmission >= 0.5) {
                out_Color += (textureColor + vec4(totalSpecular, 1.0)) * emissionIntensity;
                totalDiffuse = out_Color.xyz;
            } else {
                out_Color = mapInfo * emissionIntensity;
                totalDiffuse = mapInfo.xyz;
            }
        }
    }

//    out_Color = vec4(diffuse, 1.0) * texture(textureSampler, pass_textureCoords) + vec4(finalSpecular, 1.0);
    out_Color = mix(vec4(fogColor, 1.0), out_Color, visibility);

//    vec4 reflectedColor = texture(enviromentMap, reflectionVector);
//    out_Color = mix(out_Color, reflectedColor, length(totalSpecular) * reflectivity);
//    out_Color = mix(out_Color, reflectedColor, 0.6);
//    out_Color = reflectedColor;
}
