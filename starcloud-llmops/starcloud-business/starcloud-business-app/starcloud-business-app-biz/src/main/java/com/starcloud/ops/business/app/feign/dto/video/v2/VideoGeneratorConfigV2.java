package com.starcloud.ops.business.app.feign.dto.video.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "VideoGeneratorConfigV2", description = "视频生成配置类")
public class VideoGeneratorConfigV2 {
    private String id;
    private Map<String, String> resources;
    private GlobalSettings globalSettings;
    private List<VoiceUnit> voiceUnits;
    private String version;



    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class GlobalSettings {
        private BasicVideoConfig video;
        private VoiceConfig voice;
        private RepeatConfig repeat;
        private AnimationConfig animation;
        private IntervalConfig interval;
        private float[] elementsScale;
        private SubtitleConfig subtitle;
        private RepetitivePattern repPattern;
    }


    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class BasicVideoConfig {
        private Resolution resolution;
        private int fps;
        private String format;
        private String quality;
        private BackgroundConfig background;
    }


    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class VoiceConfig {
        private String role;
        private int volume;
        private int speed;
        private Boolean enable;
        private TimeFrame frame;
        private int duration;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class RepeatConfig {
        private String role;
        private Boolean enable;
        private int speed;
        private int count;
        private TimeFrame frame;
        private Boolean subtitleEnable;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class AnimationConfig {
        private Boolean enable; // 是否启用动效
        private String aligning; // 动效对齐方式
        private String type; // 动效类型 对应AnimationType枚举
        private String image_path; // 动效图片路径
        private int[] size; // 动效大小
        private Object params; // 动效参数
        private float speed; // 动效速度

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class IntervalConfig {
        private int elementInterval; // 元素时间间隔
        private int unitInterval; // 单元时间间隔
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class SubtitleConfig {
        private Boolean enable; //  是否启用
        private String mode; // 字幕模式
        private String font; // 字体
        private int fontSize; // 字体大小
        private String color; // 字体颜色
        private String bgColor; // 背景色（可选）
        private float opacity; // 透明度
        private int kerning; //  字间距
        private Point position; // 位置
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class RepetitivePattern {
        private Boolean enable; //  是否启用
        private String content; // 重复内容
        private Point point; // 位置
        private TimeFrame frame; // 时间范围
        private int interval; // 重复元素发音间隔时间
        private String font; // 字体
        private int fontSize; // 字体大小
        private String color; //  字体颜色
        private int maxRepeatCount; // 最大重复次数
        private float minScale; //  最小缩放比例
        private float maxScale; // 最大缩放比例
        private Object _textClip; // 文件
    }
    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class VoiceUnit {
        private String id;
        private int order;
        private Object settings;
        private List<Element> elements;
        private SoundEffect soundEffect;
        private RepetitivePattern repPattern;
    }



    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class VoiceUnitSettings {
        private int interval;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Element {
        private String type; // 元素类型 元素类型（文字/图片/视频） ref:引用
        private String content; // 元素内容
        private VoiceConfig audio; // 语音设置
        private RepeatConfig repeat; // 跟读设置
        private Point point; // 元素位置
        private Marker marker; // 位置标记（可选）
        private SubtitleConfig subtitle; // 字幕设置
        private List<SubtitlesMeta> _subtitles; // 字幕结果（可选）
    }


    // ============================
    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Resolution {
        private int width;
        private int height;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class BackgroundConfig {
        private String type;
        private String source;
        private Object size;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class TimeFrame {
        private float start;
        private float end;
    }


    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Point {
        private double x;
        private double y;
        private double bx;
        private double by;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Marker {
        private String type; //标记类型：box（方框）或 icon（图标）
        private int[] size; //标记大小
        private String color; //标记颜色
        private int border_width; //边框宽度（对方框有效）
        private String icon_path;// 图标路径（当type为icon时使用）


    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class SubtitlesMeta {
        private int begin_index;
        private String phoneme;
        private int end_time;
        private int end_index;
        private int begin_time;
        private String text;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class SoundEffect {
        private Point point;
        private TimeFrame frame;
        private AnimationConfig animation;
        private int duration;
    }

}
