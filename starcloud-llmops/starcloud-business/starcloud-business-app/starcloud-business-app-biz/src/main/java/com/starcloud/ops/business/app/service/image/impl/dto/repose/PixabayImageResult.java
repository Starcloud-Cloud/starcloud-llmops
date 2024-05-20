package com.starcloud.ops.business.app.service.image.impl.dto.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PixabayImageResult 请求结果
 */
@NoArgsConstructor
@Data
public class PixabayImageResult {


    /**
     * 总点击数
     */
    private Integer total;
    /**
     * 通过API可访问的图像数量。默认情况下，API被限制为每次查询最多返回500张图像。
     */
    private Integer totalHits;

    private List<HitsDTO> hits;

    @NoArgsConstructor
    @Data
    public static class HitsDTO {
        /**
         * 图片 ID
         */
        private Integer id;

        /**
         * Pixabay上的源页面 它提供了尺寸为imageWidth x imageHeight和文件大小为imageSize的原始图像的下载链接
         */
        private String pageURL;

        /**
         * 类型
         */
        private String type;
        /**
         * 标签
         */
        private String tags;
        /**
         * 低分辨率图像，最大宽度或高度为150px
         */
        private String previewURL;
        /**
         *
         */
        private Integer previewWidth;
        private Integer previewHeight;

        /**
         * Medium sized image with a maximum width or height of 640 px (webformatWidth x webformatHeight). URL valid for 24 hours.
         * <p>
         * Replace '_640' in any webformatURL value to access other image sizes:
         * Replace with '_180' or '_340' to get a 180 or 340 px tall version of the image, respectively. Replace with '_960' to get the image in a maximum dimension of 960 x 720 px.
         * <br/>
         * 中等大小的图像，最大宽度或高度为640像素(webformatWidth x webformatHeight)。URL有效期为24小时。
         * 替换任何webformatURL值中的'_640'以访问其他图像大小:
         * 替换为'_180'或'_340'，分别得到180或340像素高的图像版本。替换为'_960'以获得最大尺寸为960 x 720像素的图像
         */
        private String webformatURL;
        private Integer webformatWidth;
        private Integer webformatHeight;

        /**
         * Scaled image with a maximum width/height of 1280px.
         * <br/>
         * 缩放图像，最大宽度/高度为1280px。
         */
        private String largeImageURL;
        private Integer imageWidth;
        private Integer imageHeight;
        private Integer imageSize;

        /**
         * 图片浏览量
         */
        private Integer views;

        /**
         * 图片下载量
         */
        private Integer downloads;

        private Integer collections;

        /**
         * 图片收藏的数量
         */
        private Integer likes;
        /**
         * 评论总数
         */
        private Integer comments;
        /**
         * 用户ID和贡献者的名称
         */
        private Integer userId;
        /**
         * 贡献者
         */
        private String user;
        /**
         * 贡献者头像
         */
        private String userImageURL;
    }
}
