package com.starcloud.ops.business.app.domain.entity.workflow;

import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;

/**
 * action 响应实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class ActionResponse {

    /**
     * 响应状态
     */
    private Boolean success;

    /**
     * 响应错误码
     */
    private String errorCode;

    /**
     * 响应错误码
     */
    private String errorMsg;

    /**
     * 相应类型
     */
    private String type;

    /**
     * 响应数据
     */
    private String style;

    /**
     * 是否显示
     */
    private Boolean isShow;

    /**
     * 是否只读
     */
    private Boolean readOnly;

    /**
     * 请求数据
     */
    private String message;

    /**
     * 响应数据
     */
    private String answer;

    /**
     * 返回数据
     */
    private JsonData output;

    /**
     * 结果是否发送一次sse
     */
    private Boolean isSendSseAll = false;

    /**
     * 请求 token 使用
     */
    private Long messageTokens = 0L;

    /**
     * 请求单价
     */
    private BigDecimal messageUnitPrice = BigDecimal.ZERO;

    /**
     * 响应 token 使用
     */
    private Long answerTokens = 0L;

    /**
     * 响应单价
     */
    private BigDecimal answerUnitPrice = BigDecimal.ZERO;

    /**
     * 总 token 数量
     */
    private Long totalTokens = 0L;

    /**
     * 总价格
     */
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 大模型
     */
    private String aiModel;

    /**
     * 花费魔法豆
     */
    private Integer costPoints = 0;

    /**
     * step 执行的参数
     */
    @Deprecated
    private Object stepVariables;

    /**
     * step 执行的参数
     */
    private Object stepConfig;

    public static ActionResponse failure(String errorCode, String errorMsg, Object stepConfig) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(Boolean.FALSE);
        actionResponse.setErrorCode(errorCode);
        actionResponse.setErrorMsg(errorMsg);
        actionResponse.setType(AppStepResponseTypeEnum.TEXT.name());
        actionResponse.setStyle(AppStepResponseStyleEnum.TEXTAREA.name());
        actionResponse.setIsShow(Boolean.TRUE);
        actionResponse.setMessage(" ");
        actionResponse.setStepConfig(stepConfig);
        actionResponse.setCostPoints(0);
        return actionResponse;
    }

}
