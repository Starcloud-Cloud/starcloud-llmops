package com.starcloud.ops.business.dataset.dal.dataobject.segment;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("llm_segments_embeddings")
@KeySequence("llm_segments_embeddings")
public class SegmentsEmbeddingsDO extends TenantBaseDO {

    private static final long serialVersionUID = 2522868772971100777L;

    private Long id;

    private String datasetId;

    private String documentId;

    private String segmentId;

    private String segmentHash;

    private Boolean status;

    private byte[] vector;

    private Long tokens;
}