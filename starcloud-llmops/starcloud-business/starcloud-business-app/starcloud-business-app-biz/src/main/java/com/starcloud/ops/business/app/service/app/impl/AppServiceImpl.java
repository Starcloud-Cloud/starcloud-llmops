package com.starcloud.ops.business.app.service.app.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.business.app.convert.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.redis.app.RecommendedAppRedisDAO;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.enums.AppTypeEnum;
import com.starcloud.ops.business.app.exception.AppException;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.dto.SortQuery;
import com.starcloud.ops.framework.common.api.enums.SortType;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模版管理服务实现类
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
    private RecommendedAppRedisDAO recommendedAppRedisDAO;

    /**
     * 查询推荐的模版列表
     *
     * @return 模版列表
     */
    @Override
    public List<AppDTO> listRecommendedTemplates() {
        // 缓存中获取，如果有则直接返回
        List<AppDTO> list = recommendedAppRedisDAO.get();
        if (CollectionUtil.isNotEmpty(list)) {
            return list;
        }

        // 缓存中没有，则从数据库中获取最新数据，并且存入缓存，返回数据
        return recommendedAppRedisDAO.set();
    }

    /**
     * 分页查询模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<AppDTO> page(AppPageQuery query) {
        // 默认按照更新时间倒序
        query.setSorts(Collections.singletonList(SortQuery.of("update_time", SortType.DESC.name())));

        // 构建查询条件
        LambdaQueryWrapper<AppDO> wrapper = buildPageQueryWrapper()
                .likeLeft(StringUtils.isNotBlank(query.getName()), AppDO::getName, query.getName())
                .eq(StringUtils.isNotBlank(query.getType()), AppDO::getType, query.getType());

        // 执行分页查询
        Page<AppDO> page = appMapper.selectPage(PageUtil.page(query), wrapper);
        List<AppDTO> list = CollectionUtil.emptyIfNull(page.getRecords()).stream().map(AppConvert::convert).collect(Collectors.toList());
        return PageResp.of(list, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 分页查询下载的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<AppDTO> pageDownloadTemplates(AppPageQuery query) {
        query.setType(AppTypeEnum.DOWNLOAD_TEMPLATE.name());
        return this.page(query);
    }

    /**
     * 分页查询我的模版列表
     *
     * @param query 查询条件
     * @return 模版列表
     */
    @Override
    public PageResp<AppDTO> pageMyTemplate(AppPageQuery query) {
        query.setType(AppTypeEnum.MY_TEMPLATE.name());
        return this.page(query);
    }

    /**
     * 根据模版 ID 获取模版详情
     *
     * @param id 模版 ID
     * @return 模版详情
     */
    @Override
    public AppDTO getById(Long id) {
        AppDO appDO = appMapper.selectById(id);
        Assert.notNull(appDO, () -> AppException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, id));
        return AppConvert.convert(appDO);
    }

    /**
     * 根据模版 UID 获取模版详情
     *
     * @param uid 模版 UID
     * @return 模版详情
     */
    @Override
    public AppDTO getByUid(String uid) {
        LambdaQueryWrapper<AppDO> wrapper = buildBaseQueryWrapper().eq(AppDO::getUid, uid);
        AppDO templateDO = appMapper.selectOne(wrapper);
        Assert.notNull(templateDO, () -> AppException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, uid));
        return AppConvert.convert(templateDO);
    }

    /**
     * 创建模版
     *
     * @param request 模版信息
     * @return 是否创建成功
     */
    @Override
    public Boolean create(AppRequest request) {
        try {
            duplicateNameVerification(request);
            AppDO appDO = AppConvert.convertCreate(request);
            // 生成唯一 ID
            appDO.setUid(IdUtil.fastSimpleUUID());
            appMapper.insert(appDO);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedAppRedisDAO.resetByType(appDO.getType());
            return Boolean.TRUE;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppException.exception(AppResultCode.TEMPLATE_CREATE_FAILED, e.getMessage());
        }
    }

    /**
     * 复制模版
     *
     * @param request 模版信息
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(AppRequest request) {
        try {
            String name = request.getName() + "-Copy";
            request.setName(name);
            duplicateNameVerification(request);
            AppDO appDO = AppConvert.convertCreate(request);
            // 生成唯一 ID
            appDO.setUid(IdUtil.fastSimpleUUID());
            appMapper.insert(appDO);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedAppRedisDAO.resetByType(appDO.getType());
            return Boolean.TRUE;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppException.exception(AppResultCode.TEMPLATE_COPY_FAILED, e.getMessage());
        }
    }

    /**
     * 更新模版
     *
     * @param request 模版信息
     * @return 是否更新成功
     */
    @Override
    public Boolean modify(AppUpdateRequest request) {
        try {
            duplicateNameVerification(request);
            AppDO appDO = AppConvert.convertModify(request);
            LambdaUpdateWrapper<AppDO> wrapper = buildBaseUpdateWrapper().eq(AppDO::getUid, request.getUid());
            appMapper.update(appDO, wrapper);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedAppRedisDAO.resetByType(appDO.getType());
            return Boolean.TRUE;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppException.exception(AppResultCode.TEMPLATE_MODIFY_FAILED, e.getMessage());
        }
    }

    /**
     * 删除模版
     *
     * @param id 模版ID
     * @return 是否删除成功
     */
    @Override
    public Boolean delete(Long id) {
        try {
            // 判断模版是否存在, 不存在则抛出异常
            AppDO appDO = appMapper.selectById(id);
            Assert.notNull(appDO, () -> AppException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, id));
            // 根据 ID 删除模版
            appMapper.deleteById(id);
            // 如果新增的是系统模版，则需要更新缓存
            recommendedAppRedisDAO.resetByType(appDO.getType());
            return Boolean.TRUE;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppException.exception(AppResultCode.TEMPLATE_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 根据模版 UID 删除模版
     *
     * @param uid 模版 UID
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteByUid(String uid) {
        try {
            // 判断模版是否存在, 不存在则抛出异常
            AppDO appDO = appMapper.selectOne(buildBaseQueryWrapper().eq(AppDO::getUid, uid));
            Assert.notNull(appDO, () -> AppException.exception(AppResultCode.TEMPLATE_NOT_EXISTS, uid));
            // 根据 ID 删除模版
            appMapper.deleteById(appDO.getId());
            // 如果新增的是系统模版，则需要更新缓存
            recommendedAppRedisDAO.resetByType(appDO.getType());
            return Boolean.TRUE;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppException.exception(AppResultCode.TEMPLATE_DELETE_FAILED, e.getMessage());
        }
    }

    /**
     * 校验模版是否已经下载过
     *
     * @param marketUid 模版市场特有的 key，唯一。
     * @return 是否已经下载, true: 已经下载, false: 未下载
     */
    @Override
    public Boolean verifyHasDownloaded(String marketUid) {
        try {
            LambdaQueryWrapper<AppDO> wrapper = buildBaseQueryWrapper().eq(AppDO::getDownloadUid, marketUid).eq(AppDO::getType, AppTypeEnum.DOWNLOAD_TEMPLATE.name());
            Long count = appMapper.selectCount(wrapper);
            return count > 0;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppException.exception(AppResultCode.TEMPLATE_VERIFY_HAS_DOWNLOADED_FAILED, e.getMessage());
        }
    }


    /**
     * 模版名称重复校验, 重复抛出异常
     *
     * @param name 模版名称
     * @return 是否重复 true: 重复, false: 不重复
     */
    @Override
    public Boolean duplicateNameVerification(String name) {
        try {
            LambdaQueryWrapper<AppDO> wrapper = buildBaseQueryWrapper().eq(AppDO::getName, name);
            Long count = appMapper.selectCount(wrapper);
            return count > 0;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw AppException.exception(AppResultCode.TEMPLATE_DEFAULT_ERROR, e.getMessage());
        }
    }

    /**
     * 模版名称重复校验, 重复抛出异常
     *
     * @param request 模版信息
     */
    private void duplicateNameVerification(AppRequest request) {
        if (duplicateNameVerification(request.getName())) {
            throw AppException.exception(AppResultCode.TEMPLATE_NAME_DUPLICATE, request.getName());
        }
    }

    /**
     * 构建基础更新条件
     *
     * @return 更新条件
     */
    public static LambdaUpdateWrapper<AppDO> buildBaseUpdateWrapper() {
        return Wrappers.lambdaUpdate(AppDO.class).eq(AppDO::getStatus, StateEnum.ENABLE.getCode());
    }

    /**
     * 构建基础查询条件
     *
     * @return 查询条件
     */
    public static LambdaQueryWrapper<AppDO> buildBaseQueryWrapper() {
        return Wrappers.lambdaQuery(AppDO.class).eq(AppDO::getStatus, StateEnum.ENABLE.getCode());
    }

    /**
     * 构建分页查询条件, 仅查询指定字段
     *
     * @return 查询条件
     */
    public static LambdaQueryWrapper<AppDO> buildPageQueryWrapper() {
        return Wrappers.lambdaQuery(AppDO.class).select(
                        AppDO::getUid,
                        AppDO::getUploadUid,
                        AppDO::getDownloadUid,
                        AppDO::getType,
                        AppDO::getLogotype,
                        AppDO::getSourceType,
                        AppDO::getVersion,
                        AppDO::getName,
                        AppDO::getDescription,
                        AppDO::getIcon,
                        AppDO::getTags,
                        AppDO::getCategories,
                        AppDO::getScenes,
                        AppDO::getCreator,
                        AppDO::getUpdater,
                        AppDO::getCreateTime,
                        AppDO::getUpdateTime
                )
                .eq(AppDO::getStatus, StateEnum.ENABLE.getCode());
    }
}
