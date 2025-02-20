package com.starcloud.ops.business.app.api.market.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class MarketStyle implements Serializable {

    private static final long serialVersionUID = 8024672283278382797L;

    private String uuid;

    private String styleName;

    private StyleSaleInfo saleConfig;

    private Boolean openVideoMode;

    private List<MarketTemplate> templateList;

}
