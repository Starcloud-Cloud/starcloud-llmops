package com.starcloud.ops.business.app.controller.admin.chat.vo;


import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.domain.entity.params.JsonParamsEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author starcloud
 */
@Schema(description = "聊天请求")
@Data
public class ChatRequestVO extends AppContextReqVO {

//    @Schema(description = "聊天参数")
//    private Map<String,String> inputs;

    @Schema(description = "sse对象")
    private SseEmitter sseEmitter;

    @Schema(description = "聊天内容")
    @NotBlank(message = "聊天内容 不能为空")
    private String query;

    /**
     * jsonSchemas 格式的数据，后面会使用这种方式传递参数
     */
    private JsonParamsEntity jsonParams;

    @Schema(description = "游客的唯一标识")
    private String endUser;

    @Schema(description = "临时上传都索引文档")
    private List<String> docsUid;

}
