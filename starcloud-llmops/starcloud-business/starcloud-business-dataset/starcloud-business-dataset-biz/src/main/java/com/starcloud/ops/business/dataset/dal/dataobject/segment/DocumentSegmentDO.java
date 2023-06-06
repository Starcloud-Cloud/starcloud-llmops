package com.starcloud.ops.business.dataset.dal.dataobject.segment;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("llm_document_segments")
@KeySequence("llm_document_segments")
public class DocumentSegmentDO extends TenantBaseDO {

    private static final long serialVersionUID = 3500466566973435280L;

    private String id;

    private String datasetId;

    private String documentId;

    private Integer position;

    private Integer wordCount;

    private Long tokens;

    private String segmentHash;

    private Integer hitCount;

    private String status;

    private Date indexingTime;

    private String content;

    private String error;

    private Boolean disabled;
}
