package com.starcloud.ops.business.app.dal.mysql.market;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starcloud.ops.business.app.api.app.dto.AppCategoryDTO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 应用市场表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2023-06-05
 */
@Mapper
public interface AppMarketMapper extends BaseMapper<AppMarketDO> {


    List<AppCategoryDTO> statisticsCountByCategory(@Param("audits") List<Integer> audits);

    /**
     * 根据 uid 和 version 更新审核状态 <br>
     * 1. 如果 version 与数据库中的 version 相同，则更新为审核通过 <br>
     * 2. 如果 version 与数据库中的 version 不同，则更新为审核不通过 <br>
     *
     * @param uid     uid
     * @param version version
     */
    void approvedAuditByUidAndVersion(@Param("uid") String uid, @Param("version") Integer version);

}
