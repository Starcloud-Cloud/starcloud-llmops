package com.starcloud.ops.business.user.service.rights;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;

/**
 * 用户权益 Service 接口
 *
 * @author QingX
 */
public interface AdminUserRightsService {

    /**
     * 【管理员】获得权益分页
     *
     * @param pageReqVO 分页查询
     * @return 签到记录分页
     */
    PageResult<AdminUserRightsDO> getRightsPage(AdminUserRightsPageReqVO pageReqVO);

    /**
     * 【会员】获得权益记录分页
     *
     * @param userId 用户编号
     * @param pageVO 分页查询
     * @return 签到记录分页
     */
    PageResult<AdminUserRightsDO> getRightsPage(Long userId, PageParam pageVO);


    /**
     * 创建用户权益记录
     *
     * @param userId     用户ID
     * @param magicBean  魔法豆
     * @param magicImage 图片
     * @param bizType    业务类型
     * @param bizId      业务编号
     */
    void createRights(Long userId, Integer magicBean, Integer magicImage, AdminUserRightsBizTypeEnum bizType, String bizId);

    /**
     * 校验权益是否可供扣除
     * @param userId      用户 ID
     * @param rightsType  权益类型
     * @param rightAmount 权益数量
     */
    Boolean calculateUserRightsEnough(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount);

    /**
     * 权益扣减
     *
     * @param userId      用户 ID
     * @param rightsType  权益类型
     * @param rightAmount 权益数量
     * @param bizType     业务类型
     * @param bizId       业务编号
     */
    void reduceRights(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount, AdminUserRightsBizTypeEnum bizType, String bizId);


    void expireRights();
}
