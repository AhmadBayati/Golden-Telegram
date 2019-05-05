package com.hanista.mobogram.ui.Components.Paint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ShaderSet {
    private static final String ATTRIBUTES = "attributes";
    private static final Map<String, Map<String, Object>> AVAILBALBE_SHADERS;
    private static final String FRAGMENT = "fragment";
    private static final String PAINT_BLITWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLITWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { vec4 tex = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }";
    private static final String PAINT_BRUSHLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { vec4 f = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = vec4(f.r * varIntensity, f.g, f.b, 0.0); }";
    private static final String PAINT_BRUSH_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { gl_FragColor = vec4(0, 0, 0, varIntensity * texture2D(texture, varTexcoord.st, 0.0).r); }";
    private static final String PAINT_BRUSH_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }";
    private static final String PAINT_COMPOSITEWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }";
    private static final String PAINT_COMPOSITEWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }";
    private static final String PAINT_NONPREMULTIPLIEDBLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); }";
    private static final String UNIFORMS = "uniforms";
    private static final String VERTEX = "vertex";

    static {
        AVAILBALBE_SHADERS = createMap();
    }

    private static Map<String, Map<String, Object>> createMap() {
        Map hashMap = new HashMap();
        Map hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BRUSH_VSH);
        hashMap2.put(FRAGMENT, PAINT_BRUSH_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord", "alpha"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        hashMap.put("brush", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BRUSH_VSH);
        hashMap2.put(FRAGMENT, PAINT_BRUSHLIGHT_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord", "alpha"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        hashMap.put("brushLight", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BLIT_VSH);
        hashMap2.put(FRAGMENT, PAINT_BLIT_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        hashMap.put("blit", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BLIT_VSH);
        hashMap2.put(FRAGMENT, PAINT_BLITWITHMASKLIGHT_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        hashMap.put("blitWithMaskLight", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BLIT_VSH);
        hashMap2.put(FRAGMENT, PAINT_BLITWITHMASK_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        hashMap.put("blitWithMask", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BLIT_VSH);
        hashMap2.put(FRAGMENT, PAINT_COMPOSITEWITHMASK_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        hashMap.put("compositeWithMask", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BLIT_VSH);
        hashMap2.put(FRAGMENT, PAINT_COMPOSITEWITHMASKLIGHT_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        hashMap.put("compositeWithMaskLight", Collections.unmodifiableMap(hashMap2));
        hashMap2 = new HashMap();
        hashMap2.put(VERTEX, PAINT_BLIT_VSH);
        hashMap2.put(FRAGMENT, PAINT_NONPREMULTIPLIEDBLIT_FSH);
        hashMap2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        hashMap2.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        hashMap.put("nonPremultipliedBlit", Collections.unmodifiableMap(hashMap2));
        return Collections.unmodifiableMap(hashMap);
    }

    public static Map<String, Shader> setup() {
        Map hashMap = new HashMap();
        for (Entry entry : AVAILBALBE_SHADERS.entrySet()) {
            Map map = (Map) entry.getValue();
            hashMap.put(entry.getKey(), new Shader((String) map.get(VERTEX), (String) map.get(FRAGMENT), (String[]) map.get(ATTRIBUTES), (String[]) map.get(UNIFORMS)));
        }
        return Collections.unmodifiableMap(hashMap);
    }
}
