#version 330 core
out vec4 FragColor;

in vec3 vertColor;
in vec2 texCoord;
in vec3 normal;

uniform sampler2D tex;

void main()
{
  vec3 lightDir = normalize(vec3(-0.8, -1, 0.4));
  float ambientStrength = 0.3;
  float diff = max(dot(normalize(normal), -lightDir), 0.0);

  FragColor = (diff + ambientStrength) * texture(tex, texCoord);
}
