package com.starcloud.ops.business.job.biz.controller.admin.vo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.validation.ValidationUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.PluginDetailVO;
import com.starcloud.ops.business.job.biz.enums.BusinessJobTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.job.biz.enums.JobErrorCodeConstants.FIELD_VALID_ERROR;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "businessJobType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PluginDetailVO.class, name = "coze_standalone")
})
@Schema(description = "定时任务配置")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class JobConfigBaseVO {

    @Schema(description = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    @InEnum(value = BusinessJobTypeEnum.class, field = InEnum.EnumField.CODE, message = "定时类型[{value}]必须是: {values}")
    private String businessJobType;

    /**
     * 字段校验
     */
    public void valid() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<JobConfigBaseVO>> validate = factory.getValidator().validate(this);
        if (CollectionUtil.isEmpty(validate)) {
            return;
        }
        throw exception(FIELD_VALID_ERROR, validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(",")));
    }

//    public abstract void valid();
}
