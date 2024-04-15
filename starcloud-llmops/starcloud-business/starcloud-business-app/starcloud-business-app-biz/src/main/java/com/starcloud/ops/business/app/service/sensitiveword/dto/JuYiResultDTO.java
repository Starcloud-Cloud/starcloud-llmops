package com.starcloud.ops.business.app.service.sensitiveword.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * JuYiResultDTO
 */
@NoArgsConstructor
@Data
public class JuYiResultDTO {

    /**
     * 文章总字数
     */
    private Integer totalCount;

    /**
     * 敏感词数量
     */
    private Integer sensitiveCount;

    /**
     * 违禁词数量
     */

    private Integer prohibitedCount;

    private List<ContentDTO> contentDTOS;

    private List<DetailsDTO> detailsDTOS;



    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ContentDTO {
        private String content;
        private String color;
        private String spell;
        private Boolean status;

    }


    @NoArgsConstructor
    @Data
    public static class DetailsDTO {
        private String id;
        private String title;
        private String type;
        private String content;
        private String weijin_type;
    }
}
