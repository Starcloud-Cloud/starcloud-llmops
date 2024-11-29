package com.starcloud.ops.business.app.api.Plugin;

import lombok.Data;

@Data
public class AliRiskReason {

    /**
     * 风险等级 high    medium    low
     */
    private String riskLevel;

    /**
     * 细分标签
     */
    private String riskTips;

    /**
     * 命中风险词
     */
    private String riskWords;

    /**
     * 命中广告号
     */
    private String adNums;

    /**
     * 命中用户词
     */
    private String customizedWords;

    /**
     * 命中用户词库名
     */
    private String customizedLibs;

}
