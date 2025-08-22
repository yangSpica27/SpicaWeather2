uniform vec2 uResolution; // 屏幕分辨率
uniform float uTime; // 时间
uniform shader uTex; // 输入的纹理
uniform vec2 uGravity;// 方向
// uniform sampler2D uMaskTex; // 输入的纹理

// #define uStaticDropSize 1.
// #define uStaticDropAmount 1.
// #define uStaticDropSpeed 1.

// #define uRunningDropSize 1.
// #define uRunningDropAmount 1.
// #define uRunningDropSpeed 1.

// #define uHighlightIntensity 0.05

/**uniform float uStaticDropSize;
uniform float uStaticDropAmount;
uniform float uStaticDropSpeed;

uniform float uRunningDropSize;
uniform float uRunningDropAmount;
uniform float uRunningDropSpeed;

uniform float uHighlightIntensity;*/

uniform float uStaticDropSize   ;
uniform float uStaticDropAmount ;
uniform float uStaticDropSpeed  ;
uniform float uRunningDropSize  ;
uniform float uRunningDropAmount;
uniform float uRunningDropSpeed ;

const float uHighlightIntensity           = 1.0;

// 生成一个三维噪声
vec3 N13(float p) {
    vec3 p3 = fract(vec3(p) * vec3(.1031,.11369,.13787));
    p3 += dot(p3, p3.yzx + 19.19);
    return fract(vec3((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y, (p3.y+p3.z)*p3.x));
}

// 生成一个一维噪声
float N(float t) {
    return fract(sin(t*12345.564)*7658.76);
}

// 生成一个锯齿波
float Saw(float b, float t) {
    return smoothstep(0., b, t)*smoothstep(1., b, t);
}

// 生成雨滴层
vec2 DropLayer2(vec2 uv, float t) {

    t *= uRunningDropSpeed;

    vec2 UV = uv; // 获取纹理坐标

    uv /= uRunningDropSize;
    uv.y += t * 0.55; // 垂直方向上的位移

    vec2 a = vec2(6., 1.); // 控制雨滴的形状

    //vec2 grid = a * 4.; // 网格大小
    vec2 grid = a * 3.; // 网格大小

    vec2 id = floor(uv * grid); // 计算网格索引
    float colShift = N(id.x); // 列偏移
    uv.y += colShift; // 应用列偏移
    id = floor(uv * grid); // 重新计算网格索引
    vec3 n = N13(id.x * 35.2 + id.y * 2376.1); // 生成三维噪声
    vec2 st = fract(uv * grid) - vec2(.5, 0); // 计算网格内的偏移
    float x = n.x - .5; // x坐标
    float y = UV.y * 20.; // y坐标
    x *= .7; // x坐标缩放
    float ti = fract(t + n.z); // 时间缩放
    y = (Saw(.9, ti) - .5) * .9 + .5; // y坐标变化
    vec2 p = vec2(x, y); // 雨滴位置
    float d = length((st - p) * a.yx); // 计算距离

   // float mainDrop = smoothstep(.3, .0, d); // 主要雨滴
    float mainDrop = smoothstep(.4, .0, d); // 主要雨滴

    float rr = smoothstep(1.0, y, st.y); // 半径
    float r = sqrt(rr); // 半径缩放
    float cd = abs(st.x - x); // 水平方向上的距离

    float trail = smoothstep(.23 * r, .15 * rr, cd); // 拖尾效果

    float trailFront = smoothstep(-.02, .02, st.y - y); // 拖尾前沿
    trail *= trailFront * rr; // 拖尾效果
    y = UV.y; // 恢复y坐标

    float trail2 = smoothstep(.2 * r, .0, cd); // 拖尾效果2

    float droplets = max(0., (sin(y * (1. - y) * 120.) - st.y)) * trail2 * trailFront * n.z; // 水滴效果
    y = fract(y * 10.) + (st.y - .5); // 计算y坐标
    float dd = length(st - vec2(x, y)); // 计算距离
    droplets = smoothstep(.3, 0., dd); // 水滴效果
    float m = mainDrop + droplets * r * trailFront; // 最终效果
    return vec2(m, trail); // 返回结果
}

// 生成静态雨滴
float StaticDrops(vec2 uv, float t) {
    t *= uStaticDropSpeed;
    // 将纹理坐标缩放到40倍
    // uv *= 40.;
    uv *= 40. / uStaticDropSize;
    // 计算网格索引
    vec2 id = floor(uv);
    // 计算网格内的偏移
    uv = fract(uv) - .5;
    // 生成三维噪声
    vec3 n = N13(id.x * 107.45 + id.y * 3543.654);
    // 计算雨滴位置
    vec2 p = (n.xy - .5) * .7;
    // 计算距离
    float d = length(uv - p);
    // 计算时间缩放
    float fade = Saw(.025, fract(t + n.z));
    // 计算最终效果
    //float c = smoothstep(.2, 0., d) * fract(n.z * 10.) * fade;
    float c = smoothstep(.3, 0., d) * fract(n.z * 10.) * fade;
    return c;
}

// 生成雨滴效果
float Drops(vec2 uv, float t, float rs, float ra) {
    // 生成静态雨滴效果
    float s = StaticDrops(uv, t * 1.2) * rs * uStaticDropAmount;
    // 生成雨滴层效果
    vec2 m1 = DropLayer2(uv, t * 1.85) * ra * uRunningDropAmount;
    // 计算最终的颜色值
     float c = smoothstep(.3, 1., s+m1.x);
    //float c = smoothstep(.5, 1., s+m1.x);
    return c;
}

float luminance(vec3 rgb) {
    vec3 w = vec3(0.2125, 0.7154, 0.0721);
    return dot(rgb, w);
}

vec4 main(vec2 xy)
{
    vec4 fragColor;
    //值往右下增加
    vec2 UV = xy/uResolution; // 获取纹理坐标
    //UV.y-=0.4;

    //UV.y = 1.0 -UV.y;

    vec2 uv_rev = vec2(UV.x,1.0-UV.y);
/**    vec2 uv = (uv_rev - 0.5);
    uv.x *= uResolution.x / uResolution.y;*/
    //uv *= 1.25;
    vec2 uv = (UV * uResolution - .5 *uResolution) / uResolution.y;
    uv.y = 1.0 - uv.y;


    // vec2 uv = (UV * uResolution - .5 *uResolution) / uResolution.y; // 将纹理坐标转换为屏幕坐标
    // uv.y = 1.0 - uv.y; // 翻转纹理坐标的y轴
    float T = uTime * 2.3; // 时间缩放
    float alpha = smoothstep(0.0, 2.0, uTime); // 透明度
    float t = T * .2; // 时间缩放

    float rainAmount = sin(UV.x * UV.y)*.3+.7; // 雨滴数量

    float staticDrops = smoothstep(-.5, 1., rainAmount)*2.; // 静态雨滴
    float c = Drops(uv, t, staticDrops, rainAmount); // 雨滴效果

    vec2 e = vec2(.0005, 0.);

    float cx = Drops(uv+e, t, staticDrops, rainAmount);

    vec2 n = vec2(c-cx, cx-c);

    vec2 texUV = UV+n;//test

    //vec3 texNorm = normalize(vec3(dFdx(texUV.x), dFdy(texUV.y), 0.2));
    //vec3 texNorm = normalize(vec3(texUV.x, texUV.y, 0.2));//test
    //vec3 texNorm = normalize(vec3(1.0/uResolution.x, 1.0/uResolution.y, 0.2));
    //vec3 texNorm = normalize(vec3(1.0/1080.0, 1.0/2400.0, 0.2));//test
    //vec3 texNorm = normalize(vec3(1.0/uResolution.x, 1.0/uResolution.y, 0.2));//test
    vec3 texNorm = normalize(vec3(n.xy, 0.2));//test
    vec3 sunDir = normalize(vec3(-1.0, 0.0, -1.0));
    //vec3 sunDir = normalize(vec3(-60.0, -40.0, -10.0));
    vec3 r = reflect(sunDir, texNorm);
    //假设
    // 计算高光强度
    float spec = max(dot(r, vec3(0., 0., 1.)), 0.0);
    float test_uHighlightIntensity = 0.12;
    vec3 radiance = spec * length(n) * vec3(1.0, 1.0, 1.0) * test_uHighlightIntensity; // mix(1.0, 5.0, uHighlightIntensity);
     vec4 textureColor = alpha < 1.0 ? mix(uTex.eval(UV * uResolution), uTex.eval(texUV * uResolution), alpha): uTex.eval(texUV * uResolution);
   // vec4 textureColor = alpha < 1.0 ? mix(uTex.eval(UV/uResolution), uTex.eval(texUV/uResolution), alpha): uTex.eval(texUV/uResolution);

    //textureColor= uTex.eval(UV * uResolution);
   // textureColor= mix(uTex.eval(UV * uResolution), uTex.eval(texUV * uResolution), alpha);
/**    textureColor = vec4(0.0);
    if(luminance(radiance) > 0.01)
    {
        textureColor += vec4(vec3(1.0), luminance(radiance));
    }*/


    // textureColor.a *= texture2D(uMaskTex,UV).a > 0. ? 1. : 0.;
/**    if(UV.y>0.9)
    {
        textureColor = vec4(1.0,0.0,0.0,1.0);
    }*/
    fragColor = textureColor; // 输出颜色
    return fragColor;
}
