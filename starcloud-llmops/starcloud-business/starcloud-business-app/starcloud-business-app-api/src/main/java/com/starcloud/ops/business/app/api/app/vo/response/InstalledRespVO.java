package com.starcloud.ops.business.app.api.app.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用是否已安装返回 VO 对象")
public class InstalledRespVO implements Serializable {

    private static final long serialVersionUID = -3014797048970967651L;

    /**
     * 是否已安装
     */
    @Schema(description = "安装状态：UNINSTALLED：未安装，INSTALLED：已安装，UPDATE: 需要更新")
    private String installStatus;

    /**
     * 旧的版本
     */
    @Schema(description = "旧的版本，需要更新时需要用到")
    private Integer oldVersion;

    /**
     * 新的版本
     */
    @Schema(description = "新的版本，需要更新时需要用到")
    private Integer newVersion;

    /**
     * 获取已安装的应用状态信息
     *
     * @param installStatus 安装状态
     * @param oldVersion    旧的版本
     * @param newVersion    新的版本
     * @return 已安装的应用状态信息
     */
    public static InstalledRespVO of(String installStatus, Integer oldVersion, Integer newVersion) {
        InstalledRespVO installedRespVO = new InstalledRespVO();
        installedRespVO.setInstallStatus(installStatus);
        installedRespVO.setOldVersion(oldVersion);
        installedRespVO.setNewVersion(newVersion);
        return installedRespVO;
    }

}
