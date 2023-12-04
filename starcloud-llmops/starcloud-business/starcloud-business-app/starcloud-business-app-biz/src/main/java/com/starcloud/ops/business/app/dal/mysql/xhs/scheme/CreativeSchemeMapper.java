package com.starcloud.ops.business.app.dal.mysql.xhs.scheme;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.scheme.CreativeSchemeDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Mapper
public interface CreativeSchemeMapper extends BaseMapper<CreativeSchemeDO> {

    /**
     * 根据UID查询方案详情
     *
     * @param uid UID
     * @return 方案详情
     */
    CreativeSchemeDO get(@Param("uid") String uid);

    /**
     * 根据条件查询方案列表
     *
     * @param query 查询条件
     * @return 方案列表
     */
    List<CreativeSchemeDO> list(@Param("query") CreativeSchemeListReqVO query);

    /**
     * 根据条件查询方案列表
     *
     * @param list 方案UID列表
     * @return 方案列表
     */
    List<CreativeSchemeDO> listByUidList(@Param("uidList") List<String> list);

    /**
     * 分页查询方案列表
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 方案列表
     */
    IPage<CreativeSchemeDO> page(Page<LogAppConversationDO> page, @Param("query") CreativeSchemePageReqVO query);

    /**
     * 根据名称查询
     *
     * @param name 名称
     * @return 是否存在
     */
    default Boolean distinctName(String name) {
        LambdaQueryWrapper<CreativeSchemeDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CreativeSchemeDO::getName, name);
        return this.selectCount(wrapper) > 0;
    }

}
