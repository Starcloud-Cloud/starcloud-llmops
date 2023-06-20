package com.starcloud.ops.business.app.service.app.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.redis.app.RecommendedAppRedisDAO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.dto.SortQuery;
import com.starcloud.ops.framework.common.api.enums.SortType;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 应用管理服务实现类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Slf4j
@Service
public class AppServiceImpl implements AppService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private RecommendedAppRedisDAO recommendedAppRedisDAO;

    @Resource
    private DictDataService dictDataService;

    /**
     * 查询应用分类列表
     *
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categories() {
        // 查询应用分类字典数据
        DictDataExportReqVO request = new DictDataExportReqVO();
        request.setDictType(AppConstants.APP_CATEGORY_DICT_TYPE);
        request.setStatus(StateEnum.ENABLE.getCode());
        List<DictDataDO> dictDataList = dictDataService.getDictDataList(request);

        if (CollectionUtil.isEmpty(dictDataList)) {
            return Collections.emptyList();
        }
//
        return null;
    }

    /**
     * 查询推荐的应用列表
     *
     * @return 应用列表
     */
    @Override
    public List<AppRespVO> listRecommendedApps() {
        return Collections.emptyList();
    }

    /**
     * 分页查询应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    @Override
    public PageResp<AppRespVO> page(AppPageQuery query) {
        // 默认按照更新时间倒序
        query.setSorts(Collections.singletonList(SortQuery.of("update_time", SortType.DESC.name())));

        // 构建查询条件
        LambdaQueryWrapper<AppDO> wrapper = buildPageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), AppDO::getName, query.getName())
                .in(AppDO::getType, AppTypeEnum.MYSELF.name(), AppTypeEnum.DOWNLOAD.name());

        // 执行分页查询
        Page<AppDO> page = appMapper.selectPage(PageUtil.page(query), wrapper);
        List<AppRespVO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream()
                .map(AppConvert.INSTANCE::convertResp)
                .peek(item -> {
                    item.setWorkflowConfig(null);
                    item.setChatConfig(null);
                })
                .collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 根据应用 UID 获取应用详情
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    @Override
    public AppRespVO getByUid(String uid) {
        AppDO appDO = appMapper.selectOne(Wrappers.lambdaQuery(AppDO.class).eq(AppDO::getUid, uid));
        AppValidate.notNull(appDO, ErrorCodeConstants.APP_NO_EXISTS_UID, uid);
        return AppConvert.INSTANCE.convertResp(appDO);
    }

    /**
     * 创建应用
     *
     * @param request 应用信息
     */
    @Override
    public void create(AppReqVO request) {
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);
        appEntity.insert();
    }

    /**
     * 复制应用
     *
     * @param request 模版应用
     */
    @Override
    public void copy(AppReqVO request) {
        request.setName(request.getName() + " - Copy");
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);
        appEntity.insert();
    }

    /**
     * 更新应用
     *
     * @param request 更新请求信息
     */
    @Override
    public void modify(AppUpdateReqVO request) {
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);
        appEntity.setUid(request.getUid());
        appEntity.update();
    }

    /**
     * 根据应用 UID 删除应用
     *
     * @param uid 应用 UID
     */
    @Override
    public void deleteByUid(String uid) {
        AppEntity entity = new AppEntity();
        entity.setUid(uid);
        entity.deleteByUid();
    }

    /**
     * 发布应用到应用市场
     *
     * @param request 应用发布到应用市场请求对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publicAppToMarket(AppPublishReqVO request) {

        // 查询应用是否存在，不存在则抛出异常
        AppDO appDO = appMapper.selectOne(Wrappers.lambdaQuery(AppDO.class).eq(AppDO::getUid, request.getUid()));
        AppValidate.notNull(appDO, ErrorCodeConstants.APP_NO_EXISTS_UID, request.getUid());

        AppMarketDO marketDO = null;
        marketDO.setUid(IdUtil.fastSimpleUUID());
        if (StringUtils.isBlank(appDO.getUploadUid())) {
            // 说明没有发布过应用市场。
            marketDO.setVersion(AppConstants.DEFAULT_VERSION);
            marketDO.setLikeCount(0);
            marketDO.setViewCount(0);
            marketDO.setDownloadCount(0);
        } else {
            // 说明已经发布过应用市场。
            LambdaQueryWrapper<AppMarketDO> wrapper = Wrappers.lambdaQuery(AppMarketDO.class)
                    .eq(AppMarketDO::getUid, AppUtils.getUid(appDO.getUploadUid()))
                    .orderByDesc(AppMarketDO::getCreateTime);
            List<AppMarketDO> appMarketList = appMarketMapper.selectList(wrapper);
            if (CollectionUtil.isNotEmpty(appMarketList)) {
                AppMarketDO appMarketDO = appMarketList.get(0);
                marketDO.setUid(appMarketDO.getUid());
                // 版本号增加
                marketDO.setVersion(appMarketDO.getVersion() + 1);
                marketDO.setLikeCount(Optional.ofNullable(appMarketDO.getLikeCount()).orElse(0));
                marketDO.setViewCount(Optional.ofNullable(appMarketDO.getViewCount()).orElse(0));
                marketDO.setDownloadCount(Optional.ofNullable(appMarketDO.getDownloadCount()).orElse(0));
            } else {
                // 说明已经发布过应用市场，但是已经被删除了。或者是数据异常。覆盖发布。
                marketDO.setVersion(AppConstants.DEFAULT_VERSION);
                marketDO.setLikeCount(0);
                marketDO.setViewCount(0);
                marketDO.setDownloadCount(0);
            }
        }
//        // 保存到应用市场
//        appMarketMapper.insert(marketDO);
//        // 更新我的应用
//        AppDO publishApp = AppConvert.convertPublish(request);
//        publishApp.setUploadUid(AppUtils.generateUid(marketDO.getUid(), marketDO.getVersion()));
//        appMapper.update(publishApp, Wrappers.lambdaUpdate(AppDO.class)
//                .eq(AppDO::getUid, request.getUid())
//                .eq(AppDO::getStatus, StateEnum.ENABLE.getCode()));
    }

    /**
     * 批量发布应用到应用市场
     *
     * @param requestList 应用发布到应用市场请求对象列表
     */
    @Override
    public void batchPublicAppToMarket(List<AppPublishReqVO> requestList) {

    }

    /**
     * 校验应用是否已经下载过
     *
     * @param marketUid 应用市场 UID。
     * @return 是否已经下载, true: 已经下载, false: 未下载
     */
    @Override
    public Boolean verifyHasDownloaded(String marketUid) {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class)
                .eq(AppDO::getDownloadUid, marketUid)
                .eq(AppDO::getType, AppTypeEnum.DOWNLOAD.name());
        Long count = appMapper.selectCount(wrapper);
        return count > 0;
    }

    /**
     * 构建分页查询条件, 仅查询指定字段
     *
     * @return 查询条件
     */
    public static LambdaQueryWrapper<AppDO> buildPageQueryWrapper() {
        return Wrappers.lambdaQuery(AppDO.class).select(
                AppDO::getUid,
                AppDO::getName,
                AppDO::getType,
                AppDO::getModel,
                AppDO::getSource,
                AppDO::getTags,
                AppDO::getCategories,
                AppDO::getScenes,
                AppDO::getIcon,
                AppDO::getConfig,
                AppDO::getDescription,
                AppDO::getUploadUid,
                AppDO::getDownloadUid,
                AppDO::getCreator,
                AppDO::getUpdater,
                AppDO::getCreateTime,
                AppDO::getUpdateTime
        );
    }
}
