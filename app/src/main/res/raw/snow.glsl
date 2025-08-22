uniform shader uTex; // 背景纹理
uniform shader uSnowTex;//雪纹理
uniform vec2 uResolution; // 背景分辨率
uniform vec2 uSnowTexWH; // 纹理分辨率

uniform float uProgress;

float perFromVal(float val, float min, float max) {
    return (val - min) / (max - min);
}

vec4 main(float2 xy) {
    vec4 fragColor;         // 输出的颜色
    vec2 vUv                = xy/uResolution;
    vUv.y                   = 1.0 - vUv.y;
    vec2 uv                 = vUv;
    vec2 texuv              = uv*uSnowTexWH;
    texuv.y                 = 1.0-texuv.y;
    vec4 snowTexColor       = uSnowTex.eval(texuv);
    //uSnowTex纹理具有alpha通道，获取到的r值会自动乘以alpha通道，这里手动除以a以获取需要的r值。
    snowTexColor.r          /= snowTexColor.a;
    vec4 snowColor          = vec4(1.0);
    float progress          = uProgress; // progress = min(fract(t) * 3.0, 1.0);
    float a                 = max(smoothstep(progress, 0.0, 1.0 - snowTexColor.r), 0.0);
    float uAlpha            = 1.0;
    snowColor.a             *= a * ( 1.0 - 1.0 / exp(uv.y*uv.y)) * 0.5 * uAlpha;
    snowColor.rgb           /= snowColor.a;
    vec4 uiColor            = uTex.eval(xy);
    // alpha blend snowColor and uicolor
    vec4 color              = vec4(0.0);
    if (snowColor.a < 0.01) {
      color                 = uiColor;
    } else{
      color                 = mix(uiColor, snowColor * snowColor.a * 0.4, snowColor.a);
    }
    fragColor               = color;
    return fragColor;
}
