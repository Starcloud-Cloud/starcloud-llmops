package com.starcloud.ops.business.app.service.publish.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.convert.publish.AppPublishConverter;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
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
        AppDO app = appMapper.getByUid(request.getAppUid(), Boolean.FALSE);
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
            appPublish.setMarketVersion(lastAppPublish.getMarketVersion());
            // 如果最新发布应用处于审核中，将最新发布状态改为已经取消
            if (Objects.equals(AppMarketAuditEnum.PENDING.getCode(), lastAppPublish.getAudit())) {
                appPublishMapper.audit(lastAppPublish.getUid(), AppMarketAuditEnum.CANCELED.getCode());
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
        if (!Objects.equals(AppMarketAuditEnum.APPROVED.getCode(), audit) || !Objects.equals(AppMarketAuditEnum.REJECTED.getCode(), audit)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_PUBLISH_AUDIT_NOT_SUPPORTED);
        }
        // 查询发布记录
        AppPublishDO appPublish = appPublishMapper.getByUid(uid, Boolean.FALSE);
        AppValidate.notNull(appPublish, ErrorCodeConstants.APP_PUBLISH_RECORD_NO_EXISTS_UID, uid);

        // 如果审核通过
        if (Objects.equals(AppMarketAuditEnum.APPROVED.getCode(), audit)) {


        }
        // 更新发布记录
        appPublishMapper.audit(uid, audit);
    }


}
