package com.starcloud.ops.business.app.service.publish.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.base.vo.request.UidStatusRequest;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishLatestRespVO;
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
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
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
import java.util.Optional;
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

    @Resource
    private AppPublishChannelService appPublishChannelService;

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
    public AppPublishRespVO get(String uid) {
        // 查询发布记录
        AppPublishDO appPublish = appPublishMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_RECORD_NO_EXISTS_UID, uid);
        return AppPublishConverter.INSTANCE.convert(appPublish);
    }

    /**
     * 根据应用 UID 查询最新的应用发布记录
     *
     * @param appUid 应用 UID
     * @return 应用发布响应
     */
    @Override
    public AppPublishLatestRespVO getLatest(String appUid) {

        // 查询应用信息
        AppDO app = appMapper.get(appUid, Boolean.TRUE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, appUid);

        // 基本信息复制
        List<AppPublishDO> publishList = appPublishMapper.listByAppUid(appUid);
        // 不存在发布记录, 说明第一次发布应用
        // 此时：审核状态为：未发布，审核标签为：未发布，需要更新，展示发布按钮，但是发布按钮不可用，需要提示。
        if (CollectionUtil.isEmpty(publishList)) {
            return AppPublishConverter.INSTANCE.convertDefaultUnpublishedLatest(appUid, app);
        }

        // 获取最新版本发布记录
        AppPublishLatestRespVO response = AppPublishConverter.INSTANCE.convertLatest(publishList.get(0));
        response.setAppLastUpdateTime(app.getUpdateTime());
        response.setIsFirstCreatePublishRecord(Boolean.FALSE);
        // 发布记录不为空且存在待审核的发布记录, 不显示发布按钮，显示 取消发布按钮
        if (publishList.stream().anyMatch(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.PENDING.getCode()))) {
            response.setAuditTag(AppPublishAuditEnum.PENDING.getCode());
            if (app.getUpdateTime().isAfter(response.getCreateTime())) {
                // 应用有更新，需要更新
                buildNeedUpdateResponse(Boolean.FALSE, response);
            } else {
                // 应用无更新，不需要更新
                buildUnNeedUpdateResponse(Boolean.FALSE, response);
            }
            return response;
        }

        // 如果最新一条记录为审核拒绝，则为审核拒绝状态
        if (Objects.equals(response.getAudit(), AppPublishAuditEnum.REJECTED.getCode())) {
            response.setAuditTag(AppPublishAuditEnum.REJECTED.getCode());
        } else {
            boolean approvedFlag = publishList.stream().anyMatch(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.APPROVED.getCode()));
            // 发布记录不为空，不存在待审核和审核通过的发布记录。
            response.setAuditTag(approvedFlag ? AppPublishAuditEnum.APPROVED.getCode() : response.getAudit());
        }
        if (app.getUpdateTime().isAfter(response.getCreateTime())) {
            buildNeedUpdateResponse(Boolean.TRUE, response);
        } else {
            // 应用无更新，不需要更新
            buildUnNeedUpdateResponse(Boolean.TRUE, response);
        }

        List<AppPublishChannelRespVO> channelList = appPublishChannelService.listByAppUid(appUid);
        response.setChannels(channelList);
        return response;
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
            // 如果最新发布应用处于审核通过状态，则将 marketUid 带到最新发布记录中
            Optional<AppPublishDO> approvedPublish = appPublishRecords.stream()
                    .filter(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.APPROVED.getCode()))
                    .filter(item -> StringUtils.isNotBlank(item.getMarketUid()))
                    .findFirst();
            approvedPublish.ifPresent(appPublishDO -> appPublish.setMarketUid(appPublishDO.getMarketUid()));

            // 如果最新发布应用处于审核中，将最新发布状态改为已取消, 基本上只会存在一条审核中的发布记录，但是为了防止意外，将所有的审核中的发布记录都取消
            List<AppPublishDO> pendingPublishList = appPublishRecords.stream()
                    .filter(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.PENDING.getCode())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(pendingPublishList)) {
                for (AppPublishDO appPublishDO : pendingPublishList) {
                    appPublishMapper.audit(appPublishDO.getUid(), AppPublishAuditEnum.CANCELED.getCode());
                }
            }
        }
        // 更新渠道表中的发布 UID
        appPublishChannelService.updatePublishUidByAppUid(request.getAppUid(), appPublish.getUid());
        // 保存应用发布记录
        appPublishMapper.insert(appPublish);
        return AppPublishConverter.INSTANCE.convert(appPublish);
    }

    /**
     * 管理员只能审核通过或者审核拒绝
     *
     * @param request 请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(UidStatusRequest request) {
        // 校验审核状态
        if (!Objects.equals(request.getStatus(), AppPublishAuditEnum.APPROVED.getCode()) &&
                !Objects.equals(request.getStatus(), AppPublishAuditEnum.REJECTED.getCode())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_AUDIT_NOT_SUPPORTED, request.getStatus());
        }
        // 查询应用，应用不存在，抛出异常
        AppDO app = appMapper.get(request.getAppUid(), Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, request.getAppUid());

        // 查询发布记录
        AppPublishDO appPublish = appPublishMapper.get(request.getUid(), Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_RECORD_NO_EXISTS_UID, request.getUid());

        // 如果审核通过
        if (Objects.equals(request.getStatus(), AppPublishAuditEnum.APPROVED.getCode())) {
            // 处理应用市场，存在则更新，不存在则创建
            AppMarketDO appMarketDO = this.handlerMarketApp(appPublish);
            appPublish.setMarketUid(appMarketDO.getUid());
            // 更新我的应用的发布 UID
            LambdaUpdateWrapper<AppDO> appUpdateWrapper = Wrappers.lambdaUpdate(AppDO.class);
            appUpdateWrapper.eq(AppDO::getUid, appPublish.getAppUid());
            appUpdateWrapper.set(AppDO::getPublishUid, AppUtils.generateUid(appPublish.getUid(), appPublish.getVersion()));
            appMapper.update(null, appUpdateWrapper);
        }

        // 更新发布记录
        LambdaUpdateWrapper<AppPublishDO> publishUpdateWrapper = Wrappers.lambdaUpdate(AppPublishDO.class);
        publishUpdateWrapper.eq(AppPublishDO::getUid, request.getUid());
        publishUpdateWrapper.set(AppPublishDO::getAudit, request.getStatus());
        publishUpdateWrapper.set(AppPublishDO::getMarketUid, appPublish.getMarketUid());
        appPublishMapper.update(null, publishUpdateWrapper);
    }

    /**
     * 提供给用户的接口，用于取消发布到模版市场和重新发布到模版市场
     *
     * @param request 应用 request
     */
    @Override
    public void operate(UidStatusRequest request) {

        // 校验审核状态
        if (!Objects.equals(request.getStatus(), AppPublishAuditEnum.CANCELED.getCode()) &&
                !Objects.equals(request.getStatus(), AppPublishAuditEnum.PENDING.getCode())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_AUDIT_NOT_SUPPORTED, request.getStatus());
        }

        // 查询发布记录，发布记录不存在，抛出异常
        AppPublishDO appPublishDO = appPublishMapper.get(request.getUid(), Boolean.TRUE);
        AppValidate.notNull(appPublishDO, ErrorCodeConstants.APP_PUBLISH_RECORD_NO_EXISTS_UID, request.getUid());

        // 查询应用，应用不存在，抛出异常
        AppDO app = appMapper.get(request.getAppUid(), Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, request.getAppUid());

        // 说明已经最少审核通过一次了，应用市场已经存在了。
        if (StringUtils.isNotBlank(appPublishDO.getMarketUid())) {
            AppMarketDO appMarketDO = appMarketMapper.get(appPublishDO.getMarketUid(), Boolean.TRUE);
            if (Objects.nonNull(appMarketDO) && !appMarketDO.getName().equals(app.getName())) {
                // 此时说明应用市场已经存在了，但是应用市场的名称和应用的名称不一致，说明应用名称有修改，需要校验应用市场的名称是否已经存在
                AppValidate.isFalse(appMarketMapper.duplicateName(app.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE);
            }
        } else {
            // 说明还没有审核通过，应用市场不存在，需要校验应用市场的名称是否已经存在
            AppValidate.isFalse(appMarketMapper.duplicateName(app.getName()), ErrorCodeConstants.APP_NAME_DUPLICATE);
        }

        // 如果是重新发布, 则校验是否存在待审核的发布记录, 如果存在, 则不允许重新发布
        if (Objects.equals(request.getStatus(), AppPublishAuditEnum.PENDING.getCode())) {
            List<AppPublishDO> publishList = appPublishMapper.listByAppUid(request.getAppUid());
            // 存在待审核的发布记录, 则不允许重新发布，理论上不会存在多条待审核的发布记录，但是为了防止意外，进行校验
            Optional<AppPublishDO> pendingPublishOptional = publishList.stream()
                    .filter(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.PENDING.getCode())).findAny();
            if (pendingPublishOptional.isPresent()) {
                AppPublishDO pendingPublish = pendingPublishOptional.get();
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_DUPLICATE, pendingPublish.getUid());
            }
        }

        appPublishMapper.audit(request.getUid(), request.getStatus());
    }

    /**
     * 删除应用发布记录
     *
     * @param appUid 应用 UID
     */
    @Override
    public void deleteByAppUid(String appUid) {
        List<AppPublishDO> publishList = appPublishMapper.listByAppUid(appUid);
        if (CollectionUtil.isEmpty(publishList)) {
            return;
        }
        List<Long> collect = publishList.stream().map(AppPublishDO::getId).collect(Collectors.toList());
        appPublishMapper.deleteBatchIds(collect);
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

    /**
     * 构建应用发布最新版本的响应，需要更新的情况
     *
     * @param showPublish 是否显示发布按钮
     * @param response    响应
     */
    private void buildNeedUpdateResponse(Boolean showPublish, AppPublishLatestRespVO response) {
        response.setNeedUpdate(Boolean.TRUE);
        response.setNeedTips(Boolean.TRUE);
        response.setShowPublish(showPublish);
        response.setEnablePublish(Boolean.FALSE);
    }

    /**
     * 构建应用发布最新版本的响应，不需要更新的情况
     *
     * @param showPublish 是否显示发布按钮
     * @param response    响应
     */
    private void buildUnNeedUpdateResponse(Boolean showPublish, AppPublishLatestRespVO response) {
        response.setNeedUpdate(Boolean.FALSE);
        response.setNeedTips(Boolean.FALSE);
        response.setShowPublish(showPublish);
        response.setEnablePublish(Boolean.TRUE);
    }

}
