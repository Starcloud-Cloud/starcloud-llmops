package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.ParagraphDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.reference.ReferenceSchemeDTO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.poster.PosterStyleEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeGenerateModeEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.util.CostPointUtils;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@TaskComponent
public class ParagraphActionHandler extends BaseActionHandler {

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "ParagraphActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    /**
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_BEAN;
    }

    /**
     * 获取当前handler消耗的权益点数
     *
     * @return 权益点数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Integer getCostPoints() {
        String aiModel = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
        return CostPointUtils.obtainMagicBeanCostPoint(aiModel);
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @Override
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute() {

        log.info("段落内容生成 Action 执行开始......");
        StreamingSseCallBackHandler callBackHandler = new MySseCallBackHandler(this.getAppContext().getSseEmitter());
        OpenAIChatHandler handler = new OpenAIChatHandler(callBackHandler);

        Map<String, Object> params = this.getAppContext().getContextVariablesValues();
        log.info("段落内容生成 Action 执行中: 请求参数：\n{}", JSONUtil.parse(params).toStringPretty());
        // 获取到生成模式
        String mode = String.valueOf(params.get(CreativeConstants.GENERATE_MODE));
        // 需要生成的段落数量
        Integer paragraphCount = Integer.valueOf(String.valueOf(params.getOrDefault(CreativeConstants.PARAGRAPH_COUNT, "4")));


        // 随机模式
        if (CreativeSchemeGenerateModeEnum.RANDOM.name().equals(mode)) {
            // 获取到参考文案
            String refers = String.valueOf(params.get(CreativeConstants.REFERS));
            if (StrUtil.isBlank(refers)) {
                return ActionResponse.failure("", "310100019", "参考内容不能为空！", params);
            }
            List<ReferenceSchemeDTO> refersList = JSONUtil.toList(refers, ReferenceSchemeDTO.class);
            if (CollectionUtil.isEmpty(refersList)) {
                return ActionResponse.failure("", "310100019", "参考内容不能为空！", params);
            }
            if (paragraphCount < refersList.size()) {
                return ActionResponse.failure("", "310100019", "参考内容数量不能少于段落数！", params);
            }

            Map<Integer, ParagraphDTO> paragraphMap = new HashMap<>();
            for (int i = 0; i < paragraphCount; i++) {
                randomParagraph(refersList, paragraphMap);
            }
            List<ParagraphDTO> paragraphs = paragraphMap.values().stream().collect(Collectors.toList());
            ActionResponse actionResponse = new ActionResponse();
            actionResponse.setSuccess(Boolean.TRUE);
            actionResponse.setType(this.getClass().getSimpleName());
            actionResponse.setIsShow(Boolean.TRUE);
            actionResponse.setMessage("");
            actionResponse.setAnswer(JSONUtil.toJsonStr(paragraphs));
            actionResponse.setOutput(JsonData.of(paragraphs));
            actionResponse.setMessageTokens(Long.valueOf(actionResponse.getMessage().length()));
            actionResponse.setMessageUnitPrice(TokenCalculator.getUnitPrice(ModelTypeEnum.GPT_3_5_TURBO_16K, true));
            actionResponse.setAnswerTokens(Long.valueOf(actionResponse.getAnswer().length()));
            actionResponse.setAnswerUnitPrice(TokenCalculator.getUnitPrice(ModelTypeEnum.GPT_3_5_TURBO_16K, false));
            actionResponse.setTotalTokens(actionResponse.getMessageTokens() + actionResponse.getAnswerTokens());
            BigDecimal totalPrice = new BigDecimal(String.valueOf(actionResponse.getMessageTokens())).multiply(actionResponse.getMessageUnitPrice())
                    .add(new BigDecimal(String.valueOf(actionResponse.getAnswerTokens())).multiply(actionResponse.getAnswerUnitPrice()));
            actionResponse.setTotalPrice(totalPrice);
            actionResponse.setStepConfig(params);
            // 权益点数, 成功正常扣除, 失败不扣除
            actionResponse.setCostPoints(this.getCostPoints());
            log.info("段落内容生成 Action 执行成功: 生成模式: {}, : 结果：\n{}", mode, JSONUtil.parse(actionResponse).toStringPretty());
            return actionResponse;

        }

        // AI仿写模式
        if (CreativeSchemeGenerateModeEnum.AI_PARODY.name().equals(mode) || CreativeSchemeGenerateModeEnum.AI_CUSTOM.name().equals(mode)) {
            //获取前端传的完整字段（老结构）
            Long userId = this.getAppContext().getUserId();
            Long endUser = this.getAppContext().getEndUserId();
            String conversationId = this.getAppContext().getConversationUid();

            String model = Optional.ofNullable(this.getAiModel()).orElse(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
            Integer n = Optional.ofNullable(this.getAppContext().getN()).orElse(1);
            String prompt = String.valueOf(params.getOrDefault("PROMPT", "hi, what you name?"));
            Integer maxTokens = Integer.valueOf((String) params.getOrDefault("MAX_TOKENS", "1000"));
            Double temperature = Double.valueOf((String) params.getOrDefault("TEMPERATURE", "0.7"));

            // 构建请求
            OpenAIChatHandler.Request handlerRequest = new OpenAIChatHandler.Request();
            handlerRequest.setStream(Objects.nonNull(this.getAppContext().getSseEmitter()));
            handlerRequest.setModel(model);
            handlerRequest.setPrompt(prompt);
            handlerRequest.setMaxTokens(maxTokens);
            handlerRequest.setTemperature(temperature);
            handlerRequest.setN(n);

            // 构建请求
            HandlerContext handlerContext = HandlerContext.createContext(this.getAppUid(), conversationId, userId, endUser, this.getAppContext().getScene(), handlerRequest);
            // 执行步骤
            HandlerResponse<String> handlerResponse = handler.execute(handlerContext);
            String answer = handlerResponse.getAnswer();
            log.info(answer);
            if (StrUtil.isBlank(answer)) {
                return ActionResponse.failure("", "310100019", "生成段落内容为空！", params);
            }

            try {

                List<ParagraphDTO> paragraphs = JSONUtil.toList(answer, ParagraphDTO.class);
                if (CollectionUtil.isEmpty(paragraphs)) {
                    return ActionResponse.failure("", "310100019", "生成段落内容为空！", params);
                }
                if (paragraphs.size() != paragraphCount) {
                    return ActionResponse.failure("", "310100019", "生成的段落数量与要求的段落数量不一致！", params);
                }

                ActionResponse response = convert(handlerResponse);
                log.info("段落内容生成 Action 执行结束: 生成模式: {}, 响应结果：\n {}", mode, JSONUtil.parse(response).toStringPretty());
                return response;
            } catch (Exception e) {
                log.error("生成段落内容解析失败!: {}", e.getMessage(), e);
                return ActionResponse.failure("", "310100019", "生成段落内容解析失败！", params);
            }

        }

        // 不支持的生成模式
        return ActionResponse.failure("", "310100020", "不支持的生成模式: " + mode, params);
    }

    /**
     * 转换响应结果
     *
     * @param handlerResponse 响应结果
     * @return 转换后的响应结果
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    private ActionResponse convert(HandlerResponse<String> handlerResponse) {
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setSuccess(handlerResponse.getSuccess());
        actionResponse.setErrorCode(String.valueOf(handlerResponse.getErrorCode()));
        actionResponse.setErrorMsg(handlerResponse.getErrorMsg());
        actionResponse.setType(handlerResponse.getType());
        actionResponse.setIsShow(true);
        actionResponse.setMessage(handlerResponse.getMessage());


        this.writeLines(handlerResponse.getAnswer(), actionResponse);

        actionResponse.setMessageTokens(handlerResponse.getMessageTokens());
        actionResponse.setMessageUnitPrice(handlerResponse.getMessageUnitPrice());
        actionResponse.setAnswerTokens(handlerResponse.getAnswerTokens());
        actionResponse.setAnswerUnitPrice(handlerResponse.getAnswerUnitPrice());
        actionResponse.setTotalTokens(handlerResponse.getTotalTokens());
        actionResponse.setTotalPrice(handlerResponse.getTotalPrice());
        actionResponse.setStepConfig(handlerResponse.getStepConfig());
        // 权益点数, 成功正常扣除, 失败不扣除
        actionResponse.setCostPoints(handlerResponse.getSuccess() ? this.getCostPoints() : 0);
        return actionResponse;
    }

    /**
     * 递归实现，保证 paragraphList 是唯一的
     *
     * @param refersList   引用列表
     * @param paragraphMap 段落Map
     */
    private static void randomParagraph(List<ReferenceSchemeDTO> refersList, Map<Integer, ParagraphDTO> paragraphMap) {
        int index = RandomUtil.randomInt(refersList.size());

        // 如果已经存在, 说明重复, 重新生成
        if (paragraphMap.containsKey(index)) {
            randomParagraph(refersList, paragraphMap);
        }

        // 随机获取一个引用
        ReferenceSchemeDTO reference = refersList.get(index);
        ParagraphDTO paragraph = new ParagraphDTO();
        paragraph.setParagraphTitle(reference.getTitle());
        paragraph.setParagraphContent(reference.getContent());
        paragraph.setIsUseTitle(Boolean.FALSE);
        paragraph.setIsUseContent(Boolean.FALSE);
        paragraphMap.put(index, paragraph);
    }


    private static void writeLines(String str, ActionResponse actionResponse) {

        List<ParagraphDTO> paragraphDTOList = new ArrayList<>();

        if (StrUtil.isNotBlank(str)) {

            paragraphDTOList = JSONUtil.toList(str, ParagraphDTO.class);
            String answer = paragraphDTOList.stream().map(paragraphDTO -> {
                return paragraphDTO.getParagraphTitle() + "\r\n" + paragraphDTO.getParagraphContent();
            }).collect(Collectors.joining("\r\n\r\n"));

            actionResponse.setAnswer(answer);
            actionResponse.setOutput(JsonData.of(paragraphDTOList));
        }
    }

}
