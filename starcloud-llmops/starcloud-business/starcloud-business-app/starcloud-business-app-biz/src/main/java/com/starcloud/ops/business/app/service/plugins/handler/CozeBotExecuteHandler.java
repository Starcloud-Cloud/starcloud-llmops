package com.starcloud.ops.business.app.service.plugins.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
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
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.CozeLastError;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessage;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessageResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import com.starcloud.ops.business.app.service.plugins.impl.PluginsDefinitionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.*;

@Slf4j
@Service
public class CozeBotExecuteHandler extends PluginExecuteHandler {

    @Resource
    private PluginsDefinitionServiceImpl pluginsDefinitionService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private CozePublicClient cozePublicClient;


    @Override
    String supportPlatform() {
        return PlatformEnum.coze.getCode();
    }

    @Override
    public String verify(PluginTestReqVO reqVO) {
        long start = System.currentTimeMillis();
        String accessToken = pluginsDefinitionService.bearer(reqVO.getAccessTokenId());
        CozeChatRequest request = new CozeChatRequest();
        request.setUserId(Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId()).toString());
        request.setBotId(reqVO.getEntityUid());
        CozeMessage cozeMessage = new CozeMessage();
        cozeMessage.setRole("user");

        String content = StrUtil.join("\r\n", Arrays.asList("必须使用下面的参数调用工作流:", reqVO.getContent()));
        cozeMessage.setContent(content);

