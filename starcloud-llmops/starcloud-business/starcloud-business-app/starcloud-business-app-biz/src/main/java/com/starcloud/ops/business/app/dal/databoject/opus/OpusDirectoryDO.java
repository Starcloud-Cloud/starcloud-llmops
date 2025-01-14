package com.starcloud.ops.business.app.dal.databoject.opus;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("llm_opus_directory")
public class OpusDirectoryDO extends DeptBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 目录uid
     */
    private String dirUid;

    /**
     * 作品集uid
     */
    private String opusUid;

    /**
     * 目录名称
     */
    private String dirName;

    /**
     * 目录描述
     */
    private String dirDesc;

    /**
     * 父目录uid
     */
    private String parentUid;

    /**
     * 目录顺序
     */
    @TableField("`order`")
    private Integer order;

}
