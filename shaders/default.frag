precision highp float;

varying vec3 vPosition;
varying vec4 vColor;
varying vec2 vTexcoords;

uniform sampler2D uTexture;
uniform float texturesEnabled;
uniform int uChannels;

vec4 processChannels(vec4 texel) {
    if (uChannels == 1) return vec4(texel.a);
    return texel;
}

void main() {
    vec3 color = vColor.rgb;
    vec4 texel = texture2D(uTexture, vTexcoords);
    if (texturesEnabled == 1.0) {
        if (texel.a < 0.1) discard;
        color *= processChannels(texel).rgb;
    }
    gl_FragColor = vec4(color, 1.0);
}