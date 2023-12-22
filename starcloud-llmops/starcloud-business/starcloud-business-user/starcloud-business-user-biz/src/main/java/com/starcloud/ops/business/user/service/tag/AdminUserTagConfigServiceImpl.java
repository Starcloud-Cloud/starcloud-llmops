package com.starcloud.ops.business.user.service.tag;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigPageReqVO;
import com.starcloud.ops.business.user.controller.admin.tag.vo.AdminUserTagConfigUpdateReqVO;
import com.starcloud.ops.business.user.convert.tag.MemberTagConvert;
import com.starcloud.ops.business.user.dal.dataobject.tag.AdminUserTagConfigDO;
import com.starcloud.ops.business.user.dal.mysql.tag.AdminUserTagConfigMapper;
import com.starcloud.ops.business.user.dal.mysql.tag.AdminUserTagMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;

/**
 * 会员标签 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AdminUserTagConfigServiceImpl implements AdminUserTagConfigService {

    @Resource
    private AdminUserTagConfigMapper adminUserTagConfigMapper;
    @Resource
    private AdminUserTagMapper adminUserTagMapper;


    @Override
    public Long createTag(AdminUserTagConfigCreateReqVO createReqVO) {
        // 校验名称唯一
        validateTagNameUnique(null, createReqVO.getName());
        // 插入
        AdminUserTagConfigDO tag = MemberTagConvert.INSTANCE.convert(createReqVO);
        adminUserTagConfigMapper.insert(tag);
        // 返回
        return tag.getId();
    }

    @Override
    public void updateTag(AdminUserTagConfigUpdateReqVO updateReqVO) {
        // 校验存在
        validateTagExists(updateReqVO.getId());
        // 校验名称唯一
        validateTagNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 更新
        AdminUserTagConfigDO updateObj = MemberTagConvert.INSTANCE.convert(updateReqVO);
        adminUserTagConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteTag(Long id) {
        // 校验存在
        validateTagExists(id);
        // 校验标签下是否有用户
        validateTagHasUser(id);
        // 删除
        adminUserTagConfigMapper.deleteById(id);
    }

    private void validateTagExists(Long id) {
        if (adminUserTagConfigMapper.selectById(id) == null) {
            throw exception(TAG_NOT_EXISTS);
        }
    }

    private void validateTagNameUnique(Long id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }
        AdminUserTagConfigDO tag = adminUserTagConfigMapper.selectByName(name);
        if (tag == null) {
            return;
        }

        // 如果 id 为空，说明不用比较是否为相同 id 的标签
        if (id == null) {
            throw exception(TAG_NAME_EXISTS);
        }
        if (!tag.getId().equals(id)) {
            throw exception(TAG_NAME_EXISTS);
        }
    }

    void validateTagHasUser(Long id) {
        Long count = adminUserTagMapper.selectCountByTagId(id);
        if (count > 0) {
            throw exception(TAG_HAS_USER);
        }
    }

    @Override
    public AdminUserTagConfigDO getTag(Long id) {
        return adminUserTagConfigMapper.selectById(id);
    }

    @Override
    public List<AdminUserTagConfigDO> getTagList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return adminUserTagConfigMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<AdminUserTagConfigDO> getTagPage(AdminUserTagConfigPageReqVO pageReqVO) {
        return adminUserTagConfigMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AdminUserTagConfigDO> getTagList() {
        return adminUserTagConfigMapper.selectList();
    }

    /**
     * 获取新用户标签
     *
     * @return 标签列表
     */
    @Override
    public Long getNewUserTagId() {
        return adminUserTagConfigMapper.selectNewUserTag().getId();
    }

}
