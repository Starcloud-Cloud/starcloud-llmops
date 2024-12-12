package com.starcloud.ops.business.app.service.plugins.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.enums.plugin.OutputTypeEnum;
import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.business.app.exception.plugins.CozeErrorCode;
import com.starcloud.ops.business.app.feign.CozePublicClient;
import com.starcloud.ops.business.app.feign.cozev2.WorkflowDataAsynResult;
import com.starcloud.ops.business.app.feign.cozev2.WorkflowRunResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeWorkflowRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import com.starcloud.ops.business.app.service.plugins.impl.PluginsDefinitionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.COZE_ERROR;

@Slf4j
@Service
public class CozeWorkflowExecuteHandler extends PluginExecuteHandler {

    @Resource
    private PluginsDefinitionServiceImpl pluginsDefinitionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private CozePublicClient cozePublicClient;

//    private static final ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 4, Runtime.getRuntime().availableProcessors() * 8, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(16), new RejectedExecutionHandler() {
//        @Override
//        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
//            throw exception(EXECUTE_POOL_FULL);
//        }
//    });

    @Override
    String supportPlatform() {
        return PlatformEnum.coze_workflow.getCode();
    }

    @Override
    public String verify(PluginTestReqVO reqVO) {
        String accessToken = pluginsDefinitionService.bearer(reqVO.getAccessTokenId());
        String content = reqVO.getContent();
        if (StringUtils.isNotBlank(content) && !JSONUtil.isTypeJSON(content)) {
            throw exception(COZE_ERROR, "参数必须是json格式");
        }
        CozeWorkflowRequest request = new CozeWorkflowRequest();
        request.setWorkflowId(reqVO.getEntityUid());

        if (StringUtils.isNotBlank(content)) {
            request.setParameters(JSONUtil.parseObj(content));
        }
        request.setIsAsync(true);
        String code = IdUtil.fastSimpleUUID();

        WorkflowRunResult workflowRunResult = new WorkflowRunResult(code, accessToken, request.getWorkflowId());
        try {
            long start = System.currentTimeMillis();
            log.info("verify start {} ", JSONUtil.toJsonPrettyStr(workflowRunResult));
            CozeResponse<String> workflowResp = cozePublicClient.runWorkflow(request, accessToken);
            long end = System.currentTimeMillis();
            if (workflowResp.getCode() != 0) {
                throw exception(COZE_ERROR, workflowResp.getMsg());
            }

            workflowRunResult.setExecuteId(workflowResp.getExecuteId());
            redisTemplate.boundValueOps(PREFIX_EXECTUE + code).set(JSONUtil.toJsonStr(workflowRunResult), 30, TimeUnit.MINUTES);
            redisTemplate.boundValueOps(VERIFY_PARAMS + code).set(content, 30, TimeUnit.MINUTES);
            log.info("verify success, {} ms, {}", end - start, JSONUtil.toJsonPrettyStr(workflowRunResult));
        } catch (Exception e) {
            log.warn("verify error, {}", JSONUtil.toJsonPrettyStr(workflowRunResult), e);
            throw exception(COZE_ERROR, e.getMessage());
        }
        return code;
    }

    @Override
    public VerifyResult verifyResult(PluginTestResultReqVO resultReqVO) {
        String workflowRunResult = redisTemplate.boundValueOps(PREFIX_EXECTUE + resultReqVO.getCode()).get();
        VerifyResult verifyResult = new VerifyResult();
        if (StringUtils.isBlank(workflowRunResult)) {
            verifyResult.setStatus("in_progress");
            return verifyResult;
        }
        WorkflowRunResult runResult = JSONUtil.toBean(workflowRunResult, WorkflowRunResult.class);
        CozeResponse<List<WorkflowDataAsynResult>> workflowResp = cozePublicClient.runHistories(runResult.getWorkflowId(), runResult.getExecuteId(), runResult.getAccessToken());

        if (workflowResp.getCode() != 0) {
            throw exception(COZE_ERROR, workflowResp.getMsg());
        }
        if (CollectionUtil.isEmpty(workflowResp.getData())) {
            verifyResult.setStatus("in_progress");
            return verifyResult;
        }
        WorkflowDataAsynResult workflowDataAsynResult = workflowResp.getData().get(0);

        if (Objects.equals("Running", workflowDataAsynResult.getExecuteStatus())) {
            verifyResult.setStatus("in_progress");
            return verifyResult;
        } else if (Objects.equals("Fail", workflowDataAsynResult.getExecuteStatus())) {
            log.warn("result response: {}", JSONUtil.toJsonPrettyStr(workflowResp));
            throw exception(COZE_ERROR, workflowDataAsynResult.getErrorMessage());
        }

        String params = redisTemplate.boundValueOps(VERIFY_PARAMS + resultReqVO.getCode()).get();
        if (StringUtils.isNotBlank(params)) {
            verifyResult.setArguments(JSONUtil.parseObj(params));
        }

        String content = parseContent(workflowResp);

        if (JSONUtil.isTypeJSONArray(content)) {
            Type listType = new TypeReference<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> listMap = JSON.parseObject(content, listType);
            listMap.forEach(this::cleanMap);
            verifyResult.setOutput(listMap);
            verifyResult.setOutputType(OutputTypeEnum.list.getCode());
        } else if (JSONUtil.isTypeJSONObject(content)) {
            Type mapType = new TypeReference<Map<String, Object>>() {
            }.getType();
            Map<String, Object> objectMap = JSON.parseObject(content, mapType);
            cleanMap(objectMap);
            verifyResult.setOutput(objectMap);
            verifyResult.setOutputType(OutputTypeEnum.obj.getCode());
        } else {
            log.error("输出结果格式错误 {}", content);
            //处理一些场景的错误，并返回
            throw exception(new CozeErrorCode(content));
        }

        verifyResult.setStatus("completed");
        verifyResult.setVerifyState(true);
        return verifyResult;
    }

