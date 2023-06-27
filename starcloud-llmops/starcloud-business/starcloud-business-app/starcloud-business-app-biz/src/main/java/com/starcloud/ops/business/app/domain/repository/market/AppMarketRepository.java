package com.starcloud.ops.business.app.domain.repository.market;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.market.AppMarketAuditEnum;
import com.starcloud.ops.business.app.util.app.AppUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;

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
    public AppMarketEntity get(String uid, Integer version) {
        AppMarketDO appMarketDO = appMarketMapper.get(uid, version, Boolean.FALSE);
        return AppMarketConvert.INSTANCE.convert(appMarketDO);
    }

    /**
     * 新增应用
     *
     * @param appMarketEntity 应用实体
     */
    public void insert(AppMarketEntity appMarketEntity) {
        AppMarketDO appMarket = AppMarketConvert.INSTANCE.convert(appMarketEntity);
        // 新增 UID 重新生成
        appMarket.setUid(AppUtils.generateUid(AppConstants.MARKET_PREFIX));
        // 新增版本号默认为 1
        appMarket.setVersion(AppConstants.DEFAULT_VERSION);
        // 新增，点赞，查看，安装量为 0
        appMarket.setLikeCount(0);
        appMarket.setViewCount(0);
        appMarket.setInstallCount(0);
        // 目前模版市场只有免费版
        appMarket.setFree(Boolean.TRUE);
        appMarket.setCost(BigDecimal.ZERO);
        // 默认为未删除状态
        appMarket.setDeleted(Boolean.FALSE);
        // 新增审核状态统一为待审核
        appMarket.setAudit(AppMarketAuditEnum.PENDING.getCode());
        //
        appMarketMapper.insert(appMarket);
    }

    /**
     * 更新应用
     *
     * @param appMarketEntity 应用实体
     */
    public void update(AppMarketEntity appMarketEntity) {
        // 判断应用是否存在, 不存在无法修改
        appMarketMapper.get(appMarketEntity.getUid(), appMarketEntity.getVersion(), Boolean.TRUE);
        // 应用实体转换为应用 DO
        AppMarketDO appMarket = AppMarketConvert.INSTANCE.convert(appMarketEntity);
        // 版本号
        appMarket.setVersion(AppUtils.nextVersion(appMarket.getVersion()));
        // 目前模版市场只有免费版
        appMarket.setFree(Boolean.TRUE);
        appMarket.setCost(BigDecimal.ZERO);
        // 默认为未删除状态
        appMarket.setDeleted(Boolean.FALSE);
        // 修改审核状态统一为待审核
        appMarket.setAudit(AppMarketAuditEnum.PENDING.getCode());

        // 修改应用市场应用
        LambdaUpdateWrapper<AppMarketDO> wrapper = Wrappers.lambdaUpdate(AppMarketDO.class)
                .eq(AppMarketDO::getVersion, appMarketEntity.getUid())
                .eq(AppMarketDO::getVersion, appMarketEntity.getVersion());
        appMarketMapper.update(appMarket, wrapper);
    }

    /**
     * 删除应用
     *
     * @param uid 应用唯一标识
     */
    public void deleteByUidAndVersion(String uid, Integer version) {
        AppMarketDO appMarketDO = appMarketMapper.get(uid, version, Boolean.TRUE);
        appMarketMapper.deleteById(appMarketDO.getId());
    }

}
