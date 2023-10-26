package com.starcloud.ops.business.app.service.chat.momory.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 另扩展出来的Memory，存储对话中所有上传的文档和工具执行的结果的历史记录
 * 统一包装为文档，最后生成为 上下文prompt，方便处理对话中的历史问题
 */
@Slf4j
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MessageContentDocDTO {


    private Integer n;

    /**
     * 对应数据集中的文档ID
     */
    private Long id;

    /**
     * 文档类型
     * 网页
     * 文件
     * 工具
     *
     * @see MessageContentDocTypeEnum
     */
    private String type;

    /**
     * 工具名称
     */
    private String toolName;



    /**
     * 标题
     */
    private String title;


    /**
     * 网页｜文件地址
     */
    private String url;

    /**
     * 内容
     */
    private String content;

    /**
     * 总结
     */
    private String summary;


    /**
     * 字数
     */
    private Long words;


    /**
     * 数据时间
     */
    private String time;


    /**
     * 扩展信息
     * 保存到DB
     */
    @JsonIgnore
    private Object ext;


    public static enum MessageContentDocTypeEnum {

        //互联网
        WEB,

        //文件
        FILE,

        //工具
        TOOL

    }

}
