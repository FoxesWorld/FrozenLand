uniform sampler2D m_DiffuseMap;
uniform sampler2D m_NormalMap;
uniform sampler2D m_OcclusionMap;
uniform vec2 m_DiffuseMap_0_scale;
uniform vec2 m_NormalMap_0_scale;
uniform vec2 m_OcclusionMap_0_scale;
uniform float m_ParallaxHeight;

varying vec2 texCoord;
varying vec3 lightDir;

void main() {
    vec4 diffuseColor = texture2D(m_DiffuseMap, texCoord * m_DiffuseMap_0_scale);
    vec3 normal = texture2D(m_NormalMap, texCoord * m_NormalMap_0_scale).xyz * 2.0 - 1.0;
    vec3 occlusion = texture2D(m_OcclusionMap, texCoord * m_OcclusionMap_0_scale).xyz;

    // Example Parallax Mapping
    vec2 newTexCoord = texCoord + (normal.xy * (occlusion.r * m_ParallaxHeight));
    vec3 viewDir = normalize(-vec3(gl_FragCoord.xy / vec2(640.0, 480.0), 1.0));
    float depth = texture2D(m_DepthMap, newTexCoord).r;
    vec3 parallax = newTexCoord + normal.xy * (depth - 0.5) * m_ParallaxHeight;

    // Applying lighting
    vec3 light = normalize(lightDir);
    float intensity = max(dot(normal, light), 0.0);
    vec3 finalColor = diffuseColor.rgb * intensity;

    gl_FragColor = vec4(finalColor, 1.0);
}
