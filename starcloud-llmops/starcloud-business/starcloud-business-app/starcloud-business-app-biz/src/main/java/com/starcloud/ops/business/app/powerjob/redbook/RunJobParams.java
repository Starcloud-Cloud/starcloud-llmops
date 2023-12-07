package com.starcloud.ops.business.app.powerjob.redbook;

import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 小红书生成任务执行入口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-22
 */
@Data
public class RunJobParams {

    /**
     * 执行任务类型：
     * 文案生成
     * 图片生成
     */
    private String runType;

    /**
     * 批量执行一次的数据量
     */
    private Integer bathCount;

    /**
     * 子任务数量
     */
    private Integer subSize;

    /**
     * 是否只执行重试任务
     */
    private Boolean retryProcess;

    /**
     * 最大重试次数
     */
    private Integer maxRetry;

    /**
     * 校验参数
     */
    public void validate() {
        if (StringUtils.isBlank(runType)) {
            throw new IllegalArgumentException("任务执行：runType不能为空！");
        }
        if (!CreativeContentTypeEnum.contain(runType)) {
            throw new IllegalArgumentException("任务执行：不支持的创作内容类型，支持的有！" + Arrays.toString(CreativeContentTypeEnum.values()));
        }
        if (Objects.isNull(bathCount) || bathCount < 0) {
            throw new IllegalArgumentException("任务执行：bathCount不能为空或者小于0！");
        }
        if (Objects.isNull(subSize) || subSize < 0) {
            throw new IllegalArgumentException("任务执行：subSize不能为空或者小于0！");
        }
        if (Objects.isNull(retryProcess)) {
            throw new IllegalArgumentException("任务执行：retryProcess不能为空！");
        }
        if (Objects.isNull(maxRetry) || maxRetry < 0) {
            throw new IllegalArgumentException("任务执行：maxRetry不能空或者小于0！");
        }
    }
}
