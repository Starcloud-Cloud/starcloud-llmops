package com.starcloud.ops.business.app.dal.databoject.xhs.batch;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.enums.xhs.batch.CreativePlanBatchStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "llm_creative_plan_batch", autoResultMap = true)
public class CreativePlanBatchDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 执行批次号 时间戳
     */
    private Long batch;

    /**
     * 创作计划uid
     */
    private String planUid;

    /**
     * 执行计划json
     */
    @TableField(typeHandler = CreativePlanTypeHandler.class)
    private CreativePlanRespVO creativePlan;

    /**
     * 创作方案json
     */
    @TableField(typeHandler = CreativeSchemeTypeHandler.class)
    private List<CreativeSchemeRespVO> schemeConfig;

    /**
     * 批次状态，执行中，执行结束 {@link CreativePlanBatchStatusEnum}
     */
    private String status;

    /**
     * 开始执行时间
     */
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
     * 失败数
     */
    private Integer failureCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 总数
     */
    private Integer totalCount;


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

    public static class CreativeSchemeTypeHandler extends AbstractJsonTypeHandler<List<CreativeSchemeRespVO>> {
        @Override
        protected List<CreativeSchemeRespVO> parse(String json) {
            return JsonUtils.parseArray(json, CreativeSchemeRespVO.class);
        }

        @Override
        protected String toJson(List<CreativeSchemeRespVO> obj) {
            return JsonUtils.toJsonString(obj);
        }
    }


}
