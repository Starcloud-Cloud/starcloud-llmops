package com.starcloud.ops.business.user.permission;

import com.google.common.collect.Sets;

import java.util.Set;

public class DeptPermissionTable {


    /**
     * 继承 {@link cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO}
     * <p>
     * <p>
     * ALTER TABLE table_name ADD COLUMN dept_id BIGINT AFTER tenant_id;
     * ALTER TABLE table_name ADD INDEX idx_dept (dept_id) USING BTREE;
     * <p>
     * <p>
     * UPDATE
     * table_name a
     * INNER JOIN system_users b ON a.creator = b.id
     * SET a.dept_id = b.dept_id
     * WHERE
     */


    public static Set<String> getTableNames() {
        return Sets.newHashSet(

                "llm_material_library",
                "llm_material_library_slice",
                "llm_material_library_table_column",
                "llm_material_library_app_bind",
                "llm_single_mission",
                "llm_notification",
                "system_social_user",
                "system_social_user_bind",
                "llm_material_plugin_definition",
                "llm_material_plugin_config",
                "llm_business_job",
                "llm_job_log",

                "llm_app",
                "llm_creative_plan",
                "llm_creative_scheme",
                "llm_creative_plan_batch",
                "llm_creative_content",
                "poster_material_group",
                "poster_material"
        );
    }
}
