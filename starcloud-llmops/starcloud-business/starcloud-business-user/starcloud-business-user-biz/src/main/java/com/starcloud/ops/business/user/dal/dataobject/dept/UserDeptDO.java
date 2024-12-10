package com.starcloud.ops.business.user.dal.dataobject.dept;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("system_user_dept")
@KeySequence("system_user_dept")
public class UserDeptDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 部门中用户的角色
     */
    private Integer deptRole;

    /**
     * 邀请人
     */
    private Long inviteUser;

    /**
     * 消耗总魔法豆
     */
    private Long costPoints;

    /**
     * 消耗总图片
     */
    private Long imageCount;
}
