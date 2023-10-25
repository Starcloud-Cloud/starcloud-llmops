package com.starcloud.ops.business.app.service.app.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.recommend.RecommendAppCache;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.mq.producer.AppDeleteProducer;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Resource
    private AppDeleteProducer appDeleteProducer;

    /**
     * 查询应用语言列表
     *
     * @return 应用语言列表
     */
    @Override
    public Map<String, List<Option>> metadata() {
        Map<String, List<Option>> metadata = new HashMap<>();
        // 语言列表
        metadata.put("language", LanguageEnum.languageList());
        // AI 模型
        metadata.put("aiModel", AppUtils.aiModelList());
        // 应用类型
        metadata.put("type", AppTypeEnum.options());
        return metadata;
    }

    /**
     * 查询应用分类列表
     *
     * @param isRoot 是否只根节点数据
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categoryList(Boolean isRoot) {
        return appDictionaryService.categoryList(isRoot);
    }

    /**
     * 查询应用分类列表
     *
     * @return 应用分类列表
     */
    @Override
    public List<AppCategoryVO> categoryTree() {
        // 查询应用分类字典数据
        return appDictionaryService.categoryTree();
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
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, uid);
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
        AppValidate.notNull(app, ErrorCodeConstants.APP_NON_EXISTENT, uid);
        return AppConvert.INSTANCE.convertResponse(app, Boolean.FALSE);
    }

    /**
     * 创建应用
     *
     * @param request 应用信息
     */
    @Override
    public AppRespVO create(AppReqVO request) {
        handlerAndValidateRequest(request);
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
        handlerAndValidateRequest(request);
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
        handlerAndValidateRequest(request);
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
        // 删除其他资源
        appDeleteProducer.send(uid);
    }

    /**
     * 获取最新的wx mp聊天应用Uid
     */
    @Override
    public AppRespVO getRecently(Long userId) {
        LambdaQueryWrapper<AppDO> wrapper = Wrappers.lambdaQuery(AppDO.class)
                .eq(AppDO::getSource, AppSourceEnum.WX_WP.name())
                .eq(AppDO::getModel, AppModelEnum.CHAT.name())
                .eq(AppDO::getType, AppTypeEnum.COMMON.name())
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
        try {
            BaseAppEntity app = AppFactory.factory(request);
            app.asyncExecute(request);
        } catch (Exception exception) {
            if (request.getSseEmitter() != null) {
                request.getSseEmitter().completeWithError(exception);
            }
        }
    }

    /**
     * 处理校验请求
     *
     * @param request 请求信息
     */
    private void handlerAndValidateRequest(AppReqVO request) {
        // 应用类目校验
        if (AppModelEnum.COMPLETION.name().equals(request.getModel())) {
            if (StringUtils.isBlank(request.getCategory())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CATEGORY_REQUIRED, request.getCategory());
            }
            List<AppCategoryVO> categoryList = appDictionaryService.categoryList(Boolean.FALSE);
            Optional<AppCategoryVO> categoryOptional = categoryList.stream().filter(category -> category.getCode().equals(request.getCategory())).findFirst();
            if (!categoryOptional.isPresent()) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CATEGORY_NONSUPPORT, request.getCategory());
            }
            AppCategoryVO category = categoryOptional.get();
            if (AppConstants.ROOT.equals(category.getParentCode())) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CATEGORY_NONSUPPORT_FIRST, request.getCategory());
            }
            if (StringUtils.isBlank(request.getIcon())) {
                request.setIcon(category.getIcon());
            }
            // 图片默认为分类图片
            request.setImages(Collections.singletonList(category.getImage()));
        }

        // 未指定应用类型，默认为普通应用
        if (StringUtils.isBlank(request.getType())) {
            request.setType(AppTypeEnum.COMMON.name());
        }

        // 非普通应用，只有管理员可以创建
        if (!AppTypeEnum.COMMON.name().equals(request.getType()) && UserUtils.isNotAdmin()) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_TYPE_NONSUPPORT, request.getType());
        }
    }
}
