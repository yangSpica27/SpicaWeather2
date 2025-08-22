uniform shader uTex; // 背景纹理
uniform shader uFrostTex;//雪纹理
uniform vec2 uResolution; // 背景分辨率
uniform float uProgress;

vec4 main(float2 xy) {
    float uAlpha            = 1.0;
    vec4 fragColor; // 输出的颜色
    vec2 vUv                = xy/uResolution;
    vUv.y                   = 1.0 - vUv.y;
    vec2 uv                 = vUv;
    vec4 frostTexColor      = uFrostTex.eval(uv*uResolution);//改动
    vec4 frostColor         = vec4(1.0);
    float progress          = mix(0.0,0.6,uProgress * uProgress * uProgress); // progress = min(fract(t) * 3.0, 1.0);
    float a                 = max(smoothstep(progress, 0.0, frostTexColor.r), 0.0);
    frostColor.a            *= a * ( 1.0 - 1.0 / exp(uv.y * uv.y) ) * 0.5 * uAlpha;
    frostColor.rgb          /= frostColor.a;
    vec4 uiColor            = uTex.eval(xy);
    // alpha blend frostcolor and uicolor
    vec4 color              = vec4(0.0);
    if (frostColor.a < 0.01) {
        color               = uiColor;
    } else {
        float alphaStrengh = clamp(uProgress, 0.0, 1.0);
        color = mix(uiColor, frostColor * frostColor.a * 0.5, 0.7 * frostColor.a * alphaStrengh);
    }
    fragColor               = color;
    return fragColor;
}
