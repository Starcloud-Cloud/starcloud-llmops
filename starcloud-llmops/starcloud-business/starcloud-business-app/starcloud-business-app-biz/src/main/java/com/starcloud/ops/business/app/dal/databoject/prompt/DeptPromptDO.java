package com.starcloud.ops.business.app.dal.databoject.prompt;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_dept_prompt")
public class DeptPromptDO extends DeptBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    private String name;

    private String description;

    private String promptText;

    private Boolean sysEnable;

}
