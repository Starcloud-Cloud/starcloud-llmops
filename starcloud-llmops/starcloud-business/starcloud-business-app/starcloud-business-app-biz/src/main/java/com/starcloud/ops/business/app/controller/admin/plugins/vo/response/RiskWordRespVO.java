package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import com.starcloud.ops.business.app.api.Plugin.AliRiskReason;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RiskWordRespVO {

    private List<Map<String, Object>> materialList;

    private AliRiskReason reason;

    public RiskWordRespVO(List<Map<String, Object>> materialList, AliRiskReason reason) {
        this.materialList = materialList;
        this.reason = reason;
    }
}
