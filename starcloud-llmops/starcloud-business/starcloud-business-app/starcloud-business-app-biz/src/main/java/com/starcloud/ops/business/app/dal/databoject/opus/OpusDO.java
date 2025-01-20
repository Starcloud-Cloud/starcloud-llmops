package com.starcloud.ops.business.app.dal.databoject.opus;


import cn.iocoder.yudao.framework.mybatis.core.type.StringListTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "llm_opus", autoResultMap = true)
public class OpusDO extends DeptBaseDO {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作品集uid
     */
    private String opusUid;

    /**
     * 作品集名称
     */
    private String opusName;

    /**
     * 作品集描述
     */
    private String opusDesc;

    /**
     * 作品集类型
     */
    private String opusType;

    /**
     * 作品集图片
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> opusImages;

}
