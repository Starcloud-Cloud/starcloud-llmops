package com.starcloud.ops.business.product.api.comment;

import com.starcloud.ops.business.product.api.comment.dto.ProductCommentCreateReqDTO;

/**
 * 产品评论 API 接口
 *
 * @author HUIHUI
 */
public interface ProductCommentApi {

    /**
     * 创建评论
     *
     * @param createReqDTO 评论参数
     * @return 返回评论创建后的 id
     */
    Long createComment(ProductCommentCreateReqDTO createReqDTO);

}
