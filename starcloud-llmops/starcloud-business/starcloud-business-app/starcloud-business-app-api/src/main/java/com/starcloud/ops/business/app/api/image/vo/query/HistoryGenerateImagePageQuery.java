package com.starcloud.ops.business.app.api.image.vo.query;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-17
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "HistoryGenerateImagePageQuery", description = "历史生成图片分页查询参数")
public class HistoryGenerateImagePageQuery extends PageQuery {

    private static final long serialVersionUID = -7294418406714001254L;

}
