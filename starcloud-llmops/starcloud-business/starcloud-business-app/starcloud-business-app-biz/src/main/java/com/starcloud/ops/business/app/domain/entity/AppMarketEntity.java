package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.market.vo.response.MarketStyle;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.api.verification.Verification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Slf4j
@Data
public class AppMarketEntity extends AppEntity {

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
     * 开启视频生成
     */
    private Boolean openVideoMode;

    private List<MarketStyle> styles;

    /**
     * 应用市场数据库操作类
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppMarketRepository appMarketRepository = SpringUtil.getBean(AppMarketRepository.class);

    @JsonIgnore
    @JSONField(serialize = false)
    private static AppMarketService appMarketService = SpringUtil.getBean(AppMarketService.class);

    /**
     * 校验
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected List<Verification> doValidate(AppExecuteReqVO request, ValidateTypeEnum validateType) {
        return super.doValidate(request, validateType);
    }

    /**
     * 只用 应用创建者
     * 注意，创建应用的时候，要设置 creator 为当前用户态
     *
     * @param req 请求
     * @return 用户 id
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Long getRunUserId(AppExecuteReqVO req) {
        return SecurityFrameworkUtils.getLoginUserId();
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected AppExecuteRespVO doExecute(AppExecuteReqVO request) {

        log.info("应用市场执行开始......");
        AppExecuteRespVO appExecuteResponse = super.doExecute(request);

        log.info("应用市场执行，增加应用使用量开始");
        if (appExecuteResponse != null) {
            AppOperateReqVO appOperateRequest = new AppOperateReqVO();
            appOperateRequest.setAppUid(this.getUid());
            appOperateRequest.setTenantId(this.getTenantId());
            appOperateRequest.setUserId(Long.toString(request.getUserId()));
            appOperateRequest.setOperate(AppOperateTypeEnum.USAGE.name());
            appMarketService.operate(appOperateRequest);
        }
        log.info("应用市场执行，增加应用使用量结束");
        return appExecuteResponse;
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected AppExecuteRespVO doAsyncExecute(AppExecuteReqVO request) {
        return super.doAsyncExecute(request);
    }

    /**
     * 新增应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doInsert() {
        appMarketRepository.insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doUpdate() {
        appMarketRepository.update(this);
    }


}
