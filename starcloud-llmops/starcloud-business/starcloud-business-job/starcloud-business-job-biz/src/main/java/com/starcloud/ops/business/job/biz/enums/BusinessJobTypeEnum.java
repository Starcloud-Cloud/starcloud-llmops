package com.starcloud.ops.business.job.biz.enums;

import com.starcloud.ops.business.app.enums.plugin.PluginSceneEnum;
import com.starcloud.ops.business.job.biz.processor.CozeStandaloneProcessor;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;
import tech.powerjob.common.enums.ExecuteType;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务的业务类型
 */
@Getter
public enum BusinessJobTypeEnum implements IEnumable<String> {

    coze_standalone(CozeStandaloneProcessor.class, "coze素材库单机任务", ExecuteType.STANDALONE);


    /**
     * 处理器全限定类名
     */
    private Class<? extends BasicProcessor> reference;

    /**
     * 描述
     */
    private String desc;

    /**
     * 任务执行类型
     */
    private ExecuteType executeType;


    BusinessJobTypeEnum(Class<? extends BasicProcessor> reference, String desc, ExecuteType executeType) {
        this.reference = reference;
        this.desc = desc;
        this.executeType = executeType;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(BusinessJobTypeEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getLabel() {
        return desc;
    }
}
