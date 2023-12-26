package com.starcloud.ops.business.user.service.tag;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigPageReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagConfigDO;


import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 会员标签 Service 接口
 *
 * @author 芋道源码
 */
public interface AdminUserTagConfigService {

    /**
     * 创建会员标签
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTag(@Valid AdminUserTagConfigCreateReqVO createReqVO);

    /**
     * 更新会员标签
     *
     * @param updateReqVO 更新信息
     */
    void updateTag(@Valid AdminUserTagConfigUpdateReqVO updateReqVO);

    /**
     * 删除会员标签
     *
     * @param id 编号
     */
    void deleteTag(Long id);

    /**
     * 获得会员标签
     *
     * @param id 编号
     * @return 会员标签
     */
    AdminUserTagConfigDO getTag(Long id);

    /**
     * 获得会员标签列表
     *
     * @param ids 编号
     * @return 会员标签列表
     */
    List<AdminUserTagConfigDO> getTagList(Collection<Long> ids);

    /**
     * 获得会员标签分页
     *
     * @param pageReqVO 分页查询
     * @return 会员标签分页
     */
    PageResult<AdminUserTagConfigDO> getTagPage(AdminUserTagConfigPageReqVO pageReqVO);

    /**
     * 获取标签列表
     *
     * @return 标签列表
     */
    List<AdminUserTagConfigDO> getTagList();

    /**
     * 获取新用户标签
     *
     * @return 标签列表
     */
     Long getNewUserTagId();

}
