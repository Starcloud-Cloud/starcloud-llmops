package cn.iocoder.yudao.module.system.api.tenant.dto;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 租户信息 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantInfoDTO {


    /**
     * 租户编号，自增
     */
    private Long id;
    /**
     * 租户名，唯一
     */
    private String name;
    /**
     * 联系人的用户编号
     *
     */
    private Long contactUserId;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系手机
     */
    private String contactMobile;
    /**
     * 租户状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 绑定域名
     */
    private String website;
    /**
     * 租户套餐编号
     *
     */
    private Long packageId;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 账号数量
     */
    private Integer accountCount;
}
