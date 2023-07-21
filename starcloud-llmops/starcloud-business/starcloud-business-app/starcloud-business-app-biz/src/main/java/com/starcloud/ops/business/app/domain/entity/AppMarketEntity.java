package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Data
public class AppMarketEntity extends AppEntity<AppExecuteReqVO, JsonData> {

    /**
     * 应用版本
     */
    private Integer version;

    private String language;


    private String example;

    /**
     * 应用是否是免费的
     */

    private Boolean free;

    /**
     * 应用收费数
     */

    private BigDecimal cost;

    /**
     * 使用数量
     */
    private Integer usageCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 查看数量
     */
    private Integer viewCount;

    /**
     * 安装数量
     */
    private Integer installCount;

    /**
     * 应用市场数据库操作类
     */
    private static AppMarketRepository appMarketRepository;

    /**
     * 获取应用市场数据库操作类
     *
     * @return 应用市场数据库操作类
     */
    public static AppMarketRepository getAppMarketRepository() {
        if (appMarketRepository == null) {
            appMarketRepository = SpringUtil.getBean(AppMarketRepository.class);
        }
        return appMarketRepository;
    }

    /**
     * 校验
     */
    @Override
    protected void _validate() {

    }

    @Override
    protected JsonData _execute(AppExecuteReqVO req) {


        //        // 使用量加一
//        if (AppSceneEnum.WEB_MARKET.equals(appContext.getScene())) {
//            AppOperateReqVO appOperateReqVO = new AppOperateReqVO();
//            appOperateReqVO.setAppUid(appContext.getApp().getUid());
//            appOperateReqVO.setVersion(AppConstants.DEFAULT_VERSION);
//            appOperateReqVO.setOperate(AppOperateTypeEnum.USAGE.name());
//            appMarketService.operate(appOperateReqVO);
//        }

        return super._execute(req);

    }

    @Override
    protected void _aexecute(AppExecuteReqVO req) {

        super._aexecute(req);
    }

    /**
     * 新增应用
     */
    @Override
    protected void _insert() {

        getAppMarketRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    protected void _update() {

        getAppMarketRepository().update(this);
    }


}
