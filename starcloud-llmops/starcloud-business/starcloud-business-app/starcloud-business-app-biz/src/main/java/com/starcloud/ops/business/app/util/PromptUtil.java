package com.starcloud.ops.business.app.util;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.service.chat.momory.dto.MessageContentDocDTO;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一些公用的 Prompt 结构
 */
@Slf4j
public class PromptUtil {


    public static String parseDocContentLines(List<MessageContentDocDTO> blocks) {

        List<String> blockLines = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            MessageContentDocDTO contentDocDTO = blocks.get(i);
            contentDocDTO.setN(i + 1);
            blockLines.add(JsonUtils.toJsonString(contentDocDTO));
        }

        return StrUtil.join("\n", blockLines);
    }

    /**
     * 文档结构
     */
    @Data
    public static class PromptDocBlock implements Serializable {

        /**
         * 文档ID
         */
        private Long docId;

        /**
         * 文档块ID
         */
        private String blockId;

        /**
         * 分段序号
         */
        private Integer position;

        /**
         * 文档块内容
         */
        private String content;


        /**
         * 块数据来源的名称
         */
        @JsonIgnore
        private String sourceName;

        /**
         * 来源的地址
         */
        @JsonIgnore
        private String sourceUrl;


    }

}
