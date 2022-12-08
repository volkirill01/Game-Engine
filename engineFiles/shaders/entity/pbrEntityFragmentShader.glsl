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
//
//in vec2 pass_textureCoords;
//in vec3 surfaceNormal;
//in vec3 toLightVector[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
//in vec3 toCameraVector;
//in vec3 reflectionVector;
//in float visibility;
//
//layout (location = 0) out vec4 out_Color;
//
//uniform sampler2D textureSampler;
//uniform samplerCube enviromentMap;
//
//uniform sampler2D specularMap;
//uniform float specularIntensity;
//
//uniform sampler2D emissionMap;
//uniform float emissionIntensity;
//uniform float useAlbedoEmission;
//
//uniform vec3 lightColor[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
//uniform float lightIntensity[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
//uniform vec3 attenuation[4]; // MAXIMUM COUNT OF LIGHTS PER ENTITY
//
//uniform vec3 color;
//
//uniform vec3 fogColor;
//uniform vec3 ambientLightColor;
//uniform float ambientLightIntensity;
//
//uniform float shineDamper;
//uniform float reflectivity;
//
//uniform float alphaClip;

out vec4 FragmentColor; // out_Color

in vec2 TexCoords;  // pass_textureCoords
in vec3 WorldPos;   // position
in vec3 Normal;     // surfaceNormal

// Material parameters
uniform sampler2D albedoMap;
uniform sampler2D metallicMap;
uniform sampler2D roughnessMap;
uniform sampler2D aoMap;
uniform float metallicIntensity;
uniform float roughnessIntensity;

// Lights
uniform vec3 lightPositions[4]; // toLightVector    // max lights count
uniform vec3 lightColors[4]; // lightColor          // max lights count

uniform vec3 camPos;

const float PI = 3.14159265359;

// ----------------------------------------------------------------------------------------
// PBR - functions
float distributionGGX(float NdotH, float roughness) {
    float a = roughness * roughness;
    float a2 = a * a;
    float denom = NdotH * NdotH * (a2 - 1.0) + 1.0;
    denom = PI * denom * denom;
    return a2 / max(denom, 0.0000001);  // prevent divide by zero
}
// ----------------------------------------------------------------------------------------
float geometrySmith(float NdotV, float NdotL, float roughness) {
    float r = roughness + 1.0;
    float k = (r * r) / 8.0;
    float ggx1 = NdotV / (NdotV * (1.0 - k) + k); // Schlick GGX
    float ggx2 = NdotL / (NdotL * (1.0 - k) + k);
    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------------------
vec3 fresnelSchlick(float HdotV, vec3 baseReflectivity) {
    // baseReflectivity in range 0 to 1
    // returns range of baseReflectivity to 1
    // increase as HdotV decreases (more reflectivity when surface viewed at large angles)
    return baseReflectivity + (1.0 - baseReflectivity) * pow(1.0 - HdotV, 5.0);
}

// ----------------------------------------------------------------------------------------
void main() {
    vec3 albedo = texture(albedoMap, TexCoords).rgb;
    float metallic = rgb2hsv(texture(metallicMap, TexCoords).rgb).b * metallicIntensity;
    float roughness = rgb2hsv(texture(roughnessMap, TexCoords).rgb).b * roughnessIntensity;
    float ao = rgb2hsv(texture(aoMap, TexCoords).rgb).b;

    vec3 N = normalize(Normal);
    vec3 V = normalize(camPos - WorldPos);

    // calculate reflectivity at normal incidece; if dia-electric (like plastic) use baseReflectivity
    // of 0.04 and if it`s a metal, use the albedo color as baseReflectivity (metallic workflow)
    vec3 baseReflectivity = mix(vec3(0.04), albedo, metallic);

    // reflectance equation
    vec3 Lo = vec3(0.0);
    for (int i = 0; i < 4; i++) { // max (i) = lights count
        //calculate per-light radiance
        vec3 L = normalize(lightPositions[i] - WorldPos);
        vec3 H = normalize(V + L);
        float distance = length(lightPositions[i] - WorldPos);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance = lightColors[i] * attenuation;

        // Cook-Torrance BRDF
        float NdotV = max(dot(N, V), 0.0000001); // min of 0.0000001 to prevent divide by zero
        float NdotL = max(dot(N, L), 0.0000001);
        float HdotV = max(dot(H, V), 0.0);
        float NdotH = max(dot(N, H), 0.0);

        float D = distributionGGX(NdotH, roughness); // larger the more micro-facets aligned to H (normal distribution function)
        float G = geometrySmith(NdotV, NdotL, roughness); // smaller the more micro-facets shadowed by other micro-facets
        vec3 F = fresnelSchlick(HdotV, baseReflectivity); // proportion of specular reflectance

        vec3 specular = D * G * F;
        specular /= 4.0 * NdotV * NdotL;

        // for energy conservation, the diffuse and specular light can`t
        // be above 1.0 (unless the surface emits light); to preserve this
        // relationship th diffuse component (kD) should equal 1.0 - kS
        vec3 kD = vec3(1.0) - F;

        // multiply kD by the inverse metalness such that only non-metals
        // have diffuse lights, or a linear blend if partly metal
        // (pure metals have no diffuse light)
        kD *= 1.0 - metallic;

        // note that 1) angle of light to surface affects specular, not just diffuse
        //           2) we mix albedo with diffuse, but not specular
        Lo += (kD * albedo / PI + specular) * radiance * NdotL;
    }

    // ambient lighting (note that the next IBL tutorial will replace
    // this ambient lighting with enviroment lighting).
    vec3 ambient = vec3(0.03) * albedo;

    vec3 color = ambient + Lo;

    // HDR tonemapping
    color = color / (color + vec3(1.0));
    // gamma correct
    color = pow(color, vec3(1.0 / 2.2));

    FragmentColor = vec4(color, 1.0);
//    FragmentColor = vec4(texture(roughnessMap, TexCoords).rgb, 1.0);
}
