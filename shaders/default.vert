precision highp float;

attribute vec3 aPosition;
attribute vec4 aColor;
attribute vec2 aTexcoords;

varying vec3 vPosition;
varying vec4 vColor;
varying vec2 vTexcoords;

void main() {
    vPosition = aPosition;
    vColor = aColor;
    vTexcoords = aTexcoords;
    gl_Position = vec4(vPosition, 1.0);
}