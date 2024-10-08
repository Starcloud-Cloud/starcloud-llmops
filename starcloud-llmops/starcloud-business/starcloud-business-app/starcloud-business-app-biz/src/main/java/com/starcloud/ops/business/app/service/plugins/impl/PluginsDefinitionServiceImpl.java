package com.starcloud.ops.business.app.service.plugins.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginConfigModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginListReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.PluginTestReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.VerifyResult;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.convert.plugin.PluginDefinitionConvert;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryAppBindMapper;
import com.starcloud.ops.business.app.dal.mysql.materiallibrary.MaterialLibraryMapper;
import com.starcloud.ops.business.app.dal.mysql.plugin.PluginDefinitionMapper;
import com.starcloud.ops.business.app.enums.plugin.OutputTypeEnum;
import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginBindTypeEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginSceneEnum;
import com.starcloud.ops.business.app.exception.plugins.CozeErrorCode;
import com.starcloud.ops.business.app.feign.CozePublicClient;
import com.starcloud.ops.business.app.feign.dto.coze.CozeBotInfo;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.CozeLastError;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessage;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessageResult;
import com.starcloud.ops.business.app.feign.dto.coze.SpaceInfo;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.app.service.plugins.PluginsDefinitionService;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.job.api.BusinessJobApi;
import com.starcloud.ops.business.job.dto.JobDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.COZE_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.INPUT_JSON_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.INPUT_OUTPUT_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.NAME_DUPLICATE;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.NO_PERMISSIONS;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PLUGIN_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.TOKEN_ERROR;

@Slf4j
@Service
public class PluginsDefinitionServiceImpl implements PluginsDefinitionService {

    @Resource
    private PluginDefinitionMapper pluginDefinitionMapper;

    @Resource
    private CozePublicClient cozePublicClient;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private SocialUserService socialUserService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private PluginConfigService configService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private BusinessJobApi businessJobApi;

    @Resource
    private MaterialLibraryAppBindMapper materialLibraryAppBindMapper;

    @Resource
    private MaterialLibraryMapper materialLibraryMapper;

    private static final String prefix_exectue = "coze_exectue_";

    private static final String prefix_start = "coze_start_";


    @Override
    public Map<String, Object> metadata() {
        Map<String, Object> metadata = Maps.newHashMap();
        metadata.put("platform", PlatformEnum.options());
        metadata.put("scene", PluginSceneEnum.options());
        metadata.put("outputType", OutputTypeEnum.options());
        return metadata;
    }


    public String executePluginCoze(PluginExecuteReqVO executeReqVO, PluginRespVO reqVO) {

        String accessTokenId = reqVO.getCozeTokenId();
        String accessToken = bearer(accessTokenId);
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


    public PluginExecuteRespVO getPluginResultCoze(String code, PluginRespVO reqVO) {

        String accessTokenId = reqVO.getCozeTokenId();
        String cozeResult = redisTemplate.boundValueOps(prefix_exectue + code).get();

        String accessToken = bearer(accessTokenId);
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
            updateTime(time, reqVO.getUid());
        }

        log.info("getPluginResultCoze {}", JSONUtil.toJsonPrettyStr(executeRespVO));
        return executeRespVO;
    }


