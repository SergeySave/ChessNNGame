#version 330 core

uniform mat4 u_modelTransform;
uniform mat4 u_projectionView;

in vec3 a_position;
in vec2 a_uv;

out vec2 v_texCoord;

void main()
{
    gl_Position = u_projectionView * u_modelTransform * vec4(a_position, 1.0);
    v_texCoord = a_uv;
}
