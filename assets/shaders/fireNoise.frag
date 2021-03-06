#version 120
#pragma optionNV (unroll all)

varying vec2 v_texCoords;

uniform vec2 u_resolution;

// Simplex 2D noise
//
vec3 permute(vec3 x) { return mod(((x*34.0)+1.0)*x, 289.0); }

float snoise(vec2 v){
  const vec4 C = vec4(0.211324865405187, 0.366025403784439,
           -0.577350269189626, 0.024390243902439);
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);
  vec2 i1;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;
  i = mod(i, 289.0);
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
  + i.x + vec3(0.0, i1.x, 1.0 ));
  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy),
    dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;
  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

void main()
{
    vec2 uv = v_texCoords * u_resolution / 800.f;
    float noise = 0.0f;
    //for(float i = 2.0f; i < 15.0f; ++i)
    //    noise += 1.0f / i * snoise(uv * i*i + i);
    noise += 1.0f / 2.0f * snoise(uv* 2.0f * 2.0f + 2.0f);
    noise += 1.0f / 3.0f * snoise(uv* 3.0f * 3.0f + 3.0f);
    noise += 1.0f / 4.0f * snoise(uv* 4.0f * 4.0f + 4.0f);
    noise += 1.0f / 5.0f * snoise(uv* 5.0f * 5.0f + 5.0f);
    noise += 1.0f / 6.0f * snoise(uv* 6.0f * 6.0f + 6.0f);
    noise += 1.0f / 7.0f * snoise(uv* 7.0f * 7.0f + 7.0f);
    noise += 1.0f / 8.0f * snoise(uv* 8.0f * 8.0f + 8.0f);
    noise += 1.0f / 9.0f * snoise(uv* 9.0f * 9.0f + 9.0f);
    noise += 1.0f / 10.0f * snoise(uv* 10.0f * 10.0f + 10.0f);
    noise += 1.0f / 11.0f * snoise(uv* 11.0f * 11.0f + 11.0f);
    noise += 1.0f / 12.0f * snoise(uv* 12.0f * 12.0f + 12.0f);
    noise += 1.0f / 13.0f * snoise(uv* 13.0f * 13.0f + 13.0f);
    noise += 1.0f / 14.0f * snoise(uv* 14.0f * 14.0f + 14.0f);
    noise = noise / 2.0f + 0.5f;
    gl_FragColor = vec4(1) * noise;
}