        cozeMessage.setContentType("text");
        request.setMessages(Collections.singletonList(cozeMessage));
        CozeResponse<CozeChatResult> chat = cozePublicClient.chat(null, request, accessToken);
        if (chat.getCode() != 0) {
            throw exception(COZE_SERVICE_ERROR, chat.getMsg());
        }
        log.info("conversationId={}, chatId={}, token={}", chat.getData().getConversationId(), chat.getData().getId(), accessToken);
        String code = IdUtil.fastSimpleUUID();
        redisTemplate.boundValueOps(PREFIX_START + code).set(String.valueOf(start), 30, TimeUnit.MINUTES);
        redisTemplate.boundValueOps(PREFIX_EXECTUE + code).set(JSONUtil.toJsonStr(chat.getData()), 30, TimeUnit.MINUTES);
        return code;
    }

    @Override
    public VerifyResult verifyResult(PluginTestResultReqVO resultReqVO) {
        String cozeResult = redisTemplate.boundValueOps(PREFIX_EXECTUE + resultReqVO.getCode()).get();
        String accessToken = pluginsDefinitionService.bearer(resultReqVO.getAccessTokenId());
        if (StringUtils.isBlank(cozeResult)) {
            throw exception(COZE_ERROR, "请重新验证！");
        }
        CozeChatResult cozeChatResult = JSONUtil.toBean(cozeResult, CozeChatResult.class);
        CozeResponse<CozeChatResult> retrieve = cozePublicClient.retrieve(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);
        log.info("coze result {}", JSONUtil.toJsonPrettyStr(retrieve));
        if (retrieve.getCode() != 0) {
            throw exception(COZE_SERVICE_ERROR, retrieve.getMsg());
        }

        VerifyResult verifyResult = new VerifyResult();
        verifyResult.setVerifyState(false);

        String status = Optional.of(retrieve).map(CozeResponse::getData).map(CozeChatResult::getStatus).orElse(StringUtils.EMPTY);

        if (Objects.equals("failed", status)
                || Objects.equals("requires_action", status)
                || Objects.equals("canceled", status)

        ) {
            String error = Optional.of(retrieve).map(CozeResponse::getData).map(CozeChatResult::getLastError).map(CozeLastError::getMsg).orElse("bot执行失败");
            throw exception(COZE_SERVICE_ERROR, error);
        }

        if (!Objects.equals("completed", status)) {
            verifyResult.setStatus(status);
            verifyResult.setCreatedAt(Optional.of(retrieve).map(CozeResponse::getData).map(CozeChatResult::getCreatedAt).orElse(null));
            return verifyResult;
        }
        verifyResult.setStatus("completed");
        verifyResult.setCompletedAt(Optional.of(retrieve).map(CozeResponse::getData).map(CozeChatResult::getCompletedAt).orElse(null));

        CozeResponse<List<CozeMessageResult>> list = cozePublicClient.messageList(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);

        log.info("messageList list: {}", JSONUtil.toJsonPrettyStr(list));

        if (list.getCode() != 0) {
            throw exception(COZE_SERVICE_ERROR, list.getMsg());
        }

        if (CollectionUtil.isEmpty(list.getData())) {
            throw exception(COZE_SERVICE_ERROR, "未发现正确的执行记录");
        }

        for (CozeMessageResult datum : list.getData()) {
            if ("tool_response".equalsIgnoreCase(datum.getType())) {
                String content = datum.getContent();
                if (JSONUtil.isTypeJSONArray(content)) {
                    verifyResult.setOutputType(OutputTypeEnum.list.getCode());
                    Type listType = new TypeReference<List<Map<String, Object>>>() {
                    }.getType();
                    List<Map<String, Object>> listMap = JSON.parseObject(content, listType);
                    listMap.forEach(this::cleanMap);
                    verifyResult.setOutput(listMap);
                } else if (JSONUtil.isTypeJSONObject(content)) {
                    verifyResult.setOutputType(OutputTypeEnum.obj.getCode());
                    Type mapType = new TypeReference<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> objectMap = JSON.parseObject(content, mapType);
                    cleanMap(objectMap);
                    verifyResult.setOutput(objectMap);
                } else {
                    log.error("输出结果格式错误 {}", content);
                    throw exception(new CozeErrorCode(content));
                }

            } else if ("function_call".equalsIgnoreCase(datum.getType())) {
                String content = datum.getContent();
                if (!JSONUtil.isTypeJSONObject(content)) {
                    throw exception(INPUT_JSON_ERROR, content);
                }
                JSONObject jsonObject = JSON.parseObject(content);
                Type mapType = new TypeReference<Map<String, Object>>() {
                }.getType();
                verifyResult.setArguments(JSON.parseObject(jsonObject.getString("arguments"), mapType));
            }
        }

        if (Objects.isNull(verifyResult.getArguments()) || Objects.isNull(verifyResult.getOutput())) {
            throw exception(INPUT_OUTPUT_ERROR, "未调用工作流");
        } else {
            verifyResult.setVerifyState(true);
        }
        String start = redisTemplate.boundValueOps(PREFIX_START + resultReqVO.getCode()).get();
        if (NumberUtil.isLong(start)) {
            redisTemplate.delete(PREFIX_START + resultReqVO.getCode());
            long end = System.currentTimeMillis();
            Long time = end - Long.parseLong(start);
            verifyResult.setExecuteTime(time);
        }
        return verifyResult;
    }

    @Override
    public String executePlugin(PluginExecuteReqVO executeReqVO) {
        PluginRespVO reqVO = pluginsDefinitionService.detail(executeReqVO.getUuid());
        String accessToken = pluginsDefinitionService.bearer(reqVO.getCozeTokenId());
        long start = System.currentTimeMillis();

        CozeChatRequest request = new CozeChatRequest();
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        request.setUserId(Objects.isNull(loginUserId) ?
                UserContextHolder.getUserId().toString() : loginUserId.toString());
        request.setBotId(reqVO.getEntityUid());
        CozeMessage cozeMessage = new CozeMessage();
        cozeMessage.setRole("user");
        cozeMessage.setContentType("text");

        String content = StrUtil.join("\r\n", Arrays.asList("必须使用下面的参数调用工作流:", JSONUtil.toJsonStr(executeReqVO.getInputParams())));
        cozeMessage.setContent(content);

        request.setMessages(Collections.singletonList(cozeMessage));

        log.info("executePluginCoze request content: {}", content);

        CozeResponse<CozeChatResult> chat = cozePublicClient.chat(null, request, accessToken);
        if (chat.getCode() != 0) {
            throw exception(COZE_SERVICE_ERROR, chat.getMsg());
        }
        log.info("conversationId={}, chatId={}, token={}", chat.getData().getConversationId(), chat.getData().getId(), accessToken);
        String code = IdUtil.fastSimpleUUID();
        redisTemplate.boundValueOps(PREFIX_START + code).set(String.valueOf(start), 30, TimeUnit.MINUTES);
        redisTemplate.boundValueOps(PREFIX_EXECTUE + code).set(JSONUtil.toJsonStr(chat.getData()), 30, TimeUnit.MINUTES);
        return code;
    }

    @Override
    public PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO) {
        PluginRespVO pluginRespVO = pluginsDefinitionService.detail(pluginResultReqVO.getUuid());
        String code = pluginResultReqVO.getCode();
        String accessTokenId = pluginRespVO.getCozeTokenId();
        String cozeResult = redisTemplate.boundValueOps(PREFIX_EXECTUE + code).get();

        String accessToken = pluginsDefinitionService.bearer(accessTokenId);
        if (StringUtils.isBlank(cozeResult)) {
            throw exception(COZE_ERROR, "请重新验证！");
        }

        CozeChatResult cozeChatResult = JSONUtil.toBean(cozeResult, CozeChatResult.class);
        CozeResponse<CozeChatResult> retrieve = cozePublicClient.retrieve(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);
        log.info("coze result {}", JSONUtil.toJsonPrettyStr(retrieve));
        if (retrieve.getCode() != 0) {
            throw exception(COZE_SERVICE_ERROR, retrieve.getMsg());
        }

        PluginExecuteRespVO executeRespVO = new PluginExecuteRespVO();

        String status = Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getStatus).orElse(StringUtils.EMPTY);
        if (Objects.equals("failed", status)
                || Objects.equals("requires_action", status)
                || Objects.equals("canceled", status)

        ) {
            String error = Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getLastError).map(CozeLastError::getMsg).orElse("bot执行失败");
            throw exception(COZE_SERVICE_ERROR, error);
        }

        if (!Objects.equals("completed", status)) {
            executeRespVO.setStatus(status);
            return executeRespVO;
        }

        executeRespVO.setStatus("completed");
        executeRespVO.setCompletedAt(Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getCompletedAt).orElse(null));
        CozeResponse<List<CozeMessageResult>> list = cozePublicClient.messageList(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);
        if (list.getCode() != 0) {
            throw exception(COZE_SERVICE_ERROR, list.getMsg());
        }

        if (CollectionUtil.isEmpty(list.getData())) {
            throw exception(COZE_SERVICE_ERROR, "未发现正确的执行记录");
        }
        log.info("messageList list: {}", JSONUtil.toJsonPrettyStr(list));
        for (CozeMessageResult datum : list.getData()) {
            if ("tool_response".equalsIgnoreCase(datum.getType())) {
                String content = datum.getContent();
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
            }
        }
        if (Objects.isNull(executeRespVO.getOutput())) {
            throw exception(INPUT_OUTPUT_ERROR, "未调用工作流");
        }
        String start = redisTemplate.boundValueOps(PREFIX_START + code).get();
        if (NumberUtil.isLong(start)) {
            redisTemplate.delete(PREFIX_START + code);
            long end = System.currentTimeMillis();
            Long time = end - Long.parseLong(start);
            pluginsDefinitionService.updateTime(time, pluginRespVO.getUid());
        }

        log.info("getPluginResultCoze {}", JSONUtil.toJsonPrettyStr(executeRespVO));
        return executeRespVO;
    }
}
