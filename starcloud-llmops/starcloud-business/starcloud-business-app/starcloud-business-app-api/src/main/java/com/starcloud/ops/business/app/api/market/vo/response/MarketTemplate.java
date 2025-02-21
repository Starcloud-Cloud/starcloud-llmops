package com.starcloud.ops.business.app.api.market.vo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class MarketTemplate implements Serializable {

    private static final long serialVersionUID = -3243386228766373114L;

    private String uuid;

    private String code;

    private String name;

    /**
     * 图片模板分组
     */
    private Long group;

    /**
     * 图片模板分组名称
     */
    private String groupName;

    private String example;
}
