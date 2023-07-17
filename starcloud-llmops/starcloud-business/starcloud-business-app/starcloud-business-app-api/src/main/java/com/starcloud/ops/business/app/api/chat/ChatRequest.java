package com.starcloud.ops.business.app.api.chat;


import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author starcloud
 */
@Schema(description = "聊天请求")
@Data
public class ChatRequest extends AppContextReqVO {

//    @Schema(description = "聊天参数")
//    private Map<String,String> inputs;

    @Schema(description = "聊天内容")
    @NotBlank(message = "聊天内容 不能为空")
    private String query;


    @Schema(description = "临时上传都索引文档")
    private List<String> docs;

}
