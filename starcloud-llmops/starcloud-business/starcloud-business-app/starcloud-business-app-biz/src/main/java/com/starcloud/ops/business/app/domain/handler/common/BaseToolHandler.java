package com.starcloud.ops.business.app.domain.handler.common;


import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
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


    @Override
    public String getDescription() {

        String desc = "";

        if (StrUtil.isNotBlank(this.toolDescription)) {
            String all = "Tool Description:{}\n" +
                    "Usage Instructions:\n{}\n" +
                    "Interpreting Results:\n{}\n" +
                    "Example Input:\n{}\n" +
                    "Example Output:\n{}\n" +
                    "Please Note:\n{}\n";
            return StrUtil.format(all, toolDescription, toolInstructions, interpretingResults, exampleInput, exampleOutput, note);
        }

        return desc;
    }

}
