package com.starcloud.ops.business.trade.service.rights.bo;

import com.starcloud.ops.business.product.api.property.dto.ProductPropertyValueDetailRespDTO;
import com.starcloud.ops.business.product.api.spu.dto.GiveRightsDTO;
import com.starcloud.ops.business.promotion.enums.common.PromotionTypeEnum;
import com.starcloud.ops.business.trade.enums.order.TradeOrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * 权益计算 Response BO
 *
 * 整体设计，参考 taobao 的技术文档：
 * 1. <a href="https://developer.alibaba.com/docs/doc.htm?treeId=1&articleId=1029&docType=1">订单管理</a>
 * 2. <a href="https://open.taobao.com/docV3.htm?docId=108471&docType=1">常用订单金额说明</a>
 *
 * @author 芋道源码
 */
@Data
public class TradeRightsCalculateRespBO {

    // ========== 权益 相关字段 =========
    @Schema(description = "权益参数")
    @Valid
    private List<GiveRightsDTO> giveRights;

}
