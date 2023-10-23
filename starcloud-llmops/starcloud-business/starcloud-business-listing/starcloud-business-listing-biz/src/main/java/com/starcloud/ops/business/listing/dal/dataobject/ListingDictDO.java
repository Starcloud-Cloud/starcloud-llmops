package com.starcloud.ops.business.listing.dal.dataobject;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@TableName("listing_dict")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ListingDictDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    private String name;

    /**
     * 所属站点
     */
    private String endpoint;

    /**
     * 启用/禁用
     */
    private Boolean enable;

    /**
     * 关键词状态
     * {@link com.starcloud.ops.business.listing.enums.AnalysisStatusEnum}
     */
    private String status;

    /**
     * 关键词分析耗时
     */
    private Long analysisTime;


}
