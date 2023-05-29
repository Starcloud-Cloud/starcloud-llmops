package com.starcloud.ops.business.app.dal.databoject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 参数配置表
 *
 * @author 芋道源码
 */
@TableName("infra_config")
@KeySequence("infra_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LlmBusinessAppDO extends BaseDO {

    /**
     * 参数主键
     */
    @TableId
    private Long id;
    /**
     * 参数分类
     */
    /**
     * 参数名称
     */
    private String name;

}
