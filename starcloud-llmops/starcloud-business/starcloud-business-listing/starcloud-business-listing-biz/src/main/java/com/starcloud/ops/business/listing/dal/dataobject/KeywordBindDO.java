package com.starcloud.ops.business.listing.dal.dataobject;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.listing.enums.KeywordBindTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@TableName("listing_keyword_bind")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class KeywordBindDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 词库id
     */
    private Long dictId;

    /**
     * 草稿Id
     */
    private Long draftId;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 类型  词库/草稿
     *
     * {@link KeywordBindTypeEnum}
     */
    private String type;

    /**
     * 标签
     */
    private String tag;

    /**
     * 启用/禁用
     */
    private Boolean enable;


}
