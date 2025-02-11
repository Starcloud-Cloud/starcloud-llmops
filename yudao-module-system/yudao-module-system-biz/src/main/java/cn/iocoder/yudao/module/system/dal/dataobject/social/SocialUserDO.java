package cn.iocoder.yudao.module.system.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 社交（三方）用户
 *
 * @author weir
 */
@TableName(value = "system_social_user", autoResultMap = true)
@KeySequence("system_social_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserDO extends DeptBaseDO {

    /**
     * 自增主键
     */
    @TableId
    private Long id;
    /**
     * 社交平台的类型
     * <p>
     * 枚举 {@link SocialTypeEnum}
     */
    private Integer type;

    /**
     * 社交 openid
     */
    private String openid;
    /**
     * 社交 token
     */
    private String token;
    /**
     * 原始 Token 数据，一般是 JSON 格式
     */
    private String rawTokenInfo;

    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 原始用户数据，一般是 JSON 格式
     */
    private String rawUserInfo;

    /**
     * 最后一次的认证 code
     */
    private String code;
    /**
     * 最后一次的认证 state
     */
    private String state;

    /**
     * 访问令牌 过期时间
     */
    private Integer expireIn;
    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 刷新令牌 过期时间
     */
    private Integer refreshTokenExpireIn;

    /**
     * 绑定方式 自动/手动
     */
    private String remark;

    /**
     * 绑定方式 自动/手动
     */
    private Boolean auto;

}


