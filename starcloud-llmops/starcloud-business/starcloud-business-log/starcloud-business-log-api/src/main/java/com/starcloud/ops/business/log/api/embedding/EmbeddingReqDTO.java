package com.starcloud.ops.business.log.api.embedding;

import lombok.Data;

@Data
public class EmbeddingReqDTO {

    /**
     * 索引内容
     */
    private String content;

    /**
     * 文档Id
     */
    private String documentId;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * 消耗token数
     */
    private Long tokens;

    /**
     * 内容hash
     */
    private String textHash;

    /**
     * 类型 文档、query
     */
    private String type;

    /**
     * 用户
     */
    private String userId;

    private String updater;
}
