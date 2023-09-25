package com.starcloud.ops.business.app.domain.handler.common;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.service.chat.momory.MessageContentDocMemory;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.llm.langchain.core.utils.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础工具handler，满足LLM下 工具调用的字段和逻辑支持
 *
 * <p>
 * 你现在是一个prompt大师，帮我写一些关于 大语言模型调用工具的prompt。要根据给的工具描述和内容，提炼总结出给到 GPT 使用 的工具的prompt
 * <p>
 * <p>
 * 工具的描述，提炼为prompt：
 * <p>
 * 工具名称：文档内容搜索
 * 工具描述：当你需要从文档中搜索内容时，可以使用它，输入需要搜索的内容query 和 上下文中已经包含的所有文档id集合。   输出应该是一个json字符串，是一个数组，数组内是内容信息，它包含4个键:”docId”," blockId”," position”," content”。” docId”是文档ID，“blockId”是文档块内容，“position”是文档块所在文档中的位置，   “content”的值是文档的部分内容截取。
 */
@Slf4j
@Data
public abstract class BaseToolHandler<Q, R> extends BaseHandler<Q, R> {

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 工具描述
     */
    private String toolDescription;

    /**
     * 使用方法
     */
    private String toolInstructions;


    /**
     * 结果解释
     */
    private String interpretingResults;


    /**
     * 示例输入
     */
    private String exampleInput;


    /**
     * 示例输出
     */
    private String exampleOutput;


    /**
     * 注意
     */
    private String note;


    /**
     * 使用说明
     */
    private String usage;


    /**
     * 是否把执行的结果保存为上下文
     *
     * @return
     */
    @Deprecated
    public Boolean isAddHistory() {
        return true;
    }

    /**
     * 生成个handler 实例
     *
     * @param name
     * @return
     */
    public static BaseToolHandler of(String name) {

        try {
            //头部小写驼峰
            return SpringUtil.getBean(StrUtil.lowerFirst(name));
        } catch (Exception e) {
            log.error("BaseHandler of is fail: {}", name);
        }
        return null;
    }


    /**
     * 包装为 下午文 文档结构
     * 默认实现，工具类型返回
     */
    public List<MessageContentDocDTO> convertContentDoc(HandlerContext<Q> context, HandlerResponse<R> handlerResponse) {

        //解析返回的内容 生成 MessageContentDocDTO
        List<MessageContentDocDTO> messageContentDocDTOList = new ArrayList<>();

        MessageContentDocDTO messageContentDocDTO = new MessageContentDocDTO();

        messageContentDocDTO.setType(MessageContentDocDTO.MessageContentDocTypeEnum.TOOL.name());

        messageContentDocDTO.setTime(LocalDateTimeUtil.now().toString());
        messageContentDocDTO.setContent(handlerResponse.getAnswer());

        messageContentDocDTOList.add(messageContentDocDTO);

        return messageContentDocDTOList;
    }


    @Override
    public String getDescription() {

        String desc = "";

        if (StrUtil.isNotBlank(this.getToolDescription())) {
//            String all = "Tool Description: {}\n" +
//                    "Usage Instructions:\n{}\n" +
//                    "Interpreting Results:\n{}\n" +
//                    "Example Input:\n{}\n" +
//                    "Example Output:\n{}\n" +
//                    "Please Note:\n{}\n";
//            return StrUtil.format(all, this.getToolDescription(), getToolInstructions(), getInterpretingResults(), getExampleInput(), getExampleOutput(), getNote());

            String all = "Tool Description: {}\n" +
                    "Interpreting Results:\n{}\n" +
                    "Example Input:\n{}\n" +
                    "Example Output:\n{}\n";
//                    "Please Note:\n{}\n";
            return StrUtil.format(all, this.getToolDescription(), getInterpretingResults(), getExampleInput(), getExampleOutput(), getNote());
        }

        return desc;
    }


//    /**
//     * 工具类型的handler 的执行记录实现方法
//     *
//     * @param context
//     * @param handlerResponse
//     */
//    public void addRespHistory(HandlerContext<Q> context, HandlerResponse<R> handlerResponse) {
//
//        if (handlerResponse.getSuccess() && this.isAddHistory()) {
//
//            List<MessageContentDocDTO> messageContentDocDTO = this.convertContentDoc(context, handlerResponse);
//
//            List<MessageContentDocDTO> historys = Optional.ofNullable(messageContentDocDTO).orElse(new ArrayList<>()).stream().map(d -> {
//                //执行的 messageId拿不到
//                Map params = new HashMap();
//                params.put("tool", this.getName());
//                params.put("messageId", context.getMessageUid());
//
//                d.setExt(params);
//
//                d.setToolName(this.getName());
//
//                return d;
//            }).collect(Collectors.toList());
//
//            //增加工具使用结果历史
//            this.getMessageContentDocMemory().addHistory(historys);
//        }
//
//
//    }

}
