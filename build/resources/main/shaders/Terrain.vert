uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec4 inTangent;

varying vec2 texCoord;
varying vec3 lightDir;

void main() {
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);

    texCoord = inTexCoord;

    vec3 tangent = normalize(inTangent.xyz);
    vec3 binormal = cross(normalize(gl_Normal.xyz), tangent);
    vec3 normal = cross(tangent, binormal);

    lightDir = normalize(vec3(1.0, -1.0, -1.0)); // Example light direction

    // Transforming normal, tangent, and binormal to eye space
    vec3 normalCam = normalize(vec3(gl_NormalMatrix * vec4(normal, 0.0)));
    vec3 tangentCam = normalize(vec3(gl_NormalMatrix * vec4(tangent, 0.0)));
    vec3 binormalCam = normalize(vec3(gl_NormalMatrix * vec4(binormal, 0.0)));

    // Using tangent space normal for lighting
    lightDir = vec3(dot(lightDir, tangentCam),
                    dot(lightDir, binormalCam),
                    dot(lightDir, normalCam));
}
