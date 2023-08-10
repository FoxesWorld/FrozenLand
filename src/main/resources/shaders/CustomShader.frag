#version 110

#define SRGB 1
#define FRAGMENT_SHADER 1
#define DIFFUSEMAP 1
#define NORMALMAP 1
#define ROUGHNESSMAP 1
#define OCCLUSIONMAP 1
#define HEIGHTMAP 1

uniform sampler2D m_DiffuseMap;
uniform sampler2D m_NormalMap;
uniform sampler2D m_RoughnessMap;
uniform sampler2D m_OcclusionMap;
uniform sampler2D m_HeightMap;

varying vec2 texCoord;
varying vec3 normal;

void main() {
    vec4 diffuseColor = texture2D(m_DiffuseMap, texCoord);
    vec3 surfaceNormal = normalize(normal);
    vec3 normalFromMap = normalize(texture2D(m_NormalMap, texCoord).xyz * 2.0 - 1.0);
    vec3 normalInterpolated = mix(surfaceNormal, normalFromMap, diffuseColor.a);

    // Retrieve values from textures for Roughness, Occlusion, and Height
    float roughness = texture2D(m_RoughnessMap, texCoord).r;
    float occlusion = texture2D(m_OcclusionMap, texCoord).r;
    float height = texture2D(m_HeightMap, texCoord).r;

    // Calculate lighting direction
    vec3 lightDir = normalize(vec3(1.0, 1.0, 1.0));

    // Calculate diffuse lighting
    float diff = max(dot(normalInterpolated, lightDir), 0.0);
    vec3 diffuse = diff * diffuseColor.rgb;

    // Calculate ambient shading based on occlusion map
    vec3 ambient = mix(vec3(1.0), diffuseColor.rgb, occlusion);

    // Calculate specular reflection
    vec3 viewDir = normalize(-gl_FragCoord.xyz);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(normalInterpolated, halfwayDir), 0.0), 32.0);
    vec3 specular = vec3(spec);

    // Final pixel color
    vec3 finalColor = ambient + diffuse + specular;

    gl_FragColor = vec4(finalColor, 1.0);
}