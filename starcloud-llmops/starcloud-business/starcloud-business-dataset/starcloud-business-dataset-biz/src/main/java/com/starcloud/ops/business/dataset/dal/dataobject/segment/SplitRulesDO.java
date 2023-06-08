package com.starcloud.ops.business.dataset.dal.dataobject.segment;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("llm_split_rules")
@KeySequence("llm_split_rules")
public class SplitRulesDO extends TenantBaseDO {

    private static final long serialVersionUID = -9155374188984988756L;

    private String id;

    private String datasetId;

    private String documentId;

    private Boolean automatic;

    private String rules;
}
