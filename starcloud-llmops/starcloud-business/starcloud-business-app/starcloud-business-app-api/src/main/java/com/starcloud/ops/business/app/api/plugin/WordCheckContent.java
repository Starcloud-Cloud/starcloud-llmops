package com.starcloud.ops.business.app.api.plugin;

import lombok.Data;

import java.util.List;

@Data
public class WordCheckContent {

    private Integer status;

    private String resContent;

    private String topRiskStr;

    private String lowRiskStr;

    private Long contentLength;

    private List<RiskWord> riskList;
}
