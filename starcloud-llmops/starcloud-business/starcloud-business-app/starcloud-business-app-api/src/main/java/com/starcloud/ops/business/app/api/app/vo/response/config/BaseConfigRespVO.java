package com.starcloud.ops.business.app.api.app.vo.response.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 基础配置请求对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-16
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用响应配置对象基础VO")
public class BaseConfigRespVO implements Serializable {

    private static final long serialVersionUID = -2219575865854972680L;

}