    /**
     * 更新插件执行时间
     */
    private void updateTime(Long time, String pluginUid) {
        RLock lock = redissonClient.getLock(pluginUid);
        try {
            if (lock.tryLock(5, 5, TimeUnit.SECONDS)) {
                PluginDefinitionDO pluginDefinitionDO = getByUid(pluginUid);
                pluginDefinitionDO.setTotalTime((pluginDefinitionDO.getTotalTime() == null ? 0 : pluginDefinitionDO.getTotalTime()) + time);
                pluginDefinitionDO.setCount((pluginDefinitionDO.getCount() == null ? 0 : pluginDefinitionDO.getCount()) + 1);
                pluginDefinitionDO.setExecuteTimeAvg(time);
                pluginDefinitionMapper.updateById(pluginDefinitionDO);
            }
        } catch (Exception e) {
            log.warn("update time error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public String verify(PluginTestReqVO reqVO) {
        long start = System.currentTimeMillis();
        String accessToken = bearer(reqVO.getAccessTokenId());
        CozeChatRequest request = new CozeChatRequest();
        request.setUserId(Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId()).toString());
        request.setBotId(reqVO.getBotId());
        CozeMessage cozeMessage = new CozeMessage();
        cozeMessage.setRole("user");

        String content = StrUtil.join("\r\n", Arrays.asList("必须使用下面的参数调用工作流:", reqVO.getContent()));
        cozeMessage.setContent(content);

        cozeMessage.setContentType("text");
        request.setMessages(Collections.singletonList(cozeMessage));
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
    public VerifyResult verifyResult(String code, String accessTokenId) {
        String cozeResult = redisTemplate.boundValueOps(prefix_exectue + code).get();
        String accessToken = bearer(accessTokenId);
        if (StringUtils.isBlank(cozeResult)) {
            throw exception(COZE_ERROR, "请重新验证！");
        }
        CozeChatResult cozeChatResult = JSONUtil.toBean(cozeResult, CozeChatResult.class);
        CozeResponse<CozeChatResult> retrieve = cozePublicClient.retrieve(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);
        log.info("coze result {}", JSONUtil.toJsonPrettyStr(retrieve));
        if (retrieve.getCode() != 0) {
            throw exception(COZE_ERROR, retrieve.getMsg());
        }

        VerifyResult verifyResult = new VerifyResult();
        verifyResult.setVerifyState(false);

        String status = Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getStatus).orElse(StringUtils.EMPTY);

        if (Objects.equals("failed", status)
                || Objects.equals("requires_action", status)
                || Objects.equals("canceled", status)

        ) {
            String error = Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getLastError).map(CozeLastError::getMsg).orElse("bot执行失败");
            throw exception(COZE_ERROR, error);
        }

        if (!Objects.equals("completed", status)) {
            verifyResult.setStatus(status);
            verifyResult.setCreatedAt(Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getCreatedAt).orElse(null));
            return verifyResult;
        }
        verifyResult.setStatus("completed");
        verifyResult.setCompletedAt(Optional.ofNullable(retrieve).map(CozeResponse::getData).map(CozeChatResult::getCompletedAt).orElse(null));

        CozeResponse<List<CozeMessageResult>> list = cozePublicClient.messageList(cozeChatResult.getConversationId(), cozeChatResult.getId(), accessToken);

        log.info("messageList list: {}", JSONUtil.toJsonPrettyStr(list));

        if (CollectionUtil.isEmpty(list.getData())) {
            throw exception(COZE_ERROR, "未发现正确的执行记录");
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
        String start = redisTemplate.boundValueOps(prefix_start + code).get();
        if (NumberUtil.isLong(start)) {
            redisTemplate.delete(prefix_start + code);
            long end = System.currentTimeMillis();
            Long time = end - Long.parseLong(start);
            verifyResult.setExecuteTime(time);
        }
        return verifyResult;
    }

    @Override
    public PluginRespVO create(PluginDefinitionVO pluginVO) {
        PluginDefinitionDO pluginConfig = pluginDefinitionMapper.selectByName(pluginVO.getPluginName());
        if (Objects.nonNull(pluginConfig)) {
            throw exception(NAME_DUPLICATE, pluginVO.getPluginName());
        }

        PluginDefinitionDO pluginConfigDO = PluginDefinitionConvert.INSTANCE.convert(pluginVO);
        if (PlatformEnum.coze.getCode().equalsIgnoreCase(pluginConfigDO.getType())) {
            CozeBotInfo cozeBotInfo = botInfo(pluginConfigDO.getEntityUid(), pluginConfigDO.getCozeTokenId());
            pluginConfigDO.setEntityName(cozeBotInfo.getName());
        } else {
            AppMarketRespVO appMarketRespVO = appMarketService.get(pluginConfigDO.getEntityUid());
            pluginConfigDO.setEntityName(appMarketRespVO.getName());
        }

        pluginConfigDO.setUid(IdUtil.fastSimpleUUID());
        pluginDefinitionMapper.insert(pluginConfigDO);
        return PluginDefinitionConvert.INSTANCE.convert(pluginConfigDO);
    }

    @Override
    @DataPermission(enable = false)
    public List<PluginRespVO> publishedList() {
        List<PluginDefinitionDO> pluginDOList = pluginDefinitionMapper.publishedList();
        return PluginDefinitionConvert.INSTANCE.convert(pluginDOList);
    }

