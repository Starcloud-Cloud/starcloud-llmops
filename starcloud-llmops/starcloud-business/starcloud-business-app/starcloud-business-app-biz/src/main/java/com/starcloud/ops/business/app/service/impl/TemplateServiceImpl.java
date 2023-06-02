package com.starcloud.ops.business.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.dto.TemplateDTO;
import com.starcloud.ops.business.app.api.request.TemplatePageQuery;
import com.starcloud.ops.business.app.api.request.TemplateRequest;
import com.starcloud.ops.business.app.api.request.TemplateUpdateRequest;
import com.starcloud.ops.business.app.convert.TemplateConvert;
import com.starcloud.ops.business.app.dal.databoject.TemplateDO;
import com.starcloud.ops.business.app.dal.mysql.TemplateMapper;
import com.starcloud.ops.business.app.dal.redis.RecommendedTemplatesRedisDAO;
import com.starcloud.ops.business.app.enums.TemplateTypeEnum;
import com.starcloud.ops.business.app.service.TemplateService;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
     * 分页查询下载的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<TemplateDTO> pageDownloadTemplates(TemplatePageQuery query) {
        LambdaQueryWrapper<TemplateDO> wrapper = Wrappers.lambdaQuery();
        wrapper.likeLeft(TemplateDO::getName, query.getName())
                .eq(TemplateDO::getType, TemplateTypeEnum.DOWNLOAD_TEMPLATE.name())
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, 0)
                .orderByDesc(TemplateDO::getUpdateTime);
        Page<TemplateDO> page = templateMapper.selectPage(ofPage(query), wrapper);
        return PageResp.of(CollectionUtil.emptyIfNull(page.getRecords()).stream().map(TemplateConvert::convert).collect(Collectors.toList()),
                page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 分页查询我的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<TemplateDTO> pageMyTemplate(TemplatePageQuery query) {
        LambdaQueryWrapper<TemplateDO> wrapper = Wrappers.lambdaQuery();
        wrapper.likeLeft(TemplateDO::getName, query.getName())
                .eq(TemplateDO::getType, TemplateTypeEnum.MY_TEMPLATE.name())
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, 0)
                .orderByDesc(TemplateDO::getUpdateTime);
        Page<TemplateDO> page = templateMapper.selectPage(ofPage(query), wrapper);
        return PageResp.of(CollectionUtil.emptyIfNull(page.getRecords()).stream().map(TemplateConvert::convert).collect(Collectors.toList()),
                page.getTotal(), page.getCurrent(), page.getSize());
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
        Assert.notNull(templateDO, "The Id: " + id + " template does not exist.");
        return TemplateConvert.convert(templateDO);
    }

    /**
     * 创建模版
     *
     * @param request 模版信息
     */
    @Override
    public Long create(TemplateRequest request) {
        duplicateNameVerification(request.getName());
        TemplateDO templateDO = TemplateConvert.convert(request);
        templateMapper.insert(templateDO);
        // 如果新增的是系统模版，则需要更新缓存
        if (TemplateTypeEnum.SYSTEM_TEMPLATE.name().equals(templateDO.getType())) {
            recommendedTemplatesRedisDAO.set();
        }
        return templateDO.getId();
    }

    /**
     * 复制模版
     *
     * @param request 模版信息
     */
    @Override
    public Long copy(TemplateRequest request) {
        String name = request.getName() + "-Copy";
        duplicateNameVerification(name);
        TemplateDO templateDO = TemplateConvert.convert(request);
        templateMapper.insert(templateDO);
        // 如果新增的是系统模版，则需要更新缓存
        if (TemplateTypeEnum.SYSTEM_TEMPLATE.name().equals(templateDO.getType())) {
            recommendedTemplatesRedisDAO.set();
        }
        return templateDO.getId();
    }

    /**
     * 更新模版
     *
     * @param request 模版信息
     */
    @Override
    public Long modify(TemplateUpdateRequest request) {
        duplicateNameVerification(request.getName());
        TemplateDO templateDO = TemplateConvert.convert(request);
        templateMapper.updateById(templateDO);
        // 如果更新的是系统模版，则需要更新缓存
        if (TemplateTypeEnum.SYSTEM_TEMPLATE.name().equals(templateDO.getType())) {
            recommendedTemplatesRedisDAO.set();
        }
        return templateDO.getId();
    }

    /**
     * 删除模版
     *
     * @param id 模版ID
     */
    @Override
    public void delete(Long id) {
        templateMapper.deleteById(id);
    }

    /**
     * 校验模版是否已经下载过
     *
     * @param marketKey 模版市场特有的 key，唯一。
     * @return 是否已经下载
     */
    @Override
    public Boolean verifyHasDownloaded(String marketKey) {
        LambdaQueryWrapper<TemplateDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TemplateDO::getMarketKey, marketKey)
                .eq(TemplateDO::getType, TemplateTypeEnum.DOWNLOAD_TEMPLATE.name())
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, 0);
        Long count = templateMapper.selectCount(wrapper);

        return count > 0;
    }


    /**
     * 模版名称重复校验, 重复抛出异常
     *
     * @param name 模版名称
     */
    public void duplicateNameVerification(String name) {
        LambdaQueryWrapper<TemplateDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TemplateDO::getName, name)
                .eq(TemplateDO::getDeleted, Boolean.FALSE)
                .eq(TemplateDO::getStatus, 0);
        TemplateDO templateDO = templateMapper.selectOne(wrapper);
        Assert.isNull(templateDO, "The name: " + name + " has been exist, please change the template name and try again.");
    }

    /**
     * 获取分页对象
     *
     * @param query query
     * @param <T>   T
     * @return Page
     */
    private static <T> Page<T> ofPage(PageQuery query) {
        return new Page<>(query.getPageNo(), query.getPageSize());
    }
}
