package com.starcloud.ops.business.app.model.plan;

import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentCreateReqVO;
import lombok.Data;

import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ContentBatchRequest implements java.io.Serializable {

    private static final long serialVersionUID = -5656434067863140239L;

    /**
     * 内容请求列表
     */
    private List<CreativeContentCreateReqVO> contentRequestList;

    /**
     * 消息
     */
    private String message;
}
