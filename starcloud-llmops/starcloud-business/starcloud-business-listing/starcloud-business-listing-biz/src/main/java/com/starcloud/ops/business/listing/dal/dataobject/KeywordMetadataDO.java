package com.starcloud.ops.business.listing.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@TableName("listing_keyword_meta_data")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class KeywordMetadataDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 标签
     */
    private String tag;

    /**
     * 数据日期
     */
    private LocalDate dataDate;

    /**
     *  搜索量
     */




}
