package com.starcloud.ops.business.listing.service.sellersprite.DTO.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.listing.service.sellersprite.DTO.request
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2023/10/18  16:07
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2023/10/18   AlanCusack    1.0         1.0 Version
 */
@NoArgsConstructor
@Data
public class KeywordMinerRequestDTO {

    /**
     * 批量查询关键词集合
     */
    private List<String> keywordList;
    /**
     * 查询单个关键词
     */
    private String keyword;
    /**
     * 站点
     */
    private Integer market;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 页面大小
     */
    private Integer pageSize;
    /**
     * 历史数据
     */
    private String historyDate;
    /**
     * 排序字段 单个查询是为 21
     */
    private Integer orderBy;
    /**
     * 默认为 true
     */
    private Boolean desc;

    private Integer filterRootWord;

    /**
     *     默认 0
     */
    private Integer matchType;
    /**
     * 默认 false
     */
    private Boolean amazonChoice;
}
