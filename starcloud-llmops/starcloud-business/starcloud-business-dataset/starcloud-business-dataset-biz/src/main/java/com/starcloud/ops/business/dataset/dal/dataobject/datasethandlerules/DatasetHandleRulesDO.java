package com.starcloud.ops.business.dataset.dal.dataobject.datasethandlerules;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


@TableName("llm_dataset_handle_rules")
@KeySequence("llm_dataset_handle_rules")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetHandleRulesDO extends TenantBaseDO {

    private static final long serialVersionUID = -9155374188984988756L;

    /**
     * 主键 ID
     */
    @TableId
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 过滤规则组
     */
    private String ruleFilter;

    /**
     * 清洗规则
     */
    private String cleanRule;

    /**
     * 拆分规则
     */
    private String splitRule;

    /**
     * 规则类型
     */
    private String ruleType;

    /**
     * 规则来源
     */
    private String fromScene;


    /**
     * 状态 默认开启
     */
    private Boolean enable;


    /**
     * 数据集 ID
     */
    private Long datasetId;

}
