package com.starcloud.ops.business.app.service.market.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.market.dto.TemplateMarketDTO;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketPageQuery;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketRequest;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketUpdateRequest;
import com.starcloud.ops.business.app.convert.TemplateMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.market.TemplateMarketDO;
import com.starcloud.ops.business.app.dal.mysql.market.TemplateMarketMapper;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.exception.TemplateMarketException;
import com.starcloud.ops.business.app.service.market.TemplateMarketService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模版市场服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Slf4j
@Service
public class TemplateMarketServiceImpl implements TemplateMarketService {

    @Resource
    private TemplateMarketMapper templateMarketMapper;

    /**
     * 分页查询模版市场列表
     *
     * @param query 查询条件
     * @return 模版市场列表
     */
    @Override
    public PageResp<TemplateMarketDTO> page(TemplateMarketPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<TemplateMarketDO> wrapper = buildPageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), TemplateMarketDO::getName, query.getName())
                .eq(TemplateMarketDO::getDeleted, Boolean.FALSE)
                .eq(TemplateMarketDO::getStatus, StateEnum.ENABLE.getCode());

        // 分页查询
        Page<TemplateMarketDO> page = templateMarketMapper.selectPage(PageUtil.page(query), wrapper);
        List<TemplateMarketDTO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream().map(TemplateMarketConvert::convert).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    @Override
    public TemplateMarketDTO getById(Long id) {
        TemplateMarketDO templateMarketDO = templateMarketMapper.selectById(id);
        Assert.notNull(templateMarketDO, "The Id: " + id + " template does not exist in template market.");
        return TemplateMarketConvert.convert(templateMarketDO);
    }

    /**
     * 创建模版市场的模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    @Override
    public Boolean create(TemplateMarketRequest request) {
        try {
            TemplateMarketDO templateMarketDO = TemplateMarketConvert.convertCreate(request);
            templateMarketMapper.insert(templateMarketDO);
            return Boolean.TRUE;
        } catch (Exception e) {
            throw TemplateMarketException.exception(AppResultCode.TEMPLATE_MARKET_CREATE_FAILED, e.getMessage());
        }
    }

    /**
     * 更新模版市场的模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    @Override
    public Boolean modify(TemplateMarketUpdateRequest request) {
        try {
            TemplateMarketDO templateMarketDO = TemplateMarketConvert.convertModify(request);
            templateMarketMapper.updateById(templateMarketDO);
            return Boolean.TRUE;
        } catch (Exception e) {
            throw TemplateMarketException.exception(AppResultCode.TEMPLATE_MARKET_MODIFY_FAILED, e.getMessage());
        }
    }

    /**
     * 删除模版市场的模版
     *
     * @param id 模版 ID
     * @return 是否删除成功
     */
    @Override
    public Boolean delete(Long id) {
        try {
            TemplateMarketDO templateMarketDO = templateMarketMapper.selectById(id);
            Assert.notNull(templateMarketDO, "The Id: " + id + " template does not exist in template market.");
            templateMarketMapper.deleteById(id);
            return Boolean.TRUE;
        } catch (Exception e) {
            throw TemplateMarketException.exception(AppResultCode.TEMPLATE_MARKET_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 构建分页查询条件, 只查询部分字段
     *
     * @return 分页查询条件
     */
    private static LambdaQueryWrapper<TemplateMarketDO> buildPageQueryWrapper() {
        return Wrappers.lambdaQuery(TemplateMarketDO.class).select(
                TemplateMarketDO::getId,
                TemplateMarketDO::getKey,
                TemplateMarketDO::getName,
                TemplateMarketDO::getDescription,
                TemplateMarketDO::getIcon,
                TemplateMarketDO::getImages,
                TemplateMarketDO::getCategories,
                TemplateMarketDO::getTags,
                TemplateMarketDO::getScenes,
                TemplateMarketDO::getVersion,
                TemplateMarketDO::getFree,
                TemplateMarketDO::getCost,
                TemplateMarketDO::getVersion,
                TemplateMarketDO::getViewCount,
                TemplateMarketDO::getLikeCount,
                TemplateMarketDO::getCreator,
                TemplateMarketDO::getCreateTime
        );
    }
}
