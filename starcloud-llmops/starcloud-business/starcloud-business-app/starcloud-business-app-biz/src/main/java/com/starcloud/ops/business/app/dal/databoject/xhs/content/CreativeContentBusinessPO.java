package com.starcloud.ops.business.app.dal.databoject.xhs.content;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
public class CreativeContentBusinessPO implements java.io.Serializable {

    private static final long serialVersionUID = -8083569201030271228L;

    /**
     * 计划uid
     */
    private String planUid;

    /**
     * 业务uid
     */
    private String businessUid;

    /**
     * 状态
     */
    private String status;


}