    @Override
    public List<PluginRespVO> ownerList() {
        List<PluginDefinitionDO> pluginDOList = pluginDefinitionMapper.selectOwnerPlugin();
        if (CollectionUtil.isEmpty(pluginDOList)) {
            return Collections.emptyList();
        }

        List<String> socialList = pluginDOList.stream().map(PluginDefinitionDO::getCozeTokenId).collect(Collectors.toList());
        Map<String, SocialUserDO> socialUser = socialUserService.getSocialUser(socialList);

        List<PluginRespVO> result = new ArrayList<>(pluginDOList.size());
        for (PluginDefinitionDO pluginDefinitionDO : pluginDOList) {
            PluginRespVO pluginRespVO = PluginDefinitionConvert.INSTANCE.convert(pluginDefinitionDO);
            SocialUserDO socialUserDO = socialUser.get(pluginDefinitionDO.getCozeTokenId());
            if (Objects.nonNull(socialUserDO)) {
                pluginRespVO.setAccountName(socialUserDO.getNickname());
            }
            result.add(pluginRespVO);
        }
        return result;
    }

    @Override
    public List<PluginRespVO> list(PluginListReqVO reqVO) {
        List<PluginConfigRespVO> configList = configService.configList(reqVO.getLibraryUid());
        if (CollectionUtil.isEmpty(configList)) {
            return Collections.emptyList();
        }
        Map<String, PluginConfigRespVO> map = configList.stream().collect(Collectors.toMap(PluginConfigVO::getPluginUid, Function.identity(), (a, b) -> a));
        List<PluginDefinitionDO> pluginDefinitionDOList = pluginDefinitionMapper.selectByUid(configList.stream().map(PluginConfigVO::getPluginUid).collect(Collectors.toList()));
        List<PluginRespVO> result = PluginDefinitionConvert.INSTANCE.convert(pluginDefinitionDOList);
        List<Long> creatorList = pluginDefinitionDOList.stream().map(item -> Long.valueOf(item.getCreator())).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> creatorMap = UserUtils.getUserNicknameMapByIds(creatorList);
        List<JobDetailDTO> jobDetailList = businessJobApi.queryJob(configList.stream().map(PluginConfigRespVO::getUid).collect(Collectors.toList()));
        Map<String, JobDetailDTO> jobMap = jobDetailList.stream().collect(Collectors.toMap(JobDetailDTO::getForeignKey, Function.identity(), (a, b) -> a));
        result.forEach(plugin -> {
            if ((plugin.getCreator() != null)) {
                plugin.setCreator(creatorMap.get(Long.valueOf(plugin.getCreator())));
            }
            PluginConfigRespVO configRespVO = map.get(plugin.getUid());
            if (Objects.nonNull(configRespVO)) {
                plugin.setConfigUid(configRespVO.getUid());
                plugin.setSystemPlugin(PluginBindTypeEnum.isSys(configRespVO.getType()));
            }
            Boolean enable = Optional.ofNullable(jobMap.get(plugin.getConfigUid())).map(JobDetailDTO::getEnable).orElse(Boolean.FALSE);
            plugin.setJobEnable(BooleanUtil.isTrue(enable));
        });
        return result;
    }

    /**
     * 查询应用下的插件列表, 我的应用。
     *
     * @param appUid 应用uid
     * @return 插件列表
     */
    @Override
    public List<PluginRespVO> list(String appUid) {
        // 查询应用下的素材库绑定关系
        LambdaQueryWrapper<MaterialLibraryAppBindDO> wrapper = Wrappers.lambdaQuery(MaterialLibraryAppBindDO.class);
        wrapper.eq(MaterialLibraryAppBindDO::getAppUid, appUid);
        wrapper.eq(MaterialLibraryAppBindDO::getDeleted, false);
        List<MaterialLibraryAppBindDO> libraryAppBindList = materialLibraryAppBindMapper.selectList(wrapper);
        if (CollUtil.isEmpty(libraryAppBindList) || libraryAppBindList.size() > 1) {
            return Collections.emptyList();
        }
        // 根据素材库绑定关系，查询插件查询素材库
        MaterialLibraryAppBindDO libraryAppBind = libraryAppBindList.get(0);
        MaterialLibraryDO materialLibrary = materialLibraryMapper.selectById(libraryAppBind.getLibraryId());
        if (materialLibrary == null) {
            return Collections.emptyList();
        }
        PluginListReqVO request = new PluginListReqVO();
        request.setLibraryUid(materialLibrary.getUid());
        return list(request);
    }

