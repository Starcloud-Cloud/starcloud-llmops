package com.starcloud.ops.business.app.domain.repository.market;

import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * App Repository
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Repository
public class AppMarketRepository {

    @Resource
    private AppMarketMapper appMarketMapper;

    /**
     * 根据 uid 查询应用
     *
     * @param uid 应用唯一标识
     * @return 应用实体
     */
    public AppMarketEntity get(String uid) {
        AppMarketDO appMarketDO = appMarketMapper.get(uid, Boolean.FALSE);
        return AppMarketConvert.INSTANCE.convert(appMarketDO);
    }

    /**
     * 新增应用
     *
     * @param appMarketEntity 应用实体
     */
    public void insert(AppMarketEntity appMarketEntity) {
        AppMarketDO appMarket = AppMarketConvert.INSTANCE.convert(appMarketEntity);
        appMarketMapper.create(appMarket);
    }

    /**
     * 更新应用
     *
     * @param appMarketEntity 应用实体
     */
    public void update(AppMarketEntity appMarketEntity) {
        AppMarketDO appMarket = AppMarketConvert.INSTANCE.convert(appMarketEntity);
        appMarketMapper.modify(appMarket);
    }

}
