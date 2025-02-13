package com.starcloud.ops.business.user.service.rights;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.*;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;

import java.util.List;

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
    PageResult<AdminUserRightsDO> getRightsPage(Long userId, AppAdminUserRightsPageReqVO pageVO);

    /**
     * 【会员】获得权益记录分页
     *
     * @param userId 用户编号
     * @param pageVO 分页查询
     * @return 签到记录分页
     */
    PageResult<AppAdminUserRightsRespVO> getRightsPage2(Long userId, AppAdminUserRightsPageReqVO pageVO);



    /**
     * 通过业务 ID 和业务类型获取权益数据
     *
     * @param bizType 业务类型
     * @param bizId   业务编号
     * @param userId  用户编号
     * @return 权益数据
     */
    AdminUserRightsDO getRecordByBiz(Integer bizType, Long bizId,Long userId);

    /**
     * 获取权益数据汇总
     *
     * @param userId 用户编号
     * @return 权益数据汇总
     */
    List<AdminUserRightsCollectRespVO> getGroupRightsCollect(Long userId);

    /**
     * 获取权益数据汇总
     *
     * @param userId 用户编号
     * @return 权益数据汇总
     */
    List<AdminUserRightsCollectRespVO> getRightsCollect(Long userId);


    /**
     * 创建用户权益记录
     *
     * @param userId     用户ID
     * @param magicBean  魔法豆
     * @param magicImage 图片
     * @param bizType    业务类型
     * @param bizId      业务编号
     */
    void createRights(Long userId, Integer magicBean, Integer magicImage, Integer matrixBean, Integer timeNums, Integer timeRange, Integer bizType, String bizId, Long LevelId,Integer templates);


    /**
     * 创建用户权益记录
     *
     * @param addRightsDTO 新增权益 DTO
     */
    void createRights(AddRightsDTO addRightsDTO);


    /**
     * 创建用户权益记录
     *
     * @param rightsDTO 权益配置
     * @param bizType   业务类型
     * @param bizId     业务编号
     */
    AdminUserRightsDO createRights(AdminUserRightsAndLevelCommonDTO rightsDTO, Long userId, Integer bizType, String bizId);


    /**
     * 校验权益是否可供扣除
     *
     * @param userId      用户 ID
     * @param rightsType  权益类型
     * @param rightAmount 权益数量
     */
    Boolean calculateUserRightsEnough(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount);

    /**
     * 权益扣减
     *
     * @param userId      使用者用户 ID
     * @param teamOwnerId 团队所属者用户 ID (如果为空，则不属于团队消耗，不为空或者与 userId 值相同 则代表是团队下自己消耗执行)
     * @param teamId      团队ID
     * @param rightsType  权益类型
     * @param rightAmount 权益数量
     * @param bizType     业务类型
     * @param bizId       业务编号
     */
    void reduceRights(Long userId, Long teamOwnerId, Long teamId, AdminUserRightsTypeEnum rightsType, Integer rightAmount, AdminUserRightsBizTypeEnum bizType, String bizId);

    /**
     * 权益扣减
     *
     * @param reduceRightsDTO 权益扣减DTO
     */
    void reduceRights(ReduceRightsDTO reduceRightsDTO);


    /**
     * 权益过期提醒
     *
     * @param userId 用户 ID
     */
    NotifyExpiringRightsRespVO notifyExpiringRights(Long userId);


    /**
     * 【系统】自动过期用户权益
     *
     * @return 过期数量
     */
    Integer expireRights();


    void expireRightsBySystem(AdminUserRightsDO rightsDO);


    /**
     * 获取指定类型的剩余数量-有效期内的
     * @param type 权益类型
     */
    Integer getUsedNumsByType(Integer type);

    /**
     * 获取指定类型的总数量-有效期内的
     * @param rightsType 权益类型 @{AdminUserRightsTypeEnum}
     */
    Integer getEffectiveNumsByType(Long userId,AdminUserRightsTypeEnum rightsType);
}
