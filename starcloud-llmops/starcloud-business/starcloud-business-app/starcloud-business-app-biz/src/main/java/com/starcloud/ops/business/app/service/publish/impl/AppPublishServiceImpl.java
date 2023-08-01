package com.starcloud.ops.business.app.service.publish.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.base.vo.request.UidStatusRequest;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishAuditRespVO;
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
        // 查询应用
        AppDO appDO = appMapper.get(appUid, Boolean.TRUE);
        // 应用不存在，此时说明，应用还没有创建过，未持久化应用
        if (Objects.isNull(appDO)) {
            AppPublishLatestRespVO response = new AppPublishLatestRespVO();
            response.setAppUid(appUid);
            response.setAudit(AppPublishAuditEnum.UN_PUBLISH.getCode());
            response.setIsFirstCreatePublishRecord(Boolean.TRUE);
            // 此时需要提示用户更新应用发布记录
            response.setNeedUpdate(Boolean.FALSE);
            return response;
        }
        // 获取最新版本发布记录
        AppPublishDO latest = appPublishMapper.getLatest(appUid);
        // 未查询到发布记录, 说明还没有发布过应用。则返回应用的基本信息, 构造一个未发布的发布记录，临时判断时候使用，未持久化
        if (Objects.isNull(latest)) {
            AppPublishLatestRespVO response = new AppPublishLatestRespVO();
            response.setAppUid(appUid);
            response.setName(appDO.getName());
            response.setAudit(AppPublishAuditEnum.UN_PUBLISH.getCode());
            response.setAppLastUpdateTime(appDO.getUpdateTime());
            response.setIsFirstCreatePublishRecord(Boolean.TRUE);
            // 此时需要提示用户更新应用发布记录
            response.setNeedUpdate(Boolean.TRUE);
            return response;
        }
        // 应用已经发布过，返回最新版本发布记录，根据应用更新时间判断是否需要更新发布记录
        AppPublishLatestRespVO response = AppPublishConverter.INSTANCE.convertLatest(latest);
        response.setIsFirstCreatePublishRecord(Boolean.FALSE);
        response.setAppLastUpdateTime(appDO.getUpdateTime());
        // 如果应用更新时间在发布时间之后, 则需要新增发布记录
        if (appDO.getUpdateTime().isAfter(latest.getCreateTime())) {
            response.setNeedUpdate(Boolean.TRUE);
        } else {
            response.setNeedUpdate(Boolean.FALSE);
        }

        return response;
    }

    /**
     * 根据应用 UID 查询应用发布记录
     *
     * @param appUid 应用 UID
     * @return 应用发布响应
     */
    @Override
    public AppPublishAuditRespVO getAuditByAppUid(String appUid) {
        AppPublishAuditRespVO response = new AppPublishAuditRespVO();
        List<AppPublishDO> publishList = appPublishMapper.listByAppUid(appUid);
        // 不存在发布记录, 则返回 null， 说明应用未发布过
        if (CollectionUtil.isEmpty(publishList)) {
            response.setAppUid(appUid);
            response.setAudit(AppPublishAuditEnum.UN_PUBLISH.getCode());
            return response;
        }

        // 存在待审核的发布记录, 则返回该记录
        Optional<AppPublishDO> pendingPublishOptional = publishList.stream()
                .filter(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.PENDING.getCode())).findFirst();
        if (pendingPublishOptional.isPresent()) {
            AppPublishDO pendingPublish = pendingPublishOptional.get();
            response.setAudit(AppPublishAuditEnum.PENDING.getCode());
            response.setUid(pendingPublish.getUid());
            response.setAppUid(appUid);
            response.setUpdateTime(pendingPublish.getUpdateTime());
            return response;
        }

        // 存在已发布的记录, 则返回该记录
        Optional<AppPublishDO> approvedPublishOptional = publishList.stream()
                .filter(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.APPROVED.getCode())).findFirst();
        if (approvedPublishOptional.isPresent()) {
            AppPublishDO approvedPublish = approvedPublishOptional.get();
            response.setAudit(AppPublishAuditEnum.APPROVED.getCode());
            response.setUid(approvedPublish.getUid());
            response.setAppUid(appUid);
            response.setUpdateTime(approvedPublish.getUpdateTime());
            return response;
        }

        // 否则直接返回最新一条条发布记录的状态, 并且把 audit 变为 已经取消
        AppPublishDO first = publishList.get(0);
        // 如果是未发布状态, 则直接返回未发布
        if (Objects.equals(first.getAudit(), AppPublishAuditEnum.UN_PUBLISH.getCode())) {
            response.setAudit(AppPublishAuditEnum.UN_PUBLISH.getCode());
        } else if (Objects.equals(first.getAudit(), AppPublishAuditEnum.REJECTED.getCode())) {
            response.setAudit(AppPublishAuditEnum.REJECTED.getCode());
        } else {
            response.setAudit(AppPublishAuditEnum.CANCELED.getCode());
        }
        response.setUid(first.getUid());
        response.setAppUid(appUid);
        response.setUpdateTime(first.getUpdateTime());
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
            if (StringUtils.isNotBlank(lastAppPublish.getMarketUid())) {
                appPublish.setMarketUid(lastAppPublish.getMarketUid());
            }
            // 如果最新发布应用处于审核中，将最新发布状态改为已取消, 基本上只会存在一条审核中的发布记录，但是为了防止意外，将所有的审核中的发布记录都取消
            List<AppPublishDO> pendingPublishList = appPublishRecords.stream()
                    .filter(item -> Objects.equals(item.getAudit(), AppPublishAuditEnum.PENDING.getCode())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(pendingPublishList)) {
                for (AppPublishDO appPublishDO : pendingPublishList) {
                    appPublishMapper.audit(appPublishDO.getUid(), AppPublishAuditEnum.CANCELED.getCode());
                }
            }
        }
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
        // 查询应用
        AppDO app = appMapper.get(request.getUid(), Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, request.getUid());

        // 查询发布记录
        AppPublishDO appPublish = appPublishMapper.get(request.getUid(), Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_RECORD_NO_EXISTS_UID, request.getUid());

        // 如果审核通过
        if (Objects.equals(request.getStatus(), AppPublishAuditEnum.APPROVED.getCode())) {
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

        // 如果是重新发布, 则校验是否存在待审核的发布记录, 如果存在, 则不允许重新发布
        if (Objects.equals(request.getStatus(), AppPublishAuditEnum.PENDING.getCode())) {
            List<AppPublishDO> publishList = appPublishMapper.listByAppUid(request.getAppUid());
            // 存在待审核的发布记录, 则不允许重新发布
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
