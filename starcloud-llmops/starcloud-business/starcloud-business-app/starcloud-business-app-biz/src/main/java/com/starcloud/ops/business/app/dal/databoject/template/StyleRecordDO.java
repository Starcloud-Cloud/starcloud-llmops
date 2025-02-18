package com.starcloud.ops.business.app.dal.databoject.template;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_style_record")
public class StyleRecordDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    private String styleUid;

    private String planUid;
}
