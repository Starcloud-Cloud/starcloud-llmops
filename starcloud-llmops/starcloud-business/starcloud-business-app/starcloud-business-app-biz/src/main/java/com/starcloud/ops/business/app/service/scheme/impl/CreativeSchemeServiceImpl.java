package com.starcloud.ops.business.app.service.scheme.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.convert.scheme.CreativeSchemeConvert;
import com.starcloud.ops.business.app.dal.databoject.scheme.CreativeSchemeDO;
import com.starcloud.ops.business.app.dal.mysql.scheme.CreativeSchemeMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.scheme.CreativeSchemeTypeEnum;
import com.starcloud.ops.business.app.service.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 创作方案服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Slf4j
@Service
public class CreativeSchemeServiceImpl implements CreativeSchemeService {

    @Resource
    private CreativeSchemeMapper creativeSchemeMapper;

    /**
     * 获取创作方案详情
     *
     * @param uid 创作方案UID
     * @return 创作方案详情
     */
    @Override
    public CreativeSchemeRespVO get(String uid) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(uid);
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);
        return CreativeSchemeConvert.INSTANCE.convertResponse(creativeScheme);
    }

    /**
     * 获取创作方案列表
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    @Override
    public List<CreativeSchemeRespVO> list(CreativeSchemeListReqVO query) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        query.setLoginUserId(String.valueOf(loginUserId));
        List<CreativeSchemeDO> list = creativeSchemeMapper.list(query);
        return CreativeSchemeConvert.INSTANCE.convertList(list);
    }

    /**
     * 分页查询创作方案
     *
     * @param query 查询条件
     * @return 创作方案列表
     */
    @Override
    public PageResult<CreativeSchemeRespVO> page(CreativeSchemePageReqVO query) {
        IPage<CreativeSchemeDO> page = creativeSchemeMapper.page(PageUtil.page(query), query);
        return CreativeSchemeConvert.INSTANCE.convertPage(page);
    }

    /**
     * 创建创作方案
     *
     * @param request 创作方案请求
     */
    @Override
    public void create(CreativeSchemeReqVO request) {
        handlerAndValidate(request);
        if (creativeSchemeMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_NAME_EXIST);
        }
        CreativeSchemeDO creativeScheme = CreativeSchemeConvert.INSTANCE.convertCreateRequest(request);
        creativeSchemeMapper.insert(creativeScheme);
    }

    /**
     * 复制创作方案
     *
     * @param request 请求
     */
    @Override
    public void copy(UidRequest request) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(request.getUid());
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);

        CreativeSchemeDO scheme = new CreativeSchemeDO();
        scheme.setUid(IdUtil.simpleUUID());
        scheme.setName(getCopyName(creativeScheme.getName()));
        scheme.setType(creativeScheme.getType());
        scheme.setCategory(creativeScheme.getCategory());
        scheme.setTags(creativeScheme.getTags());
        scheme.setDescription(creativeScheme.getDescription());
        scheme.setReferences(creativeScheme.getReferences());
        scheme.setConfiguration(creativeScheme.getConfiguration());
        scheme.setCopyWritingExample(creativeScheme.getCopyWritingExample());
        scheme.setImageExample(creativeScheme.getImageExample());
        scheme.setCreateTime(LocalDateTime.now());
        scheme.setUpdateTime(LocalDateTime.now());
        scheme.setDeleted(Boolean.FALSE);
        creativeSchemeMapper.insert(scheme);
    }

    /**
     * 修改创作方案
     *
     * @param request 创作方案请求
     */
    @Override
    public void modify(CreativeSchemeModifyReqVO request) {
        handlerAndValidate(request);
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(request.getUid());
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);
        // 如果修改了名称，校验名称是否重复
        if (!creativeScheme.getName().equals(request.getName()) && creativeSchemeMapper.distinctName(request.getName())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_SCHEME_NAME_EXIST);
        }
        CreativeSchemeDO scheme = CreativeSchemeConvert.INSTANCE.convertModifyRequest(request);
        scheme.setId(creativeScheme.getId());
        creativeSchemeMapper.updateById(scheme);
    }

    /**
     * 删除创作方案
     *
     * @param uid 创作方案UID
     */
    @Override
    public void delete(String uid) {
        CreativeSchemeDO creativeScheme = creativeSchemeMapper.get(uid);
        AppValidate.notNull(creativeScheme, ErrorCodeConstants.CREATIVE_SCHEME_NOT_EXIST);
        creativeSchemeMapper.deleteById(creativeScheme.getId());
    }

    /**
     * 处理请求并进行验证
     *
     * @param request 创作方案请求对象
     */
    private void handlerAndValidate(CreativeSchemeReqVO request) {
        // 如果是普通用户或者为空，强制设置为用户类型
        if (UserUtils.isNotAdmin() || StringUtils.isBlank(request.getType())) {
            request.setType(CreativeSchemeTypeEnum.USER.name());
        }
        if (StringUtils.isBlank(request.getDescription())) {
            request.setDescription(StringUtils.EMPTY);
        }
    }

    /**
     * 生成一个复制名称的私有方法
     *
     * @param name 原始名称
     * @return 复制名称
     */
    private String getCopyName(String name) {
        String copyName = name + "-Copy";
        if (!creativeSchemeMapper.distinctName(copyName)) {
            return copyName;
        }
        return getCopyName(copyName);
    }
}
