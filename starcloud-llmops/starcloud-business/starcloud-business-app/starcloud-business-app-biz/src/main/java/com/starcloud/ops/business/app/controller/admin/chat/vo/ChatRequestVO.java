package com.starcloud.ops.business.app.controller.admin.chat.vo;


import com.starcloud.ops.business.app.api.app.vo.request.AppContextReqVO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
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
    @Length(max = 200, message = "聊天内容不能超过200个字符")
    private String query;

    /**
     * @see com.starcloud.ops.business.app.domain.entity.chat.ModelProviderEnum
     */
    @Schema(description = "使用的大语言模型", defaultValue = "GPT35")
    private String modelType;

    @Schema(description = "联网聊天")
    private Boolean webSearch;

    /**
     * jsonSchemas 格式的数据，后面会使用这种方式传递参数
     */
    private JsonData params;

    /**
     * 临时上传的文档，默认上传到应用+会话对应的数据集中
     */
    @Schema(description = "上传后的文档ID")
    private List<String> docsUid;

    /**
     * 临时激活的插件信息，需要机器人本身配置了技能并开启情况下才生效
     */
    @Schema(description = "临时激活的插件")
    private List<ChatSkillVO> chatSkills;

}
