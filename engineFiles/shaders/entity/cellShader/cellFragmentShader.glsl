#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColor[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform float lightIntensity[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform vec3 attenuation[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY

uniform vec3 color;

uniform vec3 fogColor;
uniform vec3 ambientLightColor;
uniform float ambientLightIntensity;

uniform float shineDamper;
uniform float reflectivity;

uniform float alphaClip;

const float levels = 3.0;

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
        float level = floor(brightness * levels);
        brightness = level / levels;

        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        level = floor(dampedFactor * levels);
        dampedFactor = level / levels;
        totalDiffuse = totalDiffuse + ((brightness * lightColor[i] * lightIntensity[i]) / attenuationFactor);
        totalSpecular = totalSpecular + ((dampedFactor * reflectivity * lightColor[i] * lightIntensity[i]) / attenuationFactor);
    }
    totalDiffuse = max(totalDiffuse, ambientLightIntensity);

    vec4 textureColor = texture(textureSampler, pass_textureCoords);
    if (textureColor.a < alphaClip) {
        discard;
    }

//    out_Color = vec4(diffuse, 1.0) * texture(textureSampler, pass_textureCoords) + vec4(finalSpecular, 1.0);
    out_Color = vec4(totalDiffuse * ambientLightColor, 1.0) * (textureColor * vec4(color, 1.0)) + vec4(totalSpecular, 1.0);
    out_Color = mix(vec4(fogColor, 1.0), out_Color, visibility);
}