    @Override
    public void publish(String uid) {
        if (UserUtils.isNotAdmin()) {
            throw exception(NO_PERMISSIONS);
        }
        PluginDefinitionDO pluginConfigDO = getByUid(uid);

        pluginConfigDO.setPublished(true);
        pluginDefinitionMapper.updateById(pluginConfigDO);
    }

    @Override
    public PluginRespVO modifyPlugin(PluginConfigModifyReqVO reqVO) {
        PluginDefinitionDO pluginConfigDO = getByUid(reqVO.getUid());
        if (UserUtils.isNotAdmin() && !WebFrameworkUtils.getLoginUserId().toString().equalsIgnoreCase(pluginConfigDO.getCreator())) {
            throw exception(NO_PERMISSIONS);
        }

        PluginDefinitionDO updateConfig = PluginDefinitionConvert.INSTANCE.convert(reqVO);
        if (PlatformEnum.coze.getCode().equalsIgnoreCase(pluginConfigDO.getType())) {
            CozeBotInfo cozeBotInfo = botInfo(pluginConfigDO.getEntityUid(), reqVO.getCozeTokenId());
            pluginConfigDO.setEntityName(cozeBotInfo.getName());
        } else {
            AppMarketRespVO appMarketRespVO = appMarketService.get(pluginConfigDO.getEntityUid());
            pluginConfigDO.setEntityName(appMarketRespVO.getName());
        }
        updateConfig.setId(pluginConfigDO.getId());
        pluginDefinitionMapper.updateById(updateConfig);
        return PluginDefinitionConvert.INSTANCE.convert(updateConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        PluginDefinitionDO pluginDefinitionDO = getByUid(uid);
        pluginDefinitionMapper.deleteById(pluginDefinitionDO.getId());
        configService.deleteByPluginUid(pluginDefinitionDO.getUid());
    }

    @Override
    @DataPermission(enable = false)
    public PluginRespVO detail(String uid) {
        PluginDefinitionDO pluginConfigDO = getByUid(uid);
        return PluginDefinitionConvert.INSTANCE.convert(pluginConfigDO);
    }

    @Override
    public SpaceInfo spaceBot(String spaceId, String accessTokenId, Integer pageSize, Integer pageIndex) {
        String accessToken = bearer(accessTokenId);
        CozeResponse<SpaceInfo> listCozeResponse = cozePublicClient.spaceBots(spaceId, accessToken, pageSize, pageIndex);
        if (listCozeResponse.getCode() != 0) {
            throw exception(COZE_ERROR, listCozeResponse.getMsg());
        }
        return listCozeResponse.getData();
    }

    @Override
    public CozeBotInfo botInfo(String botId, String accessTokenId) {
        String accessToken = bearer(accessTokenId);
        // 测试连接
        CozeResponse<CozeBotInfo> cozeResponse = cozePublicClient.botInfo(botId, accessToken);
        if (cozeResponse.getCode() != 0) {
            throw exception(COZE_ERROR, cozeResponse.getMsg());
        }
        return cozeResponse.getData();
    }

    private String bearer(String accessTokenId) {
        SocialUserDO socialUser = socialUserService.getNewSocialUser(Long.valueOf(accessTokenId));
        if (Objects.isNull(socialUser) || StringUtils.isBlank(socialUser.getToken())) {
            throw exception(TOKEN_ERROR, accessTokenId);
        }
        return "Bearer " + socialUser.getToken();
    }

    private void cleanMap(Map<String, Object> objectMap) {
        objectMap.remove("TAKO_BOT_HISTORY");
    }

    private PluginDefinitionDO getByUid(String uid) {
        PluginDefinitionDO pluginConfigDO = pluginDefinitionMapper.selectByUid(uid);
        if (Objects.isNull(pluginConfigDO)) {
            throw exception(PLUGIN_NOT_EXIST, uid);
        }
        return pluginConfigDO;
    }
}
