#version 330 core

uniform vec4 u_tint;
uniform sampler2D u_texture;

in vec2 v_texCoord;

out vec4 frag_color;

void main()
{
    vec4 color = texture(u_texture, v_texCoord);

    if (color.a == 0) {
     discard;
    }

    frag_color = color * (1.0-u_tint.a) + u_tint*u_tint.a;
}
