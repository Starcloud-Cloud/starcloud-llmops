package com.starcloud.ops.business.user.service.tag;

import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagDO;

import javax.validation.Valid;

/**
 * 会员标签 Service 接口
 *
 * @author 芋道源码
 */
public interface AdminUserTagService {


    /**
     * 更新会员标签
     *
     * @param updateReqVO 更新信息
     */
    void updateTag(@Valid AdminUserTagConfigUpdateReqVO updateReqVO);


    /**
     * 获得会员标签
     *
     * @param userId 会员编号
     */
    AdminUserTagDO getTagByUserId(Long userId);


    /**
     * 添加新用户标签
     *
     * @param userId 会员编号
     */
    void addNewUserTag(Long userId);

    /**
     * 添加新用户标签
     *
     * @param userId 会员编号
     */
    Boolean validateNewUser(Long userId);


}
