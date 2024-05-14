package com.starcloud.ops.business.listing.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("listing_draft")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ListingDraftDO extends TenantBaseDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    /**
     * 所属站点
     */
    private String endpoint;

    /**
     * asin
     */
    private String asin;

    /**
     * 草稿配置
     */
    private String config;

    /**
     * 草稿总分
     */
    private Double score;

    /**
     *  score / 选项数量
     */
    private Double scoreProportion;

    /**
     * listing 标题
     */
    private String title;

    /**
     * listing 五点描述
     */
    private String fiveDesc;

    /**
     * listing 产品描述
     */
    private String productDesc;

    /**
     * listing 搜索词
     */
    private String searchTerm;

    /**
     * 各项分数
     */
    private String itemScore;

    /**
     * 命中搜索量
     */
    private Long matchSearchers;

    /**
     * 总搜索量
     */
    private Long totalSearches;

    /**
     * 匹配关键词数/总关键词数
     */
    private Double searchersProportion;

    /**
     * 草稿版本
     */
    private Integer version;

    /**
     * 关键词状态
     * {@link com.starcloud.ops.business.listing.enums.AnalysisStatusEnum}
     */
    private String status;

    /**
     * 关键词分析耗时
     */
    private Long analysisTime;

    /**
     * 执行耗时
     */
    private Long executeTime;

    /**
     * 执行失败Msg
     */
    private String errorMsg;


    /**
     * 执行状态
     */
    private String executeStatus;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 草稿名称
     */
    private String draftName;

}
