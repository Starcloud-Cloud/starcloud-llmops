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
public enum StylePresetEnum implements IEnumable<String> {

    /**
     * 3d-model 3D模型
     */
    MODEL_3D("3d-model", "STYLE_PRESET_MODEL_3D_LABEL", "STYLE_PRESET_MODEL_3D_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/3d-model.png"),

    /**
     * analog-film 模拟电影
     */
    ANALOG_FILM("analog-film", "STYLE_PRESET_ANALOG_FILM_LABEL", "STYLE_PRESET_ANALOG_FILM_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/analog-film.png"),

    /**
     * anime 动漫
     */
    ANIME("anime", "STYLE_PRESET_ANIME_LABEL", "STYLE_PRESET_ANIME_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/anime.png"),

    /**
     * cinematic 电影
     */
    CINEMATIC("cinematic", "STYLE_PRESET_CINEMATIC_LABEL", "STYLE_PRESET_CINEMATIC_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/cinematic.png"),

    /**
     * comic-book 漫画
     */
    COMIC_BOOK("comic-book", "STYLE_PRESET_COMIC_BOOK_LABEL", "STYLE_PRESET_COMIC_BOOK_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/comic-book.png"),

    /**
     * digital-art 数字艺术
     */
    DIGITAL_ART("digital-art", "STYLE_PRESET_DIGITAL_ART_LABEL", "STYLE_PRESET_DIGITAL_ART_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/digital-art.png"),

    /**
     * enhance 增强
     */
    ENHANCE("enhance", "STYLE_PRESET_ENHANCE_LABEL", "STYLE_PRESET_ENHANCE_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/enhance.png"),

    /**
     * fantasy-art 幻想艺术
     */
    FANTASY_ART("fantasy-art", "STYLE_PRESET_FANTASY_ART_LABEL", "STYLE_PRESET_FANTASY_ART_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/fantasy-art.png"),

    /**
     * isometric 等轴
     */
    ISOMETRIC("isometric", "STYLE_PRESET_ISOMETRIC_LABEL", "STYLE_PRESET_ISOMETRIC_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/isometric.png"),

    /**
     * line-art 线条艺术
     */
    LINE_ART("line-art", "STYLE_PRESET_LINE_ART_LABEL", "STYLE_PRESET_LINE_ART_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/line-art.png"),

    /**
     * low-poly 低多边形
     */
    LOW_POLY("low-poly", "STYLE_PRESET_LOW_POLY_LABEL", "STYLE_PRESET_LOW_POLY_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/low-poly.png"),

    /**
     * modeling-compound 模型复合
     */
    MODELING_COMPOUND("modeling-compound", "STYLE_PRESET_MODELING_COMPOUND_LABEL", "STYLE_PRESET_MODELING_COMPOUND_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/modeling-compound.png"),

    /**
     * neon-punk 霓虹朋克
     */
    NEON_PUNK("neon-punk", "STYLE_PRESET_NEON_PUNK_LABEL", "STYLE_PRESET_NEON_PUNK_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/neon-punk.png"),

    /**
     * origami
     */
    ORIGAMI("origami", "STYLE_PRESET_ORIGAMI_LABEL", "STYLE_PRESET_ORIGAMI_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/origami.png"),

    /**
     * photographic
     */
    PHOTOGRAPHIC("photographic", "STYLE_PRESET_PHOTOGRAPHIC_LABEL", "STYLE_PRESET_PHOTOGRAPHIC_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/photographic.png"),

    /**
     * pixel-art
     */
    PIXEL_ART("pixel-art", "STYLE_PRESET_PIXEL_ART_LABEL", "STYLE_PRESET_PIXEL_ART_DESCRIPTION", "https://download.hotsalecloud.com/mofaai/images/style-preset/pixel-art.png"),

    /**
     * tile-texture
     */
//    TILE_TEXTURE("tile-texture", "STYLE_PRESET_TILE_TEXTURE_LABEL", "STYLE_PRESET_TILE_TEXTURE_DESCRIPTION", ""),
    ;

    /**
     * Code
     */
    @Getter
    private final String code;

    /**
     * Value
     */
    @Getter
    private final String label;

    /**
     * 描述
     */
    @Getter
    private final String description;

    /**
     * 图片
     */
    @Getter
    private final String image;

    StylePresetEnum(String code, String label, String description, String image) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.image = image;
    }
}
