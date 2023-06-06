package com.starcloud.ops.business.app.service.template.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.template.dto.TemplateDTO;
import com.starcloud.ops.business.app.api.template.request.TemplatePageQuery;
import com.starcloud.ops.business.app.api.template.request.TemplateRequest;
import com.starcloud.ops.business.app.api.template.request.TemplateUpdateRequest;
import com.starcloud.ops.business.app.convert.TemplateConvert;
import com.starcloud.ops.business.app.dal.databoject.template.TemplateDO;
import com.starcloud.ops.business.app.dal.mysql.template.TemplateMapper;
import com.starcloud.ops.business.app.dal.redis.template.RecommendedTemplatesRedisDAO;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.enums.template.TemplateTypeEnum;
import com.starcloud.ops.business.app.exception.TemplateException;
import com.starcloud.ops.business.app.exception.TemplateMarketException;
import com.starcloud.ops.business.app.service.template.TemplateService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.dto.SortQuery;
import com.starcloud.ops.framework.common.api.enums.SortType;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模版管理服务实现类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Slf4j
@Service
public class TemplateServiceImpl implements TemplateService {

    @Resource
    private TemplateMapper templateMapper;

    @Resource
    private RecommendedTemplatesRedisDAO recommendedTemplatesRedisDAO;

    /**
     * 查询推荐的模版列表
     *
     * @return 模版列表
     */
    @Override
    public List<TemplateDTO> listRecommendedTemplates() {
        // 缓存中获取，如果有则直接返回
        List<TemplateDTO> list = recommendedTemplatesRedisDAO.get();
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }

