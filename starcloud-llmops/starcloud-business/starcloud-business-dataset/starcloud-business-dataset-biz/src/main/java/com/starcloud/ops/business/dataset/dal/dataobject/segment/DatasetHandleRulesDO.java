package com.starcloud.ops.business.dataset.dal.dataobject.segment;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("llm_dataset_handle_rules")
@KeySequence("llm_dataset_handle_rules")
public class DatasetHandleRulesDO extends TenantBaseDO {

    private static final long serialVersionUID = -9155374188984988756L;

    @TableId
    private Long id;

    private Long datasetId;

    private String cleanRule;

    private String splitRule;

}
