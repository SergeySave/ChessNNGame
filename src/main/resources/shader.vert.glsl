#version 330 core

uniform float u_team;
uniform float u_type;

in vec2 a_position;
in vec2 a_uv;

out vec2 v_texCoord;

void main()
{
    gl_Position = vec4(a_position, 1.0, 1.0);
    v_texCoord = (a_uv + vec2(u_type, u_team)) / vec2(7.0, 2.0);
}
