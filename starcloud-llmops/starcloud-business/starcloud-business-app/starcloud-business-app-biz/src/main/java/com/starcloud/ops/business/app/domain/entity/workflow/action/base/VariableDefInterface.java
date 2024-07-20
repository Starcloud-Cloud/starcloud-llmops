package com.starcloud.ops.business.app.domain.entity.workflow.action.base;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;


public interface VariableDefInterface {


    /**
     * 节点参数都是固定的
     *
     * @return
     */
    JsonSchema inVariableJsonSchema();

    /**
     * 节点参数都是固定的
     *
     * @return
     */
    JsonSchema outVariableJsonSchema();


    /**
     * 返回 节点详细定义
     * @return
     */
    WorkflowStepRespVO defWorkflowStepResp();

}
