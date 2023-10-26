package com.starcloud.ops.business.app.domain.entity.skill;


import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * handler 技能包装类
 */
@Slf4j
@Data
public class HandlerSkill extends BaseSkillEntity {

    private SkillTypeEnum type = SkillTypeEnum.HANDLER;

    @JsonIgnore
    @JSONField(serialize = false)
    private BaseToolHandler handler;


    public HandlerSkill(BaseToolHandler baseHandler) {
        this.handler = baseHandler;
    }

    public String getSkillName() {
        return super.getName();
    }

    @Override
    public String getName() {
        return this.handler.getName();
    }

    @Override
    public String getDesc() {
        return this.handler.getDescription();
    }

    @Override
    public String getUserName() {
        return this.handler.getUserName();
    }

    @Override
    public String getUserDesc() {
        return this.handler.getUserDescription();
    }


    /**
     * 根据 handler name 初始化
     *
     * @param name
     * @return
     */
    public static HandlerSkill of(String name) {
        return new HandlerSkill(BaseToolHandler.of(name));
    }

    @Override
    public JsonNode getInputSchemas() {

        return null;
    }


    @Override
    public Class<?> getInputCls() {
        return null;
    }


    @Override
    public FunTool createFunTool(HandlerContext handlerContext) {

        Type query = TypeUtil.getTypeArgument(this.getHandler().getClass());
        Class<?> cc = (Class<?>) query;

        Function<Object, ToolResponse> function = (input) -> {

            log.info("FunTool HandlerSkill: {} {}", this.getHandler().getName(), input);

            //转换入参
            handlerContext.setRequest(input);

            //获取当前 应用下 配置的 技能交互信息
            SkillCustomConfig skillCustomConfig = this.getSkillSettingInfo(handlerContext.getAppUid(), this.handler.getName());

            skillCustomConfig.getName();
            skillCustomConfig.getDescription();
            skillCustomConfig.getShowType();

            //不会抛出异常
            HandlerResponse handlerResponse = this.execute(handlerContext);

            log.info("FunTool HandlerSkill: {} response:\n{}", this.getHandler().getName(), JSONUtil.toJsonPrettyStr(handlerResponse));

            ToolResponse toolResponse =ToolResponse.buildResponse(handlerResponse);
            toolResponse.setObservation(handlerResponse.getOutput());

            return toolResponse;

        };

        return createSkillFunTool(handler.getName(), handler.getDescription(), cc, function);
    }


    /**
     * 直接执行ToolHandler
     * 1，会存储上下文文档
     *
     * @param context
     * @return
     */
    public HandlerResponse execute(HandlerContext context) {

        //不会抛出异常
        HandlerResponse handlerResponse = this.handler.execute(context);

        //放在这里是因为暂时只有 聊天技能调用 才做记录
        this.addRespHistory(context, handlerResponse);

        //@todo 这里可增加 扣权益记录

        //@todo 考虑是否可以判断抛出 FailToolExecution


        return handlerResponse;
    }


    /**
     * 技能执行后增加到上下文
     * 1，因为现在执行技能必然会开启4.0, 而且执行到结果会正常保存在history中，不需要刻意在保存到上下文中，所以技能执行基本不需要在把结果保存到上下文中
     *
     * @param context
     * @param handlerResponse
     */
    public void addRespHistory(HandlerContext context, HandlerResponse handlerResponse) {

        if (handlerResponse.getSuccess() && this.getAddHistory()) {

            List<MessageContentDocDTO> messageContentDocDTO = this.getHandler().convertContentDoc(context, handlerResponse);

            List<MessageContentDocDTO> historys = Optional.ofNullable(messageContentDocDTO).orElse(new ArrayList<>()).stream().map(d -> {
                //执行的 messageId拿不到
                Map params = new HashMap();
                params.put("tool", this.getName());
                params.put("messageId", context.getMessageUid());

                d.setExt(params);

                d.setToolName(this.getName());

                return d;
            }).collect(Collectors.toList());


            if (this.getAddHistory() && !this.getSaveHistory()) {
                //增加工具使用结果历史
                this.getMessageContentDocMemory().addHistory(historys);
            } else if (this.getSaveHistory()) {
                this.getMessageContentDocMemory().saveHistory(historys);
            }

        }

    }


    /**
     * 获取在App 技能上设置的 每个应用的独立配置信息
     *
     * @return
     * @todo 读配置表
     */
    protected SkillCustomConfig getSkillSettingInfo(String appUid, String handlerName) {

        //当前应用下配置的 其他应用的技能配置

        return new SkillCustomConfig();
    }


}
