package com.starcloud.ops.business.app.service.publish.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.base.vo.request.UidStatusRequest;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishLatestRespVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.publish.AppPublishConverter;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.publish.AppPublishAuditEnum;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.chat.ChatExpandConfigService;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppPublishMapper appPublishMapper;

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private AppPublishLimitService appPublishLimitService;

    @Resource
    private ChatExpandConfigService chatExpandConfigService;

    @Resource
    private CreativeMaterialManager creativeMaterialManager;

    @Resource
    private CreativeContentService creativeContentService;

    /**
     * 分页查询应用发布记录
     *
     * @param query 请求参数
     * @return 应用发布响应
     */
    @Override
    public PageResp<AppPublishRespVO> page(AppPublishPageReqVO query) {
        Page<AppPublishDO> page = appPublishMapper.page(query);
        return AppPublishConverter.INSTANCE.convert(page);
    }

    /**
     * 分页查询应用发布记录
     *
     * @param query 请求参数
     * @return 应用发布响应
     */
    @Override
    public PageResp<AppPublishRespVO> pageAdmin(AppPublishPageReqVO query) {
        Page<AppPublishDO> page = appPublishMapper.page(query);
        return AppPublishConverter.INSTANCE.convert(page);
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
        AppValidate.notNull(appPublish, ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT, uid);
        return AppPublishConverter.INSTANCE.convert(appPublish);
    }


    @Override
    public AppPublishRespVO getMarket(String marketUid) {
        AppPublishDO market = appPublishMapper.getMarket(marketUid, true);
        return AppPublishConverter.INSTANCE.convert(market);
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
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, appUid);

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

        // 获取应用发布渠道记录，按照发布渠道类型分组
        Map<Integer, List<AppPublishChannelRespVO>> channelMap = appPublishChannelService.mapByAppPublishUidGroupByType(response.getUid());
        response.setChannelMap(channelMap);

        // 发布记录不为空且存在待审核的发布记录, 不显示发布按钮，显示 取消发布按钮
        boolean pendingFlag = publishList.stream().anyMatch(item ->
                Objects.equals(item.getAudit(), AppPublishAuditEnum.PENDING.getCode()));
        if (pendingFlag) {
            response.setAuditTag(AppPublishAuditEnum.PENDING.getCode());
            if (app.getUpdateTime().isAfter(response.getCreateTime())) {
                // 应用有更新，需要更新
                buildNeedUpdateResponse(Boolean.FALSE, Boolean.FALSE, response);
            } else {
                // 应用无更新，不需要更新
                buildUnNeedUpdateResponse(Boolean.FALSE, Boolean.FALSE, response);
            }
            return response;
        }

        // 如果最新的一条记录为审核通过，则为审核通过状态。而且不是不允许发布，必须更新之后才能发布
        if (Objects.equals(response.getAudit(), AppPublishAuditEnum.APPROVED.getCode())) {
            response.setAuditTag(AppPublishAuditEnum.APPROVED.getCode());
            if (app.getUpdateTime().isAfter(response.getCreateTime())) {
                // 应用有更新，需要更新, 更新之后才可以发布
                buildNeedUpdateResponse(Boolean.TRUE, Boolean.FALSE, response);
            } else {
                // 应用无更新，不需要更新，不可以发布
                buildUnNeedUpdateResponse(Boolean.TRUE, Boolean.FALSE, response);
            }
            return response;
        }

        // 如果最新一条记录为审核拒绝，则为审核拒绝状态。更新之后才可以发布
        if (Objects.equals(response.getAudit(), AppPublishAuditEnum.REJECTED.getCode())) {
            response.setAuditTag(AppPublishAuditEnum.REJECTED.getCode());
            if (app.getUpdateTime().isAfter(response.getCreateTime())) {
                // 应用有更新，需要更新, 更新之后才可以发布
                buildNeedUpdateResponse(Boolean.TRUE, Boolean.FALSE, response);
            } else {
                // 应用无更新，不需要更新。可以发布
                buildUnNeedUpdateResponse(Boolean.TRUE, Boolean.TRUE, response);
            }
            return response;
        }

        // 如果历史记录中存在审核通过的记录，且最新的一条记录不是审核通过。如果需要更新，则需要更新之后才可以发布
        boolean approvedFlag = publishList.stream()
                .anyMatch(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.APPROVED.getCode()));
        if (approvedFlag && !Objects.equals(response.getAudit(), AppPublishAuditEnum.APPROVED.getCode())) {
            response.setAuditTag(AppPublishAuditEnum.APPROVED.getCode());
            if (app.getUpdateTime().isAfter(response.getCreateTime())) {
                // 应用有更新，需要更新，更新之后才可以发布
                buildNeedUpdateResponse(Boolean.TRUE, Boolean.FALSE, response);
            } else {
                // 应用无更新，不需要更新，不可以发布
                buildUnNeedUpdateResponse(Boolean.TRUE, Boolean.TRUE, response);
            }
            return response;
        }

        response.setAuditTag(response.getAudit());
        if (app.getUpdateTime().isAfter(response.getCreateTime())) {
            // 应用有更新，需要更新, 更新之后才可以发布
            buildNeedUpdateResponse(Boolean.TRUE, Boolean.FALSE, response);
        } else {
            // 应用无更新，不需要更新。可以发布
            buildUnNeedUpdateResponse(Boolean.TRUE, Boolean.TRUE, response);
        }
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
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, request.getAppUid());
        // 组装应用发布记录数据
        AppPublishDO appPublish = AppPublishConverter.INSTANCE.convert(app);

        String uid = appPublish.getUid();
        if (AppModelEnum.CHAT.name().equals(app.getModel())) {
            BaseAppEntity baseApp = AppConvert.INSTANCE.convert(app, true);
            baseApp.getChatConfig().setAppConfigId(uid);
            app.setConfig(JSONUtil.toJsonStr(baseApp.getChatConfig()));
            appPublish.setAppInfo(JSONUtil.toJsonStr(app));
            // 插入 chat配置
            chatExpandConfigService.copyConfig(app.getUid(), uid);
        } else if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
            appPublish.setAppInfo(JsonUtils.toJsonString(app));
        }

        appPublish.setUserId(SecurityFrameworkUtils.getLoginUserId());
        // 查询该应用 UID 的发布记录
        List<AppPublishDO> appPublishRecords = appPublishMapper.listByAppUid(request.getAppUid());
        // 如果该应用 UID 有发布记录，说明不是第一次发布。
        if (CollectionUtil.isNotEmpty(appPublishRecords)) {
            // 获取最新发布记录
            AppPublishDO lastAppPublish = appPublishRecords.get(0);
            // 版本号递增
            appPublish.setVersion(AppUtils.nextVersion(lastAppPublish.getVersion()));
            // 如果历史中有已经审核通过的记录，则将历史记录中的 marketUid 赋值给当前发布记录
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
                    appPublishMapper.audit(appPublishDO.getUid(), AppPublishAuditEnum.CANCELED.getCode(), null);
                }
            }
        }
        // 更新渠道表中的发布 UID
        appPublishChannelService.updatePublishUidByAppUid(request.getAppUid(), appPublish.getUid());
        // 更新限流表中的发布 UID
        appPublishLimitService.updatePublishUidByAppUid(request.getAppUid(), appPublish.getUid());

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
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PUBLISH_AUDIT_NOT_SUPPORTED, request.getStatus());
        }
        // 查询应用，应用不存在，抛出异常
        AppDO app = appMapper.get(request.getAppUid(), Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, request.getAppUid());

        // 查询发布记录
        AppPublishDO appPublish = appPublishMapper.get(request.getUid(), Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT, request.getUid());

        // 如果审核通过
        if (Objects.equals(request.getStatus(), AppPublishAuditEnum.APPROVED.getCode())) {
            // 处理应用市场，存在则更新，不存在则创建
            AppMarketEntity appMarketEntity = this.handlerMarketApp(appPublish);
            appPublish.setMarketUid(appMarketEntity.getUid());
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
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PUBLISH_AUDIT_NOT_SUPPORTED, request.getStatus());
        }

        // 查询发布记录，发布记录不存在，抛出异常
        AppPublishDO appPublishDO = appPublishMapper.get(request.getUid(), Boolean.TRUE);
        AppValidate.notNull(appPublishDO, ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT, request.getUid());

        // 查询应用，应用不存在，抛出异常
        AppDO app = appMapper.get(request.getAppUid(), Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, request.getAppUid());

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
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.PUBLISH_APP_REPEAT, pendingPublish.getUid());
            }
        }

        appPublishMapper.audit(request.getUid(), request.getStatus(), SecurityFrameworkUtils.getLoginUserId());
    }

    /**
     * 根据 UID 删除应用发布记录
     *
     * @param uid 应用发布记录 UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        AppPublishDO appPublish = appPublishMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT, uid);
        appPublishMapper.deleteById(appPublish.getId());
        // 删除应用发布渠道记录
        appPublishChannelService.deleteByAppPublishUid(uid);
        // 删除应用发布限流记录
        appPublishLimitService.deleteByPublishUid(uid);
    }

    /**
     * 删除应用发布记录
     *
     * @param appUid 应用 UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAppUid(String appUid) {
        List<AppPublishDO> publishList = appPublishMapper.listByAppUid(appUid);
        if (CollectionUtil.isEmpty(publishList)) {
            return;
        }
        List<Long> collect = publishList.stream().map(AppPublishDO::getId).collect(Collectors.toList());
        appPublishMapper.deleteBatchIds(collect);
        // 删除应用发布渠道记录
        appPublishChannelService.deleteByAppUid(appUid);
        // 删除应用发布限流记录
        appPublishLimitService.deleteByAppUid(appUid);
    }

    /**
     * 新增或者更新应用市场记录
     *
     * @param appPublish 应用发布记录
     * @return 应用市场记录
     */
    private AppMarketEntity handlerMarketApp(AppPublishDO appPublish) {
        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(appPublish);
        // 查询配置的示例图片列表
        List<String> imageList = creativeContentService.listImage(appMarketEntity.getExample());
        if (CollectionUtil.isNotEmpty(imageList)) {
            appMarketEntity.setImages(imageList);
        }

        AppDO app = JsonUtils.parseObject(appPublish.getAppInfo(), AppDO.class);

        String marketUid = IdUtil.fastSimpleUUID();
        if (AppModelEnum.CHAT.name().equals(appMarketEntity.getModel())) {
            if (StringUtils.isNotBlank(appPublish.getMarketUid())) {
                marketUid = appPublish.getMarketUid();
            }
            appMarketEntity.setUid(marketUid);
            appMarketEntity.getChatConfig().setAppConfigId(marketUid);
            chatExpandConfigService.copyConfig(appPublish.getUid(), marketUid);
        }

        // marketUid 不为空，说明已经发布过，需要更新发布记录
        if (StringUtils.isNotBlank(appPublish.getMarketUid())) {
            AppMarketDO appMarket = appMarketMapper.get(appPublish.getMarketUid(), Boolean.TRUE);
            if (Objects.nonNull(appMarket)) {
                appMarketEntity.setUid(appMarket.getUid());
                appMarketEntity.setVersion(appPublish.getVersion());
                appMarketEntity.setUsageCount(appMarket.getUsageCount());
                appMarketEntity.setLikeCount(appMarket.getLikeCount());
                appMarketEntity.setViewCount(appMarket.getViewCount());
                appMarketEntity.setInstallCount(appMarket.getInstallCount());
                appMarketEntity.update();
                if (AppTypeEnum.MEDIA_MATRIX.name().equalsIgnoreCase(appMarketEntity.getType())) {
                    creativeMaterialManager.upgradeMaterialLibrary(app.getUid(), appMarketEntity);
                }
                return appMarketEntity;
            }
        }
        appMarketEntity.setUid(marketUid);
        if (AppTypeEnum.MEDIA_MATRIX.name().equalsIgnoreCase(appMarketEntity.getType())) {
            creativeMaterialManager.upgradeMaterialLibrary(app.getUid(), appMarketEntity);
        }
        // 如果应用市场不存在该应用，说明是第一次发布/或者已经删除，需要新增应用市场记录
        appMarketEntity.insert();
        return appMarketEntity;
    }

    /**
     * 构建应用发布最新版本的响应，需要更新的情况
     *
     * @param showPublish 是否显示发布按钮
     * @param response    响应
     */
    @SuppressWarnings("all")
    private void buildNeedUpdateResponse(Boolean showPublish, Boolean enablePublish, AppPublishLatestRespVO response) {
        response.setNeedUpdate(Boolean.TRUE);
        response.setNeedTips(Boolean.TRUE);
        response.setShowPublish(showPublish);
        response.setEnablePublish(enablePublish);
    }

    /**
     * 构建应用发布最新版本的响应，不需要更新的情况
     *
     * @param showPublish 是否显示发布按钮
     * @param response    响应
     */
    private void buildUnNeedUpdateResponse(Boolean showPublish, Boolean enablePublish, AppPublishLatestRespVO response) {
        response.setNeedUpdate(Boolean.FALSE);
        response.setNeedTips(Boolean.FALSE);
        response.setShowPublish(showPublish);
        response.setEnablePublish(enablePublish);
    }

}
