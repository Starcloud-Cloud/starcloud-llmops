package com.starcloud.ops.business.log.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("llm_log_embedding")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LogEmbeddingDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 索引内容
     */
    private String content;

    /**
     * 文档Id
     */
    private String documentId;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * 消耗token数
     */
    private Long tokens;

    /**
     * 内容hash
     */
    private String textHash;

    /**
     * 类型 文档、query
     */
    private String type;

}
