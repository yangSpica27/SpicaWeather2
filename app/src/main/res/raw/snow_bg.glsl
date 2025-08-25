uniform shader image;
uniform vec2 resolution;
uniform float time;
uniform float percentage;
vec2 NormalizeCoordinates(vec2 o, vec2 r) {
    float2 uv = o / r - 0.5;
    if (r.x >= r.y) {
        uv.x *= r.x / r.y;
    } else {
        uv.y *= r.y / r.x;
    }
    return uv;
}

vec4 GetImageTexture(vec2 p, vec2 pivot, vec2 r) {
    if (r.x > r.y) {
        p.x /= r.x / r.y;
    } else {
        p.y /= r.y / r.x;
    }
    p += pivot;
    p *= r;
    return image.eval(p);
}
uniform int uLayers;
uniform float uDepth;
uniform float uSpeed;

const int MAX_LAYERS = 50;
const float WIDTH = 0.4;


vec4 main(float2 fragCoord) {
    float2 uv = NormalizeCoordinates(fragCoord, resolution);
    vec4 image = GetImageTexture(uv, vec2(0.5, 0.5), resolution);
    const mat3 p = mat3(13.323122, 23.5112, 21.71123, 21.1212, 28.7312, 11.9312, 21.8112, 14.7212, 61.3934);

    float ratio = resolution.y / resolution.x;

    vec3 acc = vec3(0.0);
    float alpha = 0.0; // Initialize alpha
    float dof = 5.0 * sin(time * 0.1);

    for (int i = 0; i < MAX_LAYERS; i++) {
        if (i >= uLayers) break; // Break out of the loop if i exceeds uLayers

        float fi = float(i);
        vec2 q = uv * (1.0 + fi * uDepth);

        // Adjust flake position with modulation and time
        q -= vec2(q.y * (WIDTH * mod(fi * 7.238917, 1.0) - WIDTH * 0.5), uSpeed * time / (1.0 + fi * uDepth * 0.03));

        vec3 n = vec3(floor(q), 31.189 + fi);
        vec3 m = floor(n) * 0.00001 + fract(n);
        vec3 mp = (31415.9 + m) / fract(p * m);
        vec3 r = fract(mp);

        // Rounded snowflake shape using a circular mask
        float2 center = mod(q, 1.0) - 0.5 + 0.5 * r.xy;
        float distanceToCenter = length(center); // Circular distance
        float flakeRadius = 0.015 + 0.01 * r.z; // Vary radius slightly per flake

        // Smoother edges with extended smoothstep
        float intensity = smoothstep(flakeRadius + 0.015, flakeRadius, distanceToCenter) *
        smoothstep(flakeRadius, flakeRadius - 0.015, distanceToCenter);

        // Ensure flakes are white or transparent (prevent black color)
        vec3 flakeColor = vec3(1.0); // White color for flakes
        acc += flakeColor * intensity;

        // Accumulate alpha with smooth transition
        alpha += intensity;
    }

    // Normalize alpha to ensure it doesn’t exceed 1.0
    alpha = clamp(alpha, 0.0, 1.0);

    vec3 finalColor = mix(image.rgb, acc, alpha);

    if(uv.y < -0.5*ratio || uv.y > .5*ratio) {
        finalColor = vec3(0.0);
        alpha = 0.0;
    }
    return vec4(finalColor, alpha+image.a);
}