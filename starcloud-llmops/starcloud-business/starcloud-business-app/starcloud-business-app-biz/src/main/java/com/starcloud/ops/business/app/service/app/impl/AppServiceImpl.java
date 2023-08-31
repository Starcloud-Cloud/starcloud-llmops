package com.starcloud.ops.business.app.service.app.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.recommend.RecommendAppCache;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

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
    private AppPublishService appPublishService;

    @Resource
    private AppDictionaryService appDictionaryService;

    /**
     * 查询应用分类列表
     *
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categories() {
        // 查询应用分类字典数据
        return appDictionaryService.categories();
    }

    /**
     * 查询应用语言列表
     *
     * @return 应用语言列表
     */
    @Override
    public List<Option> languages() {
        return LanguageEnum.languageList();
    }

    /**
     * 查询推荐的应用列表
     *
     * @return 应用列表
     */
    @Override
    public List<AppRespVO> listRecommendedApps(String model) {
        return RecommendAppCache.get(model);
    }

    /**
     * 查询推荐的应用详情
     *
     * @param uid 推荐应用唯一标识
     * @return 应用详情
     */
    @Override
    public AppRespVO getRecommendApp(String uid) {
        return RecommendAppCache.getRecommendApp(uid);
    }

    /**
     * 获取步骤列表
     *
     * @return 步骤列表
     */
    @Override
    public List<WorkflowStepWrapperRespVO> stepList() {
        return Collections.singletonList(RecommendStepWrapperFactory.defDefaultTextCompletionStepWrapper());
    }

    /**
     * 分页查询应用列表
     *
     * @param query 查询条件
     * @return 应用列表
     */
    @Override
    public PageResp<AppRespVO> page(AppPageQuery query) {
        Page<AppDO> page = appMapper.page(query);
        return AppConvert.INSTANCE.convertPage(page);
    }

    /**
     * 根据应用 UID 获取应用详情
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    @Override
    public AppRespVO get(String uid) {
        AppDO app = appMapper.get(uid, Boolean.FALSE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, uid);
        return AppConvert.INSTANCE.convertResponse(app);
    }

    /**
     * 根据应用 UID 获取应用详情-简单
     *
     * @param uid 应用 UID
     * @return 应用详情
     */
    @Override
    public AppRespVO getSimple(String uid) {
        AppDO app = appMapper.get(uid, Boolean.TRUE);
        AppValidate.notNull(app, ErrorCodeConstants.APP_NO_EXISTS_UID, uid);
        return AppConvert.INSTANCE.convertResponse(app,  Boolean.FALSE);
    }

    /**
     * 创建应用
     *
     * @param request 应用信息
     */
    @Override
    public AppRespVO create(AppReqVO request) {
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);
        appEntity.insert();
        return AppConvert.INSTANCE.convertResponse(appEntity);
    }

    /**
     * 复制应用
     *
     * @param request 模版应用
     */
    @Override
    public AppRespVO copy(AppReqVO request) {
        request.setName(request.getName() + " - Copy");
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);
        appEntity.insert();
        return AppConvert.INSTANCE.convertResponse(appEntity);
    }

    /**
     * 更新应用
     *
     * @param request 更新请求信息
     */
    @Override
    public AppRespVO modify(AppUpdateReqVO request) {
        AppEntity appEntity = AppConvert.INSTANCE.convert(request);
        appEntity.setUid(request.getUid());
        appEntity.update();
        return AppConvert.INSTANCE.convertResponse(appEntity);
    }

    /**
     * 根据应用 UID 删除应用
     *
     * @param uid 应用 UID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        // 删除应用
        appMapper.delete(uid);
        // 删除应用发布信息
        appPublishService.deleteByAppUid(uid);
    }

    /**
     * 获取最新的wx mp聊天应用Uid
     */
    @Override
    public AppRespVO getRecently(Long userId) {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class)
                .eq(AppDO::getSource, AppSourceEnum.WX_WP.name())
                .eq(AppDO::getModel, AppModelEnum.CHAT.name())
                .eq(AppDO::getType, AppTypeEnum.MYSELF.name())
                .eq(AppDO::getCreator, userId)
                .orderByDesc(AppDO::getUpdateTime)
                .last("limit 1");
        AppDO appDO = appMapper.selectOne(wrapper);
        if (appDO == null) {
            return null;
        }
        return AppConvert.INSTANCE.convertResponse(appMapper.selectOne(wrapper));
    }

    /**
     * 异步执行应用
     *
     * @param request 应用执行请求信息
     */
    @Override
    @SuppressWarnings("all")
    public void asyncExecute(AppExecuteReqVO request) {
        BaseAppEntity app = AppFactory.factory(request);
        app.asyncExecute(request);
    }
}
