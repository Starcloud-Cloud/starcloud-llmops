package com.starcloud.ops.business.app.dal.databoject.opus;


import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("llm_opus")
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
    private String opusImage;

}
