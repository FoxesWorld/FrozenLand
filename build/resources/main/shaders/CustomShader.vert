#version 110

attribute vec4 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

varying vec2 texCoord;
varying vec3 normal;

void main() {
    texCoord = inTexCoord;
    normal = inNormal;
    gl_Position = g_WorldViewProjectionMatrix * inPosition;
}