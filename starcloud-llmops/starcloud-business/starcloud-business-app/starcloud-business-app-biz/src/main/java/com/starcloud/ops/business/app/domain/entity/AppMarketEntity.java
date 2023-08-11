package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Data
public class AppMarketEntity extends AppEntity<AppExecuteReqVO, AppExecuteRespVO> {

    private AppMarketService appMarketService = SpringUtil.getBean(AppMarketService.class);

    /**
     * 应用版本
     */
    private Integer version;

    /**
     * 版本名称
     */
    private String language;

    /**
     * 审核状态
     */
    private Integer audit;

    /**
     * 示例
     */
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
    protected void _validate(AppExecuteReqVO req) {

    }

    /**
     * 只用 应用创建者
     * 注意，创建应用的时候，要设置 creator 为当前用户态
     *
     * @param req
     * @return
     */
    @Override
    protected Long getRunUserId(AppExecuteReqVO req) {
        return super.getRunUserId(req);
    }

    @Override
    protected AppExecuteRespVO _execute(AppExecuteReqVO req) {

        AppExecuteRespVO appExecuteRespVO = super._execute(req);

        if (appExecuteRespVO != null) {

            AppOperateReqVO appOperateReqVO = new AppOperateReqVO();
            appOperateReqVO.setAppUid(this.getUid());
            appOperateReqVO.setVersion(AppConstants.DEFAULT_VERSION);
            appOperateReqVO.setOperate(AppOperateTypeEnum.USAGE.name());
            appMarketService.operate(appOperateReqVO);
        }

        return appExecuteRespVO;
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
