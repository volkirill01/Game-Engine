#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform sampler2D shadowMap;

uniform vec3 lightPosition[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform vec3 lightRotation[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform vec3 lightColor[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform float lightIntensity[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform int lightType[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
uniform float lightRange[9]; // MAXIMUM COUNT OF LIGHTS PER ENTITY

uniform vec3 fogColor;
uniform vec3 ambientLightColor;
uniform float ambientLightIntensity;

uniform float shineDamper;
uniform float reflectivity;

uniform float shadowMapSize;

uniform vec2 tiling;

const int pcfCount = 2; // Percentage Closer Filtering (Size of area of sample shadow)
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

const float shadowBias = 0.002;

void main() {

    float texelSize = 1.0 / shadowMapSize;
    float total = 0.0;

    for (int x = -pcfCount; x <= pcfCount; x++) {
        for (int y = -pcfCount; y <= pcfCount; y++) {
            float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
            if (shadowCoords.z > objectNearestLight + shadowBias)
                total += 1.0;
        }
    }

    total /= totalTexels;
    float lightFactor = 1.0 - (total * shadowCoords.w);

    vec4 blendMapColor = texture(blendMap, pass_textureCoords);

    float backgroundTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
    vec2 tiledCoords = pass_textureCoords * tiling;
    vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backgroundTextureAmount;
    vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
    vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
    vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;

    vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for (int i = 0; i < 9; i++) {
        if (lightType[i] == 0) { // Direction light
            vec3 unitLightVector = normalize(lightRotation[i]);
            float nDotl = dot(unitNormal, unitLightVector);
            float brightness = max(nDotl, 0.0);

            vec3 lightDirection = -unitLightVector;
            vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

            float specularFactor = max(dot(reflectedLightDirection, unitVectorToCamera), 0.0);
            float dampedFactor = pow(specularFactor, shineDamper);

            totalDiffuse = totalDiffuse + ((brightness * lightColor[i] * lightIntensity[i]));
            totalSpecular = totalSpecular + ((dampedFactor * lightColor[i] * lightIntensity[i]));
        } else if (lightType[i] == 1) { // Point light
            float distance = length(toLightVector[i]);
            vec3 unitLightVector = normalize(toLightVector[i]);
            float nDotl = dot(unitNormal, unitLightVector);
            float brightness = max(nDotl, 0.0);

            vec3 lightDirection = -unitLightVector;
            vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

            float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
            specularFactor = max(specularFactor, 0.0);
            float dampedFactor = pow(specularFactor, shineDamper);

            totalDiffuse = totalDiffuse + ((brightness * lightColor[i] * lightIntensity[i]) / distance);
            totalSpecular = totalSpecular + ((dampedFactor * lightColor[i] * lightIntensity[i]) / distance);
        }
    }
    totalDiffuse = max(totalDiffuse * lightFactor, ambientLightIntensity);

    out_Color = vec4(totalDiffuse * ambientLightColor, 1.0) * totalColor + vec4(totalSpecular, 1.0);
//    out_Color = vec4(ambientLightColor, 1.0) * totalColor + vec4(finalSpecular, 1.0);
    out_Color = mix(vec4(fogColor, 1.0), out_Color, visibility);
}
