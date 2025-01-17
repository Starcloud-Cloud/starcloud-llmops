package com.starcloud.ops.business.app.model.content;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.domain.entity.AppMarketEntity;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.model.content.resource.CreativeContentResourceConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作内容执行参数")
public class CreativeContentExecuteParam implements java.io.Serializable {

    private static final long serialVersionUID = -5322081881706423889L;

    /**
     * 应用信息
     */
    @Schema(description = "应用信息")
    private AppMarketRespVO appInformation;

    /**
     * 执行扩展参数
     */
    @Schema(description = "执行扩展参数")
    private CreativeContentExecuteExtend extend;

    /**
     * 视频快捷配置
     */
    @Schema(description = "视频快捷配置")
    private String quickConfiguration;

    /**
     * 资源配置
     */
    @Schema(description = "资源配置")
    private CreativeContentResourceConfiguration resourceConfiguration;

    public void validate(ValidateTypeEnum validateType) {
        AppValidate.notNull(appInformation, "执行参数不能为空！");
        AppMarketEntity entity = AppMarketConvert.INSTANCE.convertEntity(appInformation);
        List<Verification> validates = entity.validate(null, validateType);
        if (CollectionUtil.isNotEmpty(validates)) {
            throw new IllegalArgumentException(validates.get(0).getMessage());
        }
        appInformation = AppMarketConvert.INSTANCE.convertResponse(entity);
    }
}
