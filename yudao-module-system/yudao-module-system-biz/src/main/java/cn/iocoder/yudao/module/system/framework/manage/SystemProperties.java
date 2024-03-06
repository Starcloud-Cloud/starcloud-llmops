package cn.iocoder.yudao.module.system.framework.manage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "yudao.system-config")
@Validated
@Data
public class SystemProperties {

    /**
     * 环境名称
     */
    @NotNull(message = "环境名称")
    private String envName;
}
