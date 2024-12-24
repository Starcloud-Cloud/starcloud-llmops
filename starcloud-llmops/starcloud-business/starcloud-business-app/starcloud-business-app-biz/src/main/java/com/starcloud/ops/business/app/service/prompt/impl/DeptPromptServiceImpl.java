package com.starcloud.ops.business.app.service.prompt.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.PromptBaseVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.DeptPromptModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.PromptPageReqVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.resp.PromptRespVO;
import com.starcloud.ops.business.app.convert.prompt.PromptConvert;
import com.starcloud.ops.business.app.dal.databoject.prompt.DeptPromptDO;
import com.starcloud.ops.business.app.dal.mysql.prompt.DeptPromptMapper;
import com.starcloud.ops.business.app.service.prompt.DeptPromptService;
import com.starcloud.ops.business.app.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.NO_PERMISSION;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.PROMPT_NOT_EXIST;

@Slf4j
@Service
public class DeptPromptServiceImpl implements DeptPromptService {

    @Resource
    private DeptPromptMapper deptPromptMapper;

    @Resource
    private PermissionApi permissionApi;

    @Override
    public PromptRespVO create(PromptBaseVO promptBaseVO) {
        DeptPromptDO deptPromptDO = PromptConvert.INSTANCE.convert(promptBaseVO);
        deptPromptDO.setUid(IdUtil.fastSimpleUUID());
        if (BooleanUtils.isTrue(deptPromptDO.getSysEnable())) {
            // check permission
            if (!permissionApi.hasAnyRoles(WebFrameworkUtils.getLoginUserId(), UserUtils.ADMIN_ROLE, UserUtils.OPERATE_ROLE)) {
                throw exception(NO_PERMISSION);
            }
        }
        deptPromptMapper.insert(deptPromptDO);
        return PromptConvert.INSTANCE.convert(deptPromptDO);
    }

    @Override
    public PromptRespVO modify(DeptPromptModifyReqVO reqVO) {
        DeptPromptDO oldPrompt = getByUid(reqVO.getUid());
        DeptPromptDO deptPromptDO = PromptConvert.INSTANCE.convert(reqVO);
        deptPromptDO.setId(oldPrompt.getId());
        if (BooleanUtils.isTrue(deptPromptDO.getSysEnable())) {
            // check permission
            if (!permissionApi.hasAnyRoles(WebFrameworkUtils.getLoginUserId(), UserUtils.ADMIN_ROLE, UserUtils.OPERATE_ROLE)) {
                throw exception(NO_PERMISSION);
            }
        }
        deptPromptMapper.updateById(deptPromptDO);
        return PromptConvert.INSTANCE.convert(deptPromptDO);
    }

    @Override
    public void delete(String uid) {
        DeptPromptDO deptPromptDO = getByUid(uid);
        deptPromptMapper.deleteById(deptPromptDO.getId());
    }

    @Override
    public PageResult<PromptRespVO> page(PromptPageReqVO reqVO) {
        PageResult<DeptPromptDO> page = deptPromptMapper.page(reqVO);
        return PromptConvert.INSTANCE.convert(page);
    }

    @Override
    @DataPermission(enable = false)
    public PageResult<PromptRespVO> sysPage(PromptPageReqVO reqVO) {
        PageResult<DeptPromptDO> page = deptPromptMapper.sysPage(reqVO);
        return PromptConvert.INSTANCE.convert(page);
    }

    private DeptPromptDO getByUid(String uid) {
        DeptPromptDO deptPromptDO = deptPromptMapper.selectByUid(uid);
        if (Objects.isNull(deptPromptDO)) {
            throw exception(PROMPT_NOT_EXIST);
        }
        return deptPromptDO;
    }
}
