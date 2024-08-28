package com.starcloud.ops.business.app.dal.databoject.plugin;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginSceneEnum;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@Data
@ToString(callSuper = true)
@TableName("llm_material_plugin_definition")
public class PluginDefinitionDO extends TenantBaseDO {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uid
     */
    private String uid;

    /**
     * 插件名称
     */
    private String pluginName;

    /**
     * 图片
     */
    private String avatar;

    /**
     * 场景 {@link PluginSceneEnum}
     */
    private String scene;

    /**
     * 输入参考内容
     */
    @TableField(value = "`input`")
    private String input;

    /**
     * 输入结构
     */
    private String inputFormart;

    /**
     * 输出参考内容
     */
    @TableField(value = "`output`")
    private String output;

    /**
     * 输出结构
     */
    private String outputFormart;

    /**
     * 实现类型 {@link PlatformEnum}
     */
    private String type;

    /**
     * coze访问令牌id
     */
    private String cozeTokenId;

    /**
     * coze空间id
     */
    private String spaceId;

    /**
     * botId / appMarketUid
     */
    private String entityUid;

    /**
     * 机器人名称 或 应用名称
     */
    private String entityName;

    /**
     * 插件是否发布
     */
    private Boolean published;

    /**
     * 描述
     */
    private String description;

    /**
     * 验证状态
     */
    private Boolean verifyState;

    /**
     * 输出类型
     */
    private String outputType;

    /**
     * 总时间
     */
    private Long totalTime;

    /**
     * 执行次数
     */
    private Integer count;

    /**
     * 平均执行时间
     */
    private Long executeTimeAvg;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginDefinitionDO that = (PluginDefinitionDO) o;
        return Objects.equals(uid, that.uid) && Objects.equals(pluginName, that.pluginName) && Objects.equals(scene, that.scene) && Objects.equals(type, that.type) && Objects.equals(cozeTokenId, that.cozeTokenId) && Objects.equals(entityUid, that.entityUid) && Objects.equals(entityName, that.entityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uid, pluginName, scene, type, cozeTokenId, entityUid, entityName);
    }
}
