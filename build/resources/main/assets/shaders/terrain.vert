uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_NormalMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;

varying vec2 texCoord;

void main() {
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    vec3 normal = normalize((g_NormalMatrix * vec4(inNormal, 0.0)).xyz);
    texCoord = inTexCoord;
}
