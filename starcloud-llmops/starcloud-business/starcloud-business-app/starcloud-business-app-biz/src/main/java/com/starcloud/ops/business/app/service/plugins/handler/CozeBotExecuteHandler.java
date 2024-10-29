package com.starcloud.ops.business.app.service.plugins.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
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
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.COZE_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.INPUT_OUTPUT_ERROR;

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
            throw exception(COZE_ERROR, chat.getMsg());
        }
        log.info("conversationId={}, chatId={}, token={}", chat.getData().getConversationId(), chat.getData().getId(), accessToken);
        String code = IdUtil.fastSimpleUUID();
        redisTemplate.boundValueOps(prefix_start + code).set(String.valueOf(start), 30, TimeUnit.MINUTES);
        redisTemplate.boundValueOps(prefix_exectue + code).set(JSONUtil.toJsonStr(chat.getData()), 30, TimeUnit.MINUTES);
        return code;
    }

    @Override
    public PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO) {
        PluginRespVO pluginRespVO = pluginsDefinitionService.detail(pluginResultReqVO.getUuid());
        String code = pluginResultReqVO.getCode();
        String accessTokenId = pluginRespVO.getCozeTokenId();
        String cozeResult = redisTemplate.boundValueOps(prefix_exectue + code).get();

        String accessToken = pluginsDefinitionService.bearer(accessTokenId);
        if (StringUtils.isBlank(cozeResult)) {
            throw exception(COZE_ERROR, "请重新验证！");
        }

        CozeChatResult cozeChatResult = JSONUtil.toBean(cozeResult, CozeChatResult.class);
        CozeResponse<CozeChatResult> retrieve = cozePublicClient.retrieve(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);
        log.info("coze result {}", JSONUtil.toJsonPrettyStr(retrieve));
        if (retrieve.getCode() != 0) {
            throw exception(COZE_ERROR, retrieve.getMsg());
        }

        PluginExecuteRespVO executeRespVO = new PluginExecuteRespVO();

        String status = Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getStatus).orElse(StringUtils.EMPTY);
        if (Objects.equals("failed", status)
                || Objects.equals("requires_action", status)
                || Objects.equals("canceled", status)

        ) {
            String error = Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getLastError).map(CozeLastError::getMsg).orElse("bot执行失败");
            throw exception(COZE_ERROR, error);
        }

        if (!Objects.equals("completed", status)) {
            executeRespVO.setStatus(status);
            return executeRespVO;
        }

        executeRespVO.setStatus("completed");
        executeRespVO.setCompletedAt(Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getCompletedAt).orElse(null));
        CozeResponse<List<CozeMessageResult>> list = cozePublicClient.messageList(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);

        if (CollectionUtil.isEmpty(list.getData())) {
            throw exception(COZE_ERROR, "未发现正确的执行记录");
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
        String start = redisTemplate.boundValueOps(prefix_start + code).get();
        if (NumberUtil.isLong(start)) {
            redisTemplate.delete(prefix_start + code);
            long end = System.currentTimeMillis();
            Long time = end - Long.parseLong(start);
            pluginsDefinitionService.updateTime(time, pluginRespVO.getUid());
        }

        log.info("getPluginResultCoze {}", JSONUtil.toJsonPrettyStr(executeRespVO));
        return executeRespVO;
    }
}
