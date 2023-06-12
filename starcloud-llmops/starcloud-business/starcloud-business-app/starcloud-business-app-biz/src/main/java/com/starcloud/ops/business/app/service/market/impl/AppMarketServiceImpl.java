package com.starcloud.ops.business.app.service.market.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.market.dto.AppMarketDTO;
import com.starcloud.ops.business.app.api.market.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.request.AppMarketRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUpdateRequest;
import com.starcloud.ops.business.app.api.operate.request.AppOperateRequest;
import com.starcloud.ops.business.app.convert.AppMarketConvert;
import com.starcloud.ops.business.app.convert.AppOperateConvert;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.operate.AppOperateMapper;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.enums.AppOperateTypeEnum;
import com.starcloud.ops.business.app.exception.AppException;
import com.starcloud.ops.business.app.exception.AppMarketException;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
public class AppMarketServiceImpl implements AppMarketService {

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppOperateMapper appOperateMapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 分页查询模版市场列表
     *
     * @param query 查询条件
     * @return 模版市场列表
     */
    @Override
    public PageResp<AppMarketDTO> page(AppMarketPageQuery query) {
        // 构建查询条件
        LambdaQueryWrapper<AppMarketDO> wrapper = buildPageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), AppMarketDO::getName, query.getName())
                .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());

        // 分页查询
        Page<AppMarketDO> page = appMarketMapper.selectPage(PageUtil.page(query), wrapper);
        List<AppMarketDTO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream().map(AppMarketConvert::convert).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    @Override
    public AppMarketDTO getById(Long id) {
        AppMarketDO templateMarketDO = appMarketMapper.selectById(id);
        Assert.notNull(templateMarketDO, "The Id: " + id + " template does not exist in template market.");
        return AppMarketConvert.convert(templateMarketDO);
    }

    /**
     * 根据模版 uid 获取模版详情
     *
     * @param uid 模版 uid
     * @return 模版详情
     */
    @Override
    public AppMarketDTO getByUid(String uid) {
        LambdaQueryWrapper<AppMarketDO> wrapper = buildBaseQueryWrapper().eq(AppMarketDO::getUid, uid);
        AppMarketDO templateMarketDO = appMarketMapper.selectOne(wrapper);
        Assert.notNull(templateMarketDO, "The Uid: " + uid + " template does not exist in template market.");
        // 查看详情时候，会增加模版的查看量
        Integer viewCount = templateMarketDO.getViewCount() + 1;
        appMarketMapper.update(null, Wrappers.lambdaUpdate(AppMarketDO.class)
                .set(AppMarketDO::getViewCount, viewCount)
                .eq(AppMarketDO::getId, templateMarketDO.getId())
        );

        templateMarketDO.setViewCount(viewCount);
        return AppMarketConvert.convert(templateMarketDO);
    }

    /**
     * 创建模版市场的模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    @Override
    public Boolean create(AppMarketRequest request) {
        try {
            AppMarketDO templateMarketDO = AppMarketConvert.convertCreate(request);
            templateMarketDO.setUid(IdUtil.simpleUUID());
            appMarketMapper.insert(templateMarketDO);
            return Boolean.TRUE;
        } catch (AppMarketException | AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_CREATE_FAILED, e.getMessage());
        }
    }

    /**
     * 更新模版市场的模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    @Override
    public Boolean modify(AppMarketUpdateRequest request) {
        try {
            AppMarketDO templateMarketDO = AppMarketConvert.convertModify(request);
            LambdaUpdateWrapper<AppMarketDO> wrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                    .eq(AppMarketDO::getUid, request.getUid())
                    .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                    .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
            appMarketMapper.update(templateMarketDO, wrapper);
            return Boolean.TRUE;
        } catch (AppMarketException | AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_MODIFY_FAILED, e.getMessage());
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
            AppMarketDO templateMarketDO = appMarketMapper.selectById(id);
            Assert.notNull(templateMarketDO, "The Id: " + id + " template does not exist in template market.");
            appMarketMapper.deleteById(id);
            return Boolean.TRUE;
        } catch (AppMarketException e) {
            throw e;
        } catch (Exception e) {
            throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 删除模版市场的模版
     *
     * @param uid 模版 uid
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteByUid(String uid) {
        try {
            LambdaQueryWrapper<AppMarketDO> wrapper = buildBaseQueryWrapper().eq(AppMarketDO::getUid, uid);
            AppMarketDO appMarketDO = appMarketMapper.selectOne(wrapper);
            // 您要删除的模版不存在
            Assert.notNull(appMarketDO, "The Uid: " + uid + " of you want to delete template does not exist in template market.");
            appMarketMapper.deleteById(appMarketDO.getId());
            return Boolean.TRUE;
        } catch (AppMarketException e) {
            throw e;
        } catch (Exception e) {
            throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 模版操作
     *
     * @param request 操作请求
     * @return 是否操作成功
     */
    @Override
    public Boolean operate(AppOperateRequest request) {
        try {
            AppMarketDTO appMarketDTO = this.getByUid(request.getTemplateUid());
            Assert.notNull(appMarketDTO, "The Uid: " + request.getTemplateUid() + " template does not exist in template market.");
            return transactionTemplate.execute(status -> {
                AppOperateDO operateDO = AppOperateConvert.convert(request);
                // 插入操作记录
                appOperateMapper.insert(operateDO);
                // 更新模版市场的操作的数量
                String operate = request.getOperate().toUpperCase();
                AppMarketDO appMarketDO = new AppMarketDO();
                LambdaUpdateWrapper<AppMarketDO> wrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                        .eq(AppMarketDO::getUid, request.getTemplateUid())
                        .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                        .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
                if (AppOperateTypeEnum.LIKE.name().equals(operate)) {
                    appMarketDO.setLikeCount(appMarketDTO.getLikeCount() + 1);
                } else if (AppOperateTypeEnum.DOWNLOAD.name().equals(operate)) {
                    appMarketDO.setDownloadCount(appMarketDTO.getDownloadCount() + 1);
                } else if (AppOperateTypeEnum.VIEW.name().equals(operate)) {
                    appMarketDO.setViewCount(appMarketDTO.getViewCount() + 1);
                } else {
                    throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_OPERATE_FAIL, "The operate: " + request.getOperate() + " is not supported.");
                }
                appMarketMapper.update(appMarketDO, wrapper);
                return Boolean.TRUE;
            });
        } catch (AppMarketException e) {
            throw e;
        } catch (Exception e) {
            throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_OPERATE_FAIL, e.getMessage());
        }
    }

    /**
     * 基础查询条件
     *
     * @return 基础查询条件
     */
    private static LambdaQueryWrapper<AppMarketDO> buildBaseQueryWrapper() {
        return Wrappers.lambdaQuery(AppMarketDO.class)
                .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
    }

    /**
     * 构建分页查询条件, 只查询部分字段
     *
     * @return 分页查询条件
     */
    private static LambdaQueryWrapper<AppMarketDO> buildPageQueryWrapper() {
        return Wrappers.lambdaQuery(AppMarketDO.class).select(
                        AppMarketDO::getUid,
                        AppMarketDO::getName,
                        AppMarketDO::getDescription,
                        AppMarketDO::getIcon,
                        AppMarketDO::getImages,
                        AppMarketDO::getCategories,
                        AppMarketDO::getTags,
                        AppMarketDO::getScenes,
                        AppMarketDO::getVersion,
                        AppMarketDO::getFree,
                        AppMarketDO::getCost,
                        AppMarketDO::getVersion,
                        AppMarketDO::getViewCount,
                        AppMarketDO::getLikeCount,
                        AppMarketDO::getCreator,
                        AppMarketDO::getCreateTime
                )
                .eq(AppMarketDO::getDeleted, Boolean.FALSE)
                .eq(AppMarketDO::getStatus, StateEnum.ENABLE.getCode());
    }
}
