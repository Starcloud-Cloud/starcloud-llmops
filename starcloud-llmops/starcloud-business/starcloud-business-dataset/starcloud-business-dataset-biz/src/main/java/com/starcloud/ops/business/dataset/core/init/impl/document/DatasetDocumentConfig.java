package com.starcloud.ops.business.dataset.core.init.impl.document;

import com.starcloud.ops.business.dataset.core.DatasetConfig;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * DatasetConfig 实现类
 *
 * @author Alan Cusack
 */
@Data
public class DatasetDocumentConfig implements DatasetConfig {

    /**
     * 数据集类型
     */
    @NotBlank(message = "数据集类型不能为空", groups = {CommonConfig.class})
    private String datasetType;

    /**
     * 源数据存储 ID 集合
     */
    @NotBlank(message = "源数据存储 ID 集合", groups = {CommonConfig.class})
    private List<String> sourceDataIdList;

    /**
     * 分段规则
     */
    @NotNull(message = "分段规则不可以为空", groups = {CommonConfig.class})
    private SplitRule splitRule;

    /**
     * 文件地址
     */
    private Map<String, String> storageKeyMap;

    /**
     * 是否需要总结
     */
    public static Boolean NEED_SUMMARY = false;

    public interface CommonConfig {
    }

    @Override
    public Set<ConstraintViolation<DatasetConfig>> verifyParam(Validator validator) {
        return validator.validate(this, CommonConfig.class);
    }
}
