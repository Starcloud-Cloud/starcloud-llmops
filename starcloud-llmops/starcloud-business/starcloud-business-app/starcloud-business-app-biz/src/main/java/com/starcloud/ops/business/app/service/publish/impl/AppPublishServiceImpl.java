package com.starcloud.ops.business.app.service.publish.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.publish.AppPublishConverter;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.publish.AppPublishAuditEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用发布服务实现
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@Service
public class AppPublishServiceImpl implements AppPublishService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppService appService;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppPublishMapper appPublishMapper;

    /**
     * 分页查询应用发布记录
     *
     * @param query 请求参数
     * @return 应用发布响应
     */
    @Override
    public PageResp<AppPublishRespVO> page(AppPublishPageReqVO query) {
        Page<AppPublishDO> page = appPublishMapper.page(query);
        List<AppPublishRespVO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream()
                .map(AppPublishConverter.INSTANCE::convert).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 根据应用 UID 查询应用发布记录, 根据版本号倒序排序
     *
     * @param appUid 应用 UID
     * @return 应用发布响应
     */
    @Override
    public List<AppPublishRespVO> listByAppUid(String appUid) {
        List<AppPublishDO> list = appPublishMapper.listByAppUid(appUid);
        return CollectionUtil.emptyIfNull(list).stream().map(AppPublishConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    /**
     * 根据发布 UID 查询应用发布记录
     *
     * @param uid 发布 UID
     * @return 应用发布响应
     */
    @Override
    public AppPublishRespVO getByUid(String uid) {
        AppPublishDO appPublish = appPublishMapper.getByUid(uid, Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_RECORD_NO_EXISTS_UID, uid);
        return AppPublishConverter.INSTANCE.convert(appPublish);
    }

    /**
     * 创建一条应用发布记录
     *
     * @param request 请求参数
     * @return 应用发布响应
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppPublishRespVO create(AppPublishReqVO request) {
        // 查询并校验应用是否存在
        AppDO app = appMapper.get(request.getAppUid(), Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, request.getAppUid());
        // 组装应用发布记录数据
        AppPublishDO appPublish = AppPublishConverter.INSTANCE.convert(app);
        appPublish.setLanguage(request.getLanguage());
        // 查询该应用 UID 的发布记录
        List<AppPublishDO> appPublishRecords = appPublishMapper.listByAppUid(request.getAppUid());
        // 如果该应用 UID 有发布记录，说明不是第一次发布。
        if (CollectionUtil.isNotEmpty(appPublishRecords)) {
            // 获取最新发布记录
            AppPublishDO lastAppPublish = appPublishRecords.get(0);
            // 版本号递增
            appPublish.setVersion(AppUtils.nextVersion(lastAppPublish.getVersion()));
            appPublish.setMarketUid(lastAppPublish.getMarketUid());
            // 如果最新发布应用处于审核中，将最新发布状态改为已失效
            if (Objects.equals(AppPublishAuditEnum.PENDING.getCode(), lastAppPublish.getAudit())) {
                appPublishMapper.audit(lastAppPublish.getUid(), AppPublishAuditEnum.INVALID.getCode());
            }
        }
        // 保存应用发布记录
        appPublishMapper.insert(appPublish);
        return AppPublishConverter.INSTANCE.convert(appPublish);
    }

    /**
     * 管理员只能审核通过或者审核拒绝
     *
     * @param uid   发布 UID
     * @param audit 审核状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(String uid, Integer audit) {
        // 校验审核状态
        if (!Objects.equals(AppPublishAuditEnum.APPROVED.getCode(), audit) || !Objects.equals(AppPublishAuditEnum.REJECTED.getCode(), audit)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_AUDIT_NOT_SUPPORTED);
        }
        // 查询发布记录
        AppPublishDO appPublish = appPublishMapper.getByUid(uid, Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_RECORD_NO_EXISTS_UID, uid);

        // 如果审核通过
        if (Objects.equals(AppPublishAuditEnum.APPROVED.getCode(), audit)) {
            AppMarketDO appMarketDO = this.handlerMarketApp(appPublish);
            appPublish.setMarketUid(appMarketDO.getUid());
        }

        // 更新我的应用的发布 UID
        LambdaUpdateWrapper<AppDO> appUpdateWrapper = Wrappers.lambdaUpdate(AppDO.class);
        appUpdateWrapper.eq(AppDO::getUid, appPublish.getAppUid());
        appUpdateWrapper.set(AppDO::getPublishUid, AppUtils.generateUid(appPublish.getMarketUid(), appPublish.getVersion()));
        appMapper.update(null, appUpdateWrapper);

        // 更新发布记录
        LambdaUpdateWrapper<AppPublishDO> publishUpdateWrapper = Wrappers.lambdaUpdate(AppPublishDO.class);
        publishUpdateWrapper.eq(AppPublishDO::getUid, uid);
        publishUpdateWrapper.set(AppPublishDO::getAudit, audit);
        publishUpdateWrapper.set(AppPublishDO::getMarketUid, appPublish.getMarketUid());
        appPublishMapper.update(null, publishUpdateWrapper);
    }

    /**
     * 提供给用户的接口，用于取消发布到模版市场和重新发布到模版市场
     *
     * @param uid   发布 UID
     * @param audit 审核状态
     */
    @Override
    public void operate(String uid, Integer audit) {
        // 校验审核状态
        if (!Objects.equals(AppPublishAuditEnum.CANCELED.getCode(), audit) || !Objects.equals(AppPublishAuditEnum.PENDING.getCode(), audit)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_AUDIT_NOT_SUPPORTED);
        }
        appPublishMapper.audit(uid, audit);
    }

    /**
     * 新增或者更新应用市场记录
     *
     * @param appPublish 应用发布记录
     * @return 应用市场记录
     */
    private AppMarketDO handlerMarketApp(AppPublishDO appPublish) {
        AppMarketDO appMarketDO = AppMarketConvert.INSTANCE.convert(appPublish);
        appMarketDO.setImages(buildImages(appPublish.getCategories()));
        // marketUid 不为空，说明已经发布过，需要更新发布记录
        if (StringUtils.isNotBlank(appPublish.getMarketUid())) {
            AppMarketDO appMarket = appMarketMapper.get(appPublish.getMarketUid(), Boolean.TRUE);
            if (Objects.nonNull(appMarket)) {
                appMarketDO.setId(appMarket.getId());
                appMarketDO.setUid(appMarket.getUid());
                appMarketDO.setVersion(appPublish.getVersion());
                appMarketDO.setUsageCount(appMarket.getUsageCount());
                appMarketDO.setLikeCount(appMarket.getLikeCount());
                appMarketDO.setViewCount(appMarket.getViewCount());
                appMarketDO.setInstallCount(appMarket.getInstallCount());
                return appMarketMapper.modify(appMarketDO);
            }
        }
        // 如果应用市场不存在该应用，说明是第一次发布，需要新增应用市场记录
        return appMarketMapper.create(appMarketDO);
    }

    /**
     * 构建上传应用的图片
     *
     * @param categories 分类
     * @return 图片列表
     */
    private String buildImages(String categories) {
        if (StringUtils.isBlank(categories)) {
            return AppConstants.APP_MARKET_DEFAULT_IMAGE;
        }
        List<String> categoryCollect = AppUtils.split(categories);
        if (CollectionUtil.isEmpty(categoryCollect)) {
            return AppConstants.APP_MARKET_DEFAULT_IMAGE;
        }
        List<AppCategoryVO> categoryList = appService.categories();
        // 从 categoryList 中获取对应的图片
        List<String> images = CollectionUtil.emptyIfNull(categoryList).stream()
                .filter(category -> categoryCollect.contains(category.getCode()))
                .map(AppCategoryVO::getImage)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(images)) {
            return AppUtils.join(images);
        }

        return AppConstants.APP_MARKET_DEFAULT_IMAGE;
    }

}
