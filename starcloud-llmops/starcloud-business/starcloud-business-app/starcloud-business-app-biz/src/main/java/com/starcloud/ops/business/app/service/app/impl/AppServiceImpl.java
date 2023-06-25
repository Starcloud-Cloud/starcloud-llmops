package com.starcloud.ops.business.app.service.app.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
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
import com.starcloud.ops.business.app.convert.category.CategoryConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.domain.recommend.RecommendedAppFactory;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.app.LanguageEnum;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.dto.SortQuery;
import com.starcloud.ops.framework.common.api.enums.SortType;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
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
    private AppRepository appRepository;

    @Resource
    private AppMarketMapper appMarketMapper;

//    @Resource
//    private RecommendedAppRedisDAO recommendedAppRedisDAO;

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

        // 未查询到数据，返回空列表
        if (CollectionUtil.isEmpty(dictDataList)) {
            return Collections.emptyList();
        }
        // 转换为应用分类列表
        return dictDataList.stream().map(CategoryConvert.INSTANCE::convert)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 查询应用语言列表
     *
     * @return 应用语言列表
     */
    @Override
    public List<Option> languages() {
        LanguageEnum[] values = LanguageEnum.values();
        return Arrays.stream(values).map(language -> {
            Option option = new Option();
            option.setValue(language.getCode());
            Locale locale = LocaleContextHolder.getLocale();
            if (Locale.CHINA.equals(locale)) {
                option.setLabel(language.getLabel());
            } else {
                option.setLabel(language.getLabelEn());
            }
            return option;
        }).collect(Collectors.toList());
    }

    /**
     * 查询推荐的应用列表
     *
     * @return 应用列表
     */
    @Override
    public List<AppRespVO> listRecommendedApps() {
//        return Collections.emptyList();
        return Collections.singletonList(RecommendedAppFactory.defGenerateTextApp());
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
        appRepository.deleteByUid(uid);
    }

    /**
     * 发布应用到应用市场
     *
     * @param request 应用发布到应用市场请求对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(AppPublishReqVO request) {
        // 查询我的应用是否存在，不存在则抛出异常
        AppDO appDO = appMapper.selectOne(Wrappers.lambdaQuery(AppDO.class).eq(AppDO::getUid, request.getUid()));
        AppValidate.notNull(appDO, ErrorCodeConstants.APP_NO_EXISTS_UID, request.getUid());

        AppMarketEntity appMarketEntity = AppMarketConvert.INSTANCE.convert(appDO, request);
        // 判断是否已经发布过应用市场：判断依据：uploadUid 是否为空
        if (StringUtils.isNotBlank(appDO.getUploadUid())) {
            // 此时说明该应用已经发布过应用市场，需要先将之前的应用市场记录置为不可用。
            // 查询之前发布的应用市场记录的所有版本，按照版本号倒序排序。
            LambdaQueryWrapper<AppMarketDO> marketQueryWrapper = Wrappers.lambdaQuery(AppMarketDO.class)
                    .select(AppMarketDO::getId, AppMarketDO::getUid, AppMarketDO::getVersion,
                            AppMarketDO::getLikeCount, AppMarketDO::getVersion, AppMarketDO::getDownloadCount)
                    .eq(AppMarketDO::getUid, AppUtils.obtainUid(appDO.getUploadUid()))
                    .orderByDesc(AppMarketDO::getVersion);
            List<AppMarketDO> appMarketList = appMarketMapper.selectList(marketQueryWrapper);

            // 如果查询结果不为空，则获取最新的一条记录，进行处理: UID 不变，版本号增加, 三数取最新一条记录的数据。
            if (CollectionUtil.isNotEmpty(appMarketList)) {
                AppMarketDO appMarketDO = appMarketList.get(0);
                // 新增的数据，UID 不变，版本号增加
                appMarketEntity.setUid(appMarketDO.getUid());
                // 版本号增加
                appMarketEntity.setVersion(AppUtils.nextVersion(appMarketDO.getVersion()));
                // 三数取最新一条记录的数据
                appMarketEntity.setLikeCount(Optional.ofNullable(appMarketDO.getLikeCount()).orElse(0));
                appMarketEntity.setViewCount(Optional.ofNullable(appMarketDO.getViewCount()).orElse(0));
                appMarketEntity.setDownloadCount(Optional.ofNullable(appMarketDO.getDownloadCount()).orElse(0));
            }
        }
        // 校验数据
        appMarketEntity.validate();
        AppMarketDO appMarketDO = AppMarketConvert.INSTANCE.convert(appMarketEntity);
        appMarketDO.setDeleted(Boolean.FALSE);
        // 统一设置为待审核状态
        appMarketDO.setAudit(AppMarketAuditEnum.PENDING.getCode());
        // 保存到应用市场
        appMarketMapper.insert(appMarketDO);

        // 更新我的应用
        appDO.setUploadUid(AppUtils.generateUid(appMarketDO.getUid(), appMarketDO.getVersion()));
        appDO.setLastUpload(LocalDateTime.now());
        appMapper.update(appDO, Wrappers.lambdaUpdate(AppDO.class).eq(AppDO::getUid, request.getUid()));
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
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            return Boolean.FALSE;
        }
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class)
                .eq(AppDO::getDownloadUid, marketUid)
                .eq(AppDO::getType, AppTypeEnum.DOWNLOAD.name())
                .eq(AppDO::getCreator, SecurityFrameworkUtils.getLoginUserId());
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
