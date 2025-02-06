package com.starcloud.ops.business.app.feign.dto.video;

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
@Schema(name = "VideoGeneratorConfig", description = "视频生成配置类")
public class VideoGeneratorConfig {
    private String id;
    private Map<String, String> resources;
    private GlobalSettings globalSettings;
    private List<VoiceUnit> voiceUnits;


    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class GlobalSettings {
        private int elementInterval;
        private int unitInterval;
        private String voiceRole;
        private String repeatRole;
        private String soundEffect;
        private Resolution resolution;
        private int fps;
        private String format;
        private String quality;
        private Background background;
        private Boolean repeatEnable;
        private Boolean animationEnable;
        private Integer soundSpeed;
        private Subtitles subtitles;
    }


    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class Subtitles {
        private Boolean enable;
        private String mode;
        private String font;
        private String fontSize;
        private String color;
        private String bgColor;
        private Float opacity;
        private Point position;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class Resolution {
        private int width;
        private int height;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class Background {
        private String type;
        private String source;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class VoiceUnit {
        private String id;
        private int order;
        private VoiceUnitSettings settings;
        private List<Element> elements;
        private SoundEffect soundEffect;

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
    public static
    class Element {
        private String type;
        private String content;
        private Audio audio;
        private Point point;
        private RepeatAfter settings;


    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class Audio {
        private String voiceRole;
        private int voiceSpeed;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class Point {
        private double x;
        private double y;
        private double bx;
        private double by;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class RepeatAfter {
        private Boolean audioEnable;
        private Boolean repeatEnable;
        private String repeatRole;
        private int repeatCount;
        private int repeatSpeed;
        private String pauseEnable;
        private Boolean subtitleEnable;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class SoundEffect {
        private Animation animation;

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class Animation {
        private String type;
        private List<Integer> size;
        private Map<String, Object> params;
    }

}