        // 缓存中没有，则从数据库中获取最新数据，并且存入缓存，返回数据
        return recommendedTemplatesRedisDAO.set();
    }

    /**
     * 分页查询模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<TemplateDTO> page(TemplatePageQuery query) {
        // 默认按照更新时间倒序
        query.setSorts(Collections.singletonList(SortQuery.of("update_time", SortType.DESC.name())));

        // 构建查询条件
        LambdaQueryWrapper<TemplateDO> wrapper = buildPageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), TemplateDO::getName, query.getName())
                .eq(StringUtils.isNotBlank(query.getType()), TemplateDO::getType, query.getType())
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, StateEnum.ENABLE.getCode());

        // 执行分页查询
        Page<TemplateDO> page = templateMapper.selectPage(PageUtil.page(query), wrapper);
        List<TemplateDTO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream().map(TemplateConvert::convert).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 分页查询下载的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<TemplateDTO> pageDownloadTemplates(TemplatePageQuery query) {
        query.setType(TemplateTypeEnum.DOWNLOAD_TEMPLATE.name());
        return page(query);
    }

    /**
     * 分页查询我的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<TemplateDTO> pageMyTemplate(TemplatePageQuery query) {
        query.setType(TemplateTypeEnum.MY_TEMPLATE.name());
        return page(query);
    }

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    @Override
    public TemplateDTO getById(Long id) {
        TemplateDO templateDO = templateMapper.selectById(id);
        Assert.notNull(templateDO, () -> TemplateException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, id));
        return TemplateConvert.convert(templateDO);
    }

    /**
     * 根据模版 UID 获取模版详情
     *
     * @param uid 模版 UID
     * @return 模版详情
     */
    @Override
    public TemplateDTO getByUid(String uid) {
        LambdaQueryWrapper<TemplateDO> wrapper = buildBaseQueryWrapper().eq(TemplateDO::getUid, uid);
        TemplateDO templateDO = templateMapper.selectOne(wrapper);
        Assert.notNull(templateDO, () -> TemplateException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, uid));
        return TemplateConvert.convert(templateDO);
    }

    /**
     * 创建模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    @Override
    public Boolean create(TemplateRequest request) {
        try {
            duplicateNameVerification(request);
            TemplateDO templateDO = TemplateConvert.convertCreate(request);
            // 生成唯一 ID
            templateDO.setUid(IdUtil.fastSimpleUUID());
            templateMapper.insert(templateDO);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedTemplatesRedisDAO.resetByType(templateDO.getType());
            return Boolean.TRUE;
        } catch (Exception e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_CREATE_FAILED, e.getMessage());
        }
    }

    /**
     * 复制模版
     *
     * @param request 模版信息
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(TemplateRequest request) {
        try {
            String name = request.getName() + "-Copy";
            request.setName(name);
            duplicateNameVerification(request);
            TemplateDO templateDO = TemplateConvert.convertCreate(request);
            // 生成唯一 ID
            templateDO.setUid(IdUtil.fastSimpleUUID());
            templateMapper.insert(templateDO);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedTemplatesRedisDAO.resetByType(templateDO.getType());
            return Boolean.TRUE;
        } catch (Exception e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_COPY_FAILED, e.getMessage());
        }
    }

    /**
     * 更新模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    @Override
    public Boolean modify(TemplateUpdateRequest request) {
        try {
            duplicateNameVerification(request);
            TemplateDO templateDO = TemplateConvert.convertModify(request);
            LambdaUpdateWrapper<TemplateDO> wrapper = Wrappers.lambdaUpdate(TemplateDO.class)
                    .eq(TemplateDO::getUid, request.getUid())
                    .eq(TemplateDO::getDeleted, Boolean.FALSE)
                    .eq(TemplateDO::getStatus, StateEnum.ENABLE.getCode());
            templateMapper.update(templateDO, wrapper);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedTemplatesRedisDAO.resetByType(templateDO.getType());
            return Boolean.TRUE;
        } catch (Exception e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_MODIFY_FAILED, e.getMessage());
        }
    }

    /**
     * 删除模版
     *
     * @param id 模版ID
     * @return 是否删除成功
     */
    @Override
    public Boolean delete(Long id) {
        try {
            TemplateDO templateDO = templateMapper.selectById(id);
            Assert.notNull(templateDO, () -> TemplateException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, id));
            templateMapper.deleteById(id);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedTemplatesRedisDAO.resetByType(templateDO.getType());
            return Boolean.TRUE;
        } catch (IllegalArgumentException e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 根据模版 UID 删除模版
     *
     * @param uid 模版 UID
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteByUid(String uid) {
        try {
            TemplateDO templateDO = templateMapper.selectOne(buildBaseQueryWrapper().eq(TemplateDO::getUid, uid));
            Assert.notNull(templateDO, () -> TemplateException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, uid));
            templateMapper.deleteById(templateDO.getId());
            // 如果新增的是系统模版，则需要更新缓存
            recommendedTemplatesRedisDAO.resetByType(templateDO.getType());
            return Boolean.TRUE;
        } catch (IllegalArgumentException e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 校验模版是否已经下载过
     *
     * @param marketUid 模版市场特有的 key，唯一。
     * @return 是否已经下载, true: 已经下载, false: 未下载
     */
    @Override
    public Boolean verifyHasDownloaded(String marketUid) {
        try {
            LambdaQueryWrapper<TemplateDO> wrapper = buildBaseQueryWrapper()
                    .eq(TemplateDO::getMarketUid, marketUid)
                    .eq(TemplateDO::getType, TemplateTypeEnum.DOWNLOAD_TEMPLATE.name());
            Long count = templateMapper.selectCount(wrapper);
            return count > 0;
        } catch (Exception e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_VERIFY_HAS_DOWNLOADED_FAILED, e.getMessage());
        }
    }


    /**
     * 模版名称重复校验, 重复抛出异常
     *
     * @param name 模版名称
     * @return 是否重复 true: 重复, false: 不重复
     */
    public Boolean duplicateNameVerification(String name) {
        try {
            LambdaQueryWrapper<TemplateDO> wrapper = buildBaseQueryWrapper().eq(TemplateDO::getName, name);
            Long count = templateMapper.selectCount(wrapper);
            return count > 0;
        } catch (TemplateMarketException e) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_DEFAULT_ERROR, e.getMessage());
        }
    }

    /**
     * 模版名称重复校验, 重复抛出异常
     *
     * @param request 模版信息
     */
    private void duplicateNameVerification(TemplateRequest request) {
        if (duplicateNameVerification(request.getName())) {
            throw TemplateException.exception(AppResultCode.TEMPLATE_NAME_DUPLICATE, request.getName());
        }
    }

    /**
     * 构建基础查询条件
     *
     * @return 查询条件
     */
    public static LambdaQueryWrapper<TemplateDO> buildBaseQueryWrapper() {
        return Wrappers.lambdaQuery(TemplateDO.class)
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, StateEnum.ENABLE.getCode());
    }

    /**
     * 构建分页查询条件, 仅查询指定字段
     *
     * @return 查询条件
     */
    public static LambdaQueryWrapper<TemplateDO> buildPageQueryWrapper() {
        return Wrappers.lambdaQuery(TemplateDO.class).select(
                        TemplateDO::getUid,
                        TemplateDO::getMarketUid,
                        TemplateDO::getType,
                        TemplateDO::getLogotype,
                        TemplateDO::getSourceType,
                        TemplateDO::getVersion,
                        TemplateDO::getName,
                        TemplateDO::getDescription,
                        TemplateDO::getIcon,
                        TemplateDO::getTags,
                        TemplateDO::getCategories,
                        TemplateDO::getScenes,
                        TemplateDO::getCreator,
                        TemplateDO::getUpdater,
                        TemplateDO::getCreateTime,
                        TemplateDO::getUpdateTime
                )
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, StateEnum.ENABLE.getCode());
    }
}
