package cn.iocoder.yudao.framework.tenant.core.db;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DeptBaseDO extends TenantBaseDO {

    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.BIGINT)
    private Long deptId;
}
