package com.starcloud.ops.business.app.dal.databoject.xhs.batch;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.enums.xhs.batch.CreativePlanBatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 创作计划批次
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "llm_creative_plan_batch1", autoResultMap = true)
@KeySequence("llm_creative_plan_batch_seq1")
public class CreativePlanBatchDO extends TenantBaseDO {

    private static final long serialVersionUID = -3967506144564738057L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 执行批次UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 创作计划uid
     */
    @TableField("plan_uid")
    private String planUid;

    /**
     * 应用UID
     */
    @TableField("app_uid")
    private String appUid;

    /**
     * 应用版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 应用信息
     */
    @TableField("app_info")
    private String appInfo;

    /**
     * 素材
     */
    @TableField("material")
    private String material;

    /**
     * 创作计划标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 生成数量
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 失败数量
     */
    @TableField("failure_count")
    private Integer failureCount;

    /**
     * 成功数量
     */
    @TableField("success_count")
    private Integer successCount;

    /**
     * 开始执行时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行耗时
     */
    private Long elapsed;

    /**
     * 创作计划执行状态：待执行，执行中，暂停，执行完成
     */
    @TableField("status")
    private String status;


    public static class CreativePlanTypeHandler extends AbstractJsonTypeHandler<CreativePlanRespVO> {

        @Override
        protected CreativePlanRespVO parse(String json) {
            return JsonUtils.parseObject(json, CreativePlanRespVO.class);
        }

        @Override
        protected String toJson(CreativePlanRespVO obj) {
            return JsonUtils.toJsonString(obj);
        }
    }

    public static class CreativeSchemeTypeHandler extends AbstractJsonTypeHandler<CreativeSchemeRespVO> {
        @Override
        protected CreativeSchemeRespVO parse(String json) {
            return JsonUtils.parseObject(json, CreativeSchemeRespVO.class);
        }

        @Override
        protected String toJson(CreativeSchemeRespVO obj) {
            return JsonUtils.toJsonString(obj);
        }
    }


}
