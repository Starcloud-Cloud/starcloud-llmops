package com.starcloud.ops.business.app.service.app.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.category.CategoryConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.recommend.RecommendedAppCache;
import com.starcloud.ops.business.app.domain.recommend.RecommendedStepWrapperFactory;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.enums.LanguageEnum;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        return dictDataList.stream().map(CategoryConvert.INSTANCE::convert).filter(Objects::nonNull).collect(Collectors.toList());
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
    public List<AppRespVO> listRecommendedApps() {
        return RecommendedAppCache.get();
    }

    /**
     * 查询推荐的应用详情
     *
     * @param recommend 推荐应用唯一标识
     * @return 应用详情
     */
    @Override
    public AppRespVO getRecommendApp(String recommend) {
        return RecommendedAppCache.getRecommendApp(recommend);
    }

    /**
     * 获取步骤列表
     *
     * @return 步骤列表
     */
    @Override
    public List<WorkflowStepWrapperRespVO> stepList() {
        return Collections.singletonList(RecommendedStepWrapperFactory.defDefaultTextCompletionStepWrapper());
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
        List<AppRespVO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream()
                .map(AppConvert.INSTANCE::convertResp).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
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
        return AppConvert.INSTANCE.convertResp(app);
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
    public void delete(String uid) {
        appMapper.delete(uid);
    }

}
