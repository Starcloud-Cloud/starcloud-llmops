package com.starcloud.ops.business.app.dal.mysql.market;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 审核数据，审核通过：该版本的审核状态改为已通过，其余的版本改为未通过
     *
     * @param uid     应用uid
     * @param version 版本号
     */
    @Update("UPDATE llm_app_market SET audit = CASE WHEN version = #{version} THEN 1 WHEN version != #{version} THEN 2 ELSE audit END WHERE uid = #{uid} AND deleted = 0")
    void approvedAuditByUidAndVersion(@Param("uid") String uid, @Param("version") Integer version);

}
