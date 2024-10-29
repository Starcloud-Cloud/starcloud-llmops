package com.starcloud.ops.business.app.service.plugins.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginResultReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.business.app.exception.plugins.CozeErrorCode;
import com.starcloud.ops.business.app.feign.CozePublicClient;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.WorkflowResult;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.COZE_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.EXECUTE_POOL_FULL;

@Slf4j
@Service
public class CozeWorkflowExecuteHandler extends PluginExecuteHandler {

    @Resource
    private PluginsDefinitionServiceImpl pluginsDefinitionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private CozePublicClient cozePublicClient;

    private static final ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 4, Runtime.getRuntime().availableProcessors() * 8, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(16), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            throw exception(EXECUTE_POOL_FULL);
        }
    });

    @Override
    String supportPlatform() {
        return PlatformEnum.coze_workflow.getCode();
    }

    @Override
    public String executePlugin(PluginExecuteReqVO executeReqVO) {
        PluginRespVO reqVO = pluginsDefinitionService.detail(executeReqVO.getUuid());
        String accessToken = pluginsDefinitionService.bearer(reqVO.getCozeTokenId());
        final long start = System.currentTimeMillis();
        CozeWorkflowRequest request = new CozeWorkflowRequest();
        request.setWorkflowId(reqVO.getEntityUid());
        request.setParameters(executeReqVO.getInputParams());
        String code = IdUtil.fastSimpleUUID();
        POOL_EXECUTOR.execute(() -> {
            try {
                CozeResponse<String> workflowResp = cozePublicClient.runWorkflow(request, accessToken);
                long end = System.currentTimeMillis();
                redisTemplate.boundValueOps(prefix_exectue + code).set(JSONUtil.toJsonStr(workflowResp), 30, TimeUnit.MINUTES);
                pluginsDefinitionService.updateTime(end - start, reqVO.getUid());
            } catch (Exception e) {
                throw exception(COZE_ERROR, e.getMessage());
            }
        });
        return code;
    }

    @Override
    public PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO) {
        String code = pluginResultReqVO.getCode();
        String workflowResp = redisTemplate.boundValueOps(prefix_exectue + code).get();
        PluginExecuteRespVO executeRespVO = new PluginExecuteRespVO();
        if (StringUtils.isBlank(workflowResp)) {
            executeRespVO.setStatus("in_progress");
            return executeRespVO;
        }

        CozeResponse<String> response = JSONUtil.toBean(workflowResp, CozeResponse.class);
        if (response.getCode() != 0 || Objects.isNull(response.getData())) {
            throw exception(COZE_ERROR, response.getMsg());
        }
        WorkflowResult data = JSONUtil.toBean(response.getData(), WorkflowResult.class);
        String content = data.getData();
        if (StringUtils.isBlank(code)) {
            throw exception(COZE_ERROR, "返回结果为空");
        }

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
}
