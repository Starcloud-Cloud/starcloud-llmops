package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Data
@ToString
public class SplitRule {
    /**
     * 分段大小
     */
    @Max(3000)
    @Min(100)
    private Integer chunkSize;

    /**
     * 分隔符
     */
    private List<String> separator;

}
