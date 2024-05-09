package com.starcloud.ops.business.app.api.xhs.scheme.vo.request;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创作方案应用节点 出入参数 下拉数据
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CreativeAppStepSchemeReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 33863755137653429L;


    @NotBlank(message = "应用市场ID不能为空")
    @Schema(description = "应用市场ID")
    private String appUid;


    @NotBlank(message = "应用节点ID不能为空")
    @Schema(description = "应用节点ID")
    private String stepCode;


    /**
     * 逻辑上应该前端的所以改动都要请求下接口重新进行计算
     */
    private AppReqVO appReqVO;


}