    @Override
    public String executePlugin(PluginExecuteReqVO executeReqVO) {
        PluginRespVO reqVO = pluginsDefinitionService.detail(executeReqVO.getUuid());
        String accessToken = pluginsDefinitionService.bearer(reqVO.getCozeTokenId());
        CozeWorkflowRequest request = new CozeWorkflowRequest();
        request.setWorkflowId(reqVO.getEntityUid());
        request.setParameters(executeReqVO.getInputParams());
        String code = IdUtil.fastSimpleUUID();
        request.setIsAsync(true);
        WorkflowRunResult workflowRunResult = new WorkflowRunResult(code, accessToken, request.getWorkflowId());
        try {
            log.info("execute start {}", JSONUtil.toJsonPrettyStr(workflowRunResult));
            final long start = System.currentTimeMillis();
            CozeResponse<String> workflowResp = cozePublicClient.runWorkflow(request, accessToken);
            long end = System.currentTimeMillis();
            if (workflowResp.getCode() != 0) {
                throw exception(COZE_ERROR, workflowResp.getMsg());
            }

            workflowRunResult.setExecuteId(workflowResp.getExecuteId());
            redisTemplate.boundValueOps(PREFIX_EXECTUE + code).set(JSONUtil.toJsonStr(workflowRunResult), 30, TimeUnit.MINUTES);
            pluginsDefinitionService.updateTime(end - start, reqVO.getUid());
            log.info("execute success, {} ms, {}", end - start, JSONUtil.toJsonPrettyStr(workflowRunResult));
        } catch (Exception e) {
            log.warn("execute error, {}", JSONUtil.toJsonPrettyStr(workflowRunResult), e);
            throw exception(COZE_ERROR, e.getMessage());
        }
        return code;
    }

    @Override
    public PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO) {
        String workflowRunResult = redisTemplate.boundValueOps(PREFIX_EXECTUE + pluginResultReqVO.getCode()).get();
        PluginExecuteRespVO executeRespVO = new PluginExecuteRespVO();
        if (StringUtils.isBlank(workflowRunResult)) {
            executeRespVO.setStatus("in_progress");
            return executeRespVO;
        }

        WorkflowRunResult runResult = JSONUtil.toBean(workflowRunResult, WorkflowRunResult.class);
        CozeResponse<List<WorkflowDataAsynResult>> workflowResp = cozePublicClient.runHistories(runResult.getWorkflowId(), runResult.getExecuteId(), runResult.getAccessToken());

        if (workflowResp.getCode() != 0) {
            throw exception(COZE_ERROR, workflowResp.getMsg());
        }
        if (CollectionUtil.isEmpty(workflowResp.getData())) {
            executeRespVO.setStatus("in_progress");
            return executeRespVO;
        }
        WorkflowDataAsynResult workflowDataAsynResult = workflowResp.getData().get(0);

        if (Objects.equals("Running", workflowDataAsynResult.getExecuteStatus())) {
            executeRespVO.setStatus("in_progress");
            return executeRespVO;
        } else if (Objects.equals("Fail", workflowDataAsynResult.getExecuteStatus())) {
            log.warn("result response: {}", JSONUtil.toJsonPrettyStr(workflowResp));
            throw exception(COZE_ERROR, workflowDataAsynResult.getErrorMessage());
        }

        String content = parseContent(workflowResp);

        if (JSONUtil.isTypeJSONArray(content)) {
            Type listType = new TypeReference<List<Map<String, Object>>>() {
            }.getType();
            List<Map<String, Object>> listMap = JSON.parseObject(content, listType);
            listMap.forEach(this::cleanMap);
            executeRespVO.setOutput(listMap);
        } else if (JSONUtil.isTypeJSONObject(content)) {
            Type mapType = new TypeReference<Map<String, Object>>() {
            }.getType();
            Map<String, Object> objectMap = JSON.parseObject(content, mapType);
            cleanMap(objectMap);
            executeRespVO.setOutput(objectMap);
        } else {
            log.error("输出结果格式错误 {}", content);
            //处理一些场景的错误，并返回
            throw exception(new CozeErrorCode(content));
        }
        executeRespVO.setStatus("completed");
        return executeRespVO;
    }

    private String parseContent(CozeResponse<List<WorkflowDataAsynResult>> workflowResp) {
        log.info("result response: {}", JSONUtil.toJsonPrettyStr(workflowResp));
        WorkflowDataAsynResult workflowDataAsynResult = workflowResp.getData().get(0);
        String output = workflowDataAsynResult.getOutput();
        JSONObject jsonObject = JSONObject.parseObject(output);
        if (!jsonObject.containsKey("Output")) {
            throw exception(COZE_ERROR, "返回结果为空");
        }
        String content = jsonObject.getJSONObject("Output").getString("data");
        if (StringUtils.isBlank(content)) {
            content = jsonObject.getJSONObject("Output").getString("output");
        }

        if (StringUtils.isBlank(content)) {
            throw exception(COZE_ERROR, "返回结果为空");
        }
        return content;
    }
}
