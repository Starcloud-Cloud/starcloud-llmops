package com.starcloud.ops.business.app.dal.mysql.xhs.content;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentPageReqVOV2;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentTaskReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.util.PageUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Mapper
public interface CreativeContentMapper extends BaseMapperX<CreativeContentDO> {

    /**
     * 根据创作内容UID查询创作内容详情
     *
     * @param uid 创作内容UID
     * @return 创作内容详情
     */
    default CreativeContentDO get(String uid) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
        wrapper.eq(CreativeContentDO::getUid, uid);
        return selectOne(wrapper);
    }

    @DataPermission(enable = false)
    default void updateByUid(CreativeContentDO creativeContentDO) {
        LambdaUpdateWrapper<CreativeContentDO> wrapper = Wrappers.lambdaUpdate(CreativeContentDO.class)
                .eq(CreativeContentDO::getUid, creativeContentDO.getUid());
        update(creativeContentDO, wrapper);
    }

    /**
     * 根据条件查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    default List<CreativeContentDO> list(CreativeContentListReqVO query) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
        wrapper.in(CollectionUtil.isNotEmpty(query.getUidList()), CreativeContentDO::getUid, query.getUidList());
        wrapper.eq(StringUtils.isNotBlank(query.getBatchUid()), CreativeContentDO::getBatchUid, query.getBatchUid());
        wrapper.eq(StringUtils.isNotBlank(query.getPlanUid()), CreativeContentDO::getPlanUid, query.getPlanUid());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), CreativeContentDO::getStatus, query.getStatus());
        wrapper.eq(Objects.nonNull(query.getLiked()), CreativeContentDO::getLiked, query.getLiked());
        wrapper.eq(Objects.nonNull(query.getClaim()), CreativeContentDO::getClaim, query.getClaim());
        return selectList(wrapper);
    }

    /**
     * 根据条件查询创作内容列表，只查询id, uid, status 字段
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    default List<CreativeContentDO> listStatus(CreativeContentListReqVO query) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
        wrapper.select(CreativeContentDO::getId, CreativeContentDO::getUid, CreativeContentDO::getStatus);
        wrapper.in(CollectionUtil.isNotEmpty(query.getUidList()), CreativeContentDO::getUid, query.getUidList());
        wrapper.eq(StringUtils.isNotBlank(query.getBatchUid()), CreativeContentDO::getBatchUid, query.getBatchUid());
        wrapper.eq(StringUtils.isNotBlank(query.getPlanUid()), CreativeContentDO::getPlanUid, query.getPlanUid());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), CreativeContentDO::getStatus, query.getStatus());
        wrapper.eq(Objects.nonNull(query.getLiked()), CreativeContentDO::getLiked, query.getLiked());
        wrapper.eq(Objects.nonNull(query.getClaim()), CreativeContentDO::getClaim, query.getClaim());
        return selectList(wrapper);
    }

    /**
     * 查询创作内容任务列表
     *
     * @param query 查询条件
     * @return 创作内容任务列表
     */
    List<CreativeContentDO> listTask(@Param("query") CreativeContentTaskReqVO query);

    /**
     * 分页查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    default IPage<CreativeContentDO> page(@Param("query") CreativeContentPageReqVOV2 query) {
        // 构造分页条件
        Page<CreativeContentDO> page = PageUtil.page(query);
        // 构造查询条件
        LambdaQueryWrapperX<CreativeContentDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.select(CreativeContentDO.class, item -> !"execute_param".equalsIgnoreCase(item.getColumn()));
        wrapper.likeRight(StringUtils.isNotBlank(query.getTitle()), CreativeContentDO::getExecuteTitle, query.getTitle());
        wrapper.betweenIfPresent(CreativeContentDO::getCreateTime, query.getCreateTime());
        if (StringUtils.isNotBlank(query.getTag())) {
            wrapper.apply("FIND_IN_SET({0}, execute_tags)", query.getTag());
        }
        wrapper.orderByDesc(CreativeContentDO::getId);
        // 执行查询
        return selectPage(page, wrapper);
    }

    /**
     * 分页查询创作内容列表
     *
     * @param query 查询条件
     * @return 创作内容列表
     */
    default IPage<CreativeContentDO> page(CreativeContentPageReqVO query) {
        // 构造分页条件
        Page<CreativeContentDO> page = PageUtil.page(query);

        // 构造查询条件
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
        wrapper.select(CreativeContentDO.class, item -> !"execute_param".equalsIgnoreCase(item.getColumn()));
        wrapper.eq(StringUtils.isNotBlank(query.getBatchUid()), CreativeContentDO::getBatchUid, query.getBatchUid());
        wrapper.eq(StringUtils.isNotBlank(query.getPlanUid()), CreativeContentDO::getPlanUid, query.getPlanUid());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), CreativeContentDO::getStatus, query.getStatus());
        wrapper.eq(Objects.nonNull(query.getLiked()), CreativeContentDO::getLiked, query.getLiked());
        wrapper.eq(Objects.nonNull(query.getClaim()), CreativeContentDO::getClaim, query.getClaim());
        if (BooleanUtils.isTrue(query.getDesc())) {
            wrapper.orderByDesc(CreativeContentDO::getId);
        } else {
            wrapper.orderByAsc(CreativeContentDO::getId);
        }
        // 执行查询
        return selectPage(page, wrapper);
    }

    /**
     * 根据创作计划UID删除创作内容
     *
     * @param planUid 创作计划UID
     */
    default void deleteByPlanUid(String planUid) {
        LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class)
                .eq(CreativeContentDO::getPlanUid, planUid);
        delete(wrapper);
    }

    /**
     * 认领任务
     *
     * @param uidList 创作内容UID列表
     * @param claim   是否认领
     */
    default void claim(List<String> uidList, Boolean claim) {
        LambdaUpdateWrapper<CreativeContentDO> updateWrapper = Wrappers.lambdaUpdate(CreativeContentDO.class);
        updateWrapper.in(CreativeContentDO::getUid, uidList);
        updateWrapper.set(CreativeContentDO::getClaim, claim);
        updateWrapper.set(CreativeContentDO::getUpdateTime, LocalDateTime.now());
        this.update(updateWrapper);
    }

}
