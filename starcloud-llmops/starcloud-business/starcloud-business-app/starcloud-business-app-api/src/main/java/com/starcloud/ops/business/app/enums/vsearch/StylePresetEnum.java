package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 风格预设
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-04-19
 */
@Getter
public enum StylePresetEnum implements IEnumable<String> {

    /**
     * enhance 增强
     */
    ENHANCE("enhance", "STYLE_PRESET_ENHANCE_LABEL", "STYLE_PRESET_ENHANCE_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/enhance.png"),

    /**
     * anime 动漫
     */
    ANIME("anime", "STYLE_PRESET_ANIME_LABEL", "STYLE_PRESET_ANIME_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/anime.png"),

    /**
     * photographic
     */
    PHOTOGRAPHIC("photographic", "STYLE_PRESET_PHOTOGRAPHIC_LABEL", "STYLE_PRESET_PHOTOGRAPHIC_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/photographic.png"),

    /**
     * digital-art 数字艺术
     */
    DIGITAL_ART("digital-art", "STYLE_PRESET_DIGITAL_ART_LABEL", "STYLE_PRESET_DIGITAL_ART_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/digital-art.png"),

    /**
     * comic-book 漫画
     */
    COMIC_BOOK("comic-book", "STYLE_PRESET_COMIC_BOOK_LABEL", "STYLE_PRESET_COMIC_BOOK_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/comic-book.png"),

    /**
     * fantasy-art 幻想艺术
     */
    FANTASY_ART("fantasy-art", "STYLE_PRESET_FANTASY_ART_LABEL", "STYLE_PRESET_FANTASY_ART_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/fantasy-art.png"),

    /**
     * analog-film 模拟电影
     */
    ANALOG_FILM("analog-film", "STYLE_PRESET_ANALOG_FILM_LABEL", "STYLE_PRESET_ANALOG_FILM_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/analog-film.png"),

    /**
     * neon-punk 霓虹朋克
     */
    NEON_PUNK("neon-punk", "STYLE_PRESET_NEON_PUNK_LABEL", "STYLE_PRESET_NEON_PUNK_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/neon-punk.png"),

    /**
     * isometric 等轴
     */
    ISOMETRIC("isometric", "STYLE_PRESET_ISOMETRIC_LABEL", "STYLE_PRESET_ISOMETRIC_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/isometric.png"),

    /**
     * low-poly 低多边形
     */
    LOW_POLY("low-poly", "STYLE_PRESET_LOW_POLY_LABEL", "STYLE_PRESET_LOW_POLY_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/low-poly.png"),

    /**
     * origami
     */
    ORIGAMI("origami", "STYLE_PRESET_ORIGAMI_LABEL", "STYLE_PRESET_ORIGAMI_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/origami.png"),

    /**
     * line-art 线条艺术
     */
    LINE_ART("line-art", "STYLE_PRESET_LINE_ART_LABEL", "STYLE_PRESET_LINE_ART_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/line-art.png"),

    /**
     * modeling-compound 模型复合
     */
    MODELING_COMPOUND("modeling-compound", "STYLE_PRESET_MODELING_COMPOUND_LABEL", "STYLE_PRESET_MODELING_COMPOUND_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/modeling-compound.png"),

    /**
     * cinematic 电影
     */
    CINEMATIC("cinematic", "STYLE_PRESET_CINEMATIC_LABEL", "STYLE_PRESET_CINEMATIC_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/cinematic.png"),

    /**
     * 3d-model 3D模型
     */
    MODEL_3D("3d-model", "STYLE_PRESET_MODEL_3D_LABEL", "STYLE_PRESET_MODEL_3D_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/3d-model.png"),

    /**
     * pixel-art
     */
    PIXEL_ART("pixel-art", "STYLE_PRESET_PIXEL_ART_LABEL", "STYLE_PRESET_PIXEL_ART_DESCRIPTION", "https://service-oss.mofaai.com.cn/metadata/preset/pixel-art.png"),

    ;

    /**
     * Code
     */
    private final String code;

    /**
     * Value
     */
    private final String label;

    /**
     * 描述
     */
    private final String description;

    /**
     * 图片
     */
    private final String image;

    StylePresetEnum(String code, String label, String description, String image) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.image = image;
    }
}
