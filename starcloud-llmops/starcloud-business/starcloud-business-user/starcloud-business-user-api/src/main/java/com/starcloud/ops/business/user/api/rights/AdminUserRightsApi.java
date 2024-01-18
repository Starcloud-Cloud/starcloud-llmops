package com.starcloud.ops.business.user.api.rights;

import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;

import javax.validation.constraints.Min;

/**
 * 用户积分的 API 接口
 *
 * @author owen
 */
public interface AdminUserRightsApi {

    /**
     * 增加用户积分
     *
     * @param userId     用户编号
     * @param magicBean  积分
     * @param magicImage 积分
     * @param bizType    业务类型 {@link AdminUserRightsBizTypeEnum}
     * @param bizId      业务编号
     */
    void addRights(Long userId, @Min(value = 1L, message = "魔法豆必须是正数") Integer magicBean, @Min(value = 1L, message = "图片值必须是正数") Integer magicImage, Integer rightsTimeNums, Integer rightsTimeRange,
                   Integer bizType, String bizId,Long levelId);

    /**
     * 减少用户积分
     *
     * @param userId      用户编号
     * @param rightType   权益类型 {@link AdminUserRightsTypeEnum}
     * @param rightAmount 权益
     * @param bizType     业务类型 {@link AdminUserRightsBizTypeEnum}
     * @param bizId       业务编号
     */
    void reduceRights(Long userId, AdminUserRightsTypeEnum rightType, @Min(value = 1L, message = "积分必须是正数") Integer rightAmount,
                      Integer bizType, String bizId);

    /**
     * 判断权益是否充足
     *
     * @param userId
     * @param rightsType
     * @param rightAmount
     * @return
     */
    Boolean calculateUserRightsEnough(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount);

}
