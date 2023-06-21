package com.starcloud.ops.business.app.api.app.vo.request.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * 基础配置请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-16
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用请求 action 请求对象")
public class BaseConfigReqVO implements Serializable {

    private static final long serialVersionUID = -3805698859950168812L;
    
}
