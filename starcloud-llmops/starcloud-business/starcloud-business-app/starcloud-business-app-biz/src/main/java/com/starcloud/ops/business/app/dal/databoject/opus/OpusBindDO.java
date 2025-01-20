package com.starcloud.ops.business.app.dal.databoject.opus;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("llm_opus_bind")
public class OpusBindDO extends DeptBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uid
     */
    private String bindUid;

    /**
     * 作品集uid
     */
    private String opusUid;

    /**
     * 目录uid
     */
    private String dirUid;

    /**
     * 创作内容uid
     */
    private String creativeContentUid;

    /**
     * 视频
     */
    private Boolean video;

    /**
     * 开启
     */
    private Boolean open;
}
