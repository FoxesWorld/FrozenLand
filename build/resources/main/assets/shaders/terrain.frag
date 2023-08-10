uniform sampler2D m_DiffuseMap;
uniform sampler2D m_NormalMap;
uniform sampler2D m_RoughnessMap;
uniform sampler2D m_OcclusionMap;
uniform sampler2D m_HeightMap;

varying vec2 texCoord;

void main() {
    vec4 diffuseColor = texture2D(m_DiffuseMap, texCoord);
    vec3 normal = normalize(texture2D(m_NormalMap, texCoord).xyz * 2.0 - 1.0);

    // Получение значений текстур для Roughness, Occlusion и Height
    float roughness = texture2D(m_RoughnessMap, texCoord).r;
    float occlusion = texture2D(m_OcclusionMap, texCoord).r;
    float height = texture2D(m_HeightMap, texCoord).r;

    // Координаты направления света
    vec3 lightDir = normalize(vec3(1.0, 1.0, 1.0));

    // Рассчитываем яркость освещения (Diffuse)
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * diffuseColor.rgb;

    // Рассчитываем затенение на основе карты окклюзии
    vec3 ambient = mix(vec3(1.0), diffuseColor.rgb, occlusion);

    // Рассчитываем отраженное освещение (Specular)
    vec3 viewDir = normalize(-gl_FragCoord.xyz);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0); // Коэффициент бликов
    vec3 specular = vec3(spec);

    // Конечный цвет пикселя
    vec3 finalColor = ambient + diffuse + specular;

    gl_FragColor = vec4(finalColor, 1.0);
}
