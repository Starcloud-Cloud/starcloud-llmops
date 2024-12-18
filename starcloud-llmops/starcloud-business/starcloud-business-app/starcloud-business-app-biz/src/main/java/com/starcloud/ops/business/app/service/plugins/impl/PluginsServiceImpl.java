package com.starcloud.ops.business.app.service.plugins.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.*;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ImageOcrActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.SensitiveWordActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.XhsParseActionHandler;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.app.service.plugins.PluginsDefinitionService;
import com.starcloud.ops.business.app.service.plugins.PluginsService;
import com.starcloud.ops.business.app.service.plugins.handler.PluginExecuteFactory;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.user.api.level.AdminUserLevelApi;
import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelRespDTO;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.*;

@Slf4j
@Service
public class PluginsServiceImpl implements PluginsService {

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppService appService;

    @Resource
    private PluginExecuteFactory pluginExecuteFactory;

    @Resource
    private MaterialLibraryAppBindService libraryAppBindService;

    @Resource
    private MaterialLibraryService libraryService;

    @Resource
    private PluginsDefinitionService pluginsDefinitionService;

    @Resource
    private PluginConfigService pluginConfigService;

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private AdminUserLevelApi adminUserLevelApi;

    @Resource
    private AdminUserApi adminUserApi;

    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String EXECUTE_REQ = "coze_execute_req_";


    @Override
    @DataPermission(enable = false)
    public String executePlugin(PluginExecuteReqVO reqVO) {
        try {
            String code = pluginExecuteFactory.getHandlerByUid(reqVO.getUuid()).executePlugin(reqVO);
            redisTemplate.boundValueOps(EXECUTE_REQ + code).set(JSONUtil.toJsonStr(reqVO), 30, TimeUnit.MINUTES);
            return code;
        } catch (Exception e) {
            log.error("execute plugin error, executeReqStr={}", JSONUtil.toJsonStr(reqVO), e);
            sendMsg(reqVO, e);
            throw e;
        }
    }


    /**
     * 获取执行返回结果
     */
    @Override
    @DataPermission(enable = false)
    public PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO) {
        try {
            return pluginExecuteFactory.getHandlerByUid(pluginResultReqVO.getUuid()).getPluginResult(pluginResultReqVO);
        } catch (Exception e) {
            String executeReqStr = redisTemplate.boundValueOps(EXECUTE_REQ + pluginResultReqVO.getCode()).get();
            log.error("get execute plugin result error, executeReqStr={}", executeReqStr, e);
            if (StringUtils.isNoneBlank(executeReqStr)) {
                PluginExecuteReqVO bean = JSONUtil.toBean(executeReqStr, PluginExecuteReqVO.class);
                sendMsg(bean, e);
            }
            throw e;
        }
    }

    @Override
    public Object syncExecute(PluginExecuteReqVO reqVO) {
        String code = executePlugin(reqVO);
        PluginResultReqVO pluginResultReqVO = new PluginResultReqVO();
        pluginResultReqVO.setCode(code);
        pluginResultReqVO.setUuid(reqVO.getUuid());
        int count = 0;
        while (count < 100) {
            PluginExecuteRespVO pluginResult = getPluginResult(pluginResultReqVO);
            if (StringUtils.equalsIgnoreCase("completed", pluginResult.getStatus())) {
                return pluginResult.getOutput();
            }
            try {
                TimeUnit.SECONDS.sleep(3);
                count++;
            } catch (Exception e) {
                throw exception(PLUGIN_EXECUTE_ERROR, e.getMessage());
            }
        }
        throw exception(PLUGIN_EXECUTE_ERROR, "执行插件超过300s未成功");
    }

    @Override
    public String verify(PluginTestReqVO reqVO) {
        return PluginExecuteFactory.getHandler(reqVO.getType()).verify(reqVO);
    }

    @Override
    public VerifyResult verifyResult(PluginTestResultReqVO resultReqVO) {
        return PluginExecuteFactory.getHandler(resultReqVO.getType()).verifyResult(resultReqVO);
    }

    @Override
    public XhsNoteDTO xhsOcr(XhsOcrReqVO reqVO) {
        XhsDetailConstants.validNoteUrl(reqVO.getXhsNoteUrl());
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.XHS_NOTE_URL, reqVO.getXhsNoteUrl());
        return execute(XhsParseActionHandler.class.getSimpleName(), variableMap).toJavaObject(XhsNoteDTO.class);
    }

    @Override
    public HandlerResponse imageOcr(ImageOcrReqVO reqVO) {
        if (!ImageUploadUtils.isImage(reqVO.getImageUrls())) {
            throw exception(URL_IS_NOT_IMAGES, reqVO.getImageUrls());
        }

        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.IMAGE_OCR_URL, JSONUtil.toJsonStr(reqVO));

        return execute(ImageOcrActionHandler.class.getSimpleName(), variableMap).toJavaObject(HandlerResponse.class);
    }

    @Override
    public JSONObject sensitiveWord(RiskWordReqVO reqVO) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.SENSITIVE_WORD, JSONUtil.toJsonStr(reqVO));
        return execute(SensitiveWordActionHandler.class.getSimpleName(), variableMap);
    }

    @Override
    public JSONObject aiIdentify(AiIdentifyReqVO reqVO) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("USER_INPUT", reqVO.getUserInput());

        String inputFormat = reqVO.getInputFormart();
        List<InputFormat> inputFormatList = JSONUtil.parseArray(inputFormat).toList(InputFormat.class);
        variableMap.put("RESULT_FORMAT", parseSchema(inputFormatList));
        variableMap.put("PLUGIN_DESC", reqVO.getDescription());
        variableMap.put("PLUGIN_NAME", reqVO.getPluginName());
        if (StringUtils.isNoneBlank(reqVO.getUserPrompt())) {
            variableMap.put("USER_PROMPT", reqVO.getUserPrompt());
        }
        return execute("PLUGIN_INPUT_GENERATE", variableMap);
    }

    private String parseSchema(List<InputFormat> inputFormatList) {
        ObjectSchema obj = new ObjectSchema();
        Map<String, JsonSchema> properties = new LinkedHashMap<>(inputFormatList.size());
        for (InputFormat inputFormat : inputFormatList) {
            if (Objects.equals("String", inputFormat.getVariableType())) {
                StringSchema stringSchema = new StringSchema();
                stringSchema.setDescription(inputFormat.getVariableDesc());
                properties.put(inputFormat.getVariableKey(), stringSchema);
            } else if (Objects.equals("Boolean", inputFormat.getVariableType())) {
                BooleanSchema booleanSchema = new BooleanSchema();
                booleanSchema.setDescription(inputFormat.getVariableDesc());
                properties.put(inputFormat.getVariableKey(), booleanSchema);
            } else if (Objects.equals("Array<String>", inputFormat.getVariableType())) {
                ArraySchema arraySchema = new ArraySchema();
                arraySchema.setDescription(inputFormat.getVariableDesc());
                arraySchema.setItemsSchema(new StringSchema());
                properties.put(inputFormat.getVariableKey(), arraySchema);
            }
        }
        obj.setProperties(properties);
        return JsonSchemaUtils.jsonNode2Str(obj);
    }


    @Override
    public JSONObject intelligentTextExtraction(TextExtractionReqVO reqVO) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("DEFINE", JSONUtil.toJsonPrettyStr(reqVO.getDefine()));
        variableMap.put("PARSE_TEXT", reqVO.getParseText());
        return execute("IntelligentTextExtraction", variableMap);
    }

    @Override
    public AppBindPluginRespVO bindPlugin(AppBindPluginReqVO resultReqVO) {
        MaterialLibraryAppBindDO libraryAppBindDO;
        List<PluginDetailVO> result = new ArrayList<>();
        if (CreativePlanSourceEnum.isApp(resultReqVO.getSource())) {
            libraryAppBindDO = libraryAppBindService.getMaterialLibraryAppBind(resultReqVO.getAppUid());
        } else if (CreativePlanSourceEnum.isMarket(resultReqVO.getSource()))
            libraryAppBindDO = libraryAppBindService.getMaterialLibraryAppBind(resultReqVO.getPlanUid());
        else {
            return new AppBindPluginRespVO(result);
        }
        if (Objects.isNull(libraryAppBindDO)) {
            return new AppBindPluginRespVO(result);
        }
        MaterialLibraryDO materialLibrary = libraryService.getMaterialLibrary(libraryAppBindDO.getLibraryId());
        if (Objects.isNull(materialLibrary)) {
            return new AppBindPluginRespVO(result);
        }
        List<PluginConfigRespVO> pluginConfigRespList = pluginConfigService.configList(materialLibrary.getUid());
        if (CollectionUtils.isEmpty(pluginConfigRespList)) {
            return new AppBindPluginRespVO(result);
        }
        for (PluginConfigRespVO pluginConfigRespVO : pluginConfigRespList) {
            if (StringUtils.isBlank(pluginConfigRespVO.getFieldMap())) {
                continue;
            }
            PluginRespVO detail = pluginsDefinitionService.detail(pluginConfigRespVO.getPluginUid());
            if (Objects.isNull(detail)) {
                continue;
            }
            if (BooleanUtils.isNotTrue(detail.getEnableAi())) {
                continue;
            }
            PluginDetailVO pluginDetailVO = new PluginDetailVO(detail, pluginConfigRespVO);
            result.add(pluginDetailVO);
        }
        return new AppBindPluginRespVO(result);
    }

    /**
     * 不考虑前端传入的类型，因为开始节点参数都是定义出来的
     *
     * @param tag
     * @param data
     * @return
     * @todo 下游要获取，需要实现占位符解析获取
     */
    private JSONObject execute(String tag, Object data) {


        AppMarketRespVO app = getApp(tag);
        String stepId = Optional.ofNullable(app.getWorkflowConfig())
                .map(WorkflowConfigRespVO::getSteps)
                .map(stepList -> stepList.get(0))
                .map(WorkflowStepWrapperRespVO::getStepCode)
                .orElseThrow(() -> exception(PLUGIN_CONFIG_ERROR));


        app.putStartVariable(data);

        AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
        appExecuteRequest.setAppUid(app.getUid());
        appExecuteRequest.setContinuous(Boolean.FALSE);
        appExecuteRequest.setStepId(stepId);
        appExecuteRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(app));
        // 执行应用
        AppExecuteRespVO executeResponse = appService.execute(appExecuteRequest);
        if (!executeResponse.getSuccess() || executeResponse.getResult() == null) {
            throw exception(PLUGIN_EXECUTE_ERROR);
        }

        return JSONObject.parseObject(String.valueOf(executeResponse.getResult()));

    }

    private JSONObject execute(String tag, Map<String, Object> variableMap) {
        AppMarketRespVO app = getApp(tag);
        String stepId = Optional.ofNullable(app.getWorkflowConfig())
                .map(WorkflowConfigRespVO::getSteps)
                .map(stepList -> stepList.get(0))
                .map(WorkflowStepWrapperRespVO::getStepCode)
                .orElseThrow(() -> exception(PLUGIN_CONFIG_ERROR));

        MapUtil.emptyIfNull(variableMap).forEach((key, value) -> {
            app.putVariable(stepId, key, value);
        });
        AppExecuteReqVO appExecuteRequest = new AppExecuteReqVO();
        appExecuteRequest.setAppUid(app.getUid());
        appExecuteRequest.setContinuous(Boolean.FALSE);
        appExecuteRequest.setUserId(SecurityFrameworkUtils.getLoginUserId());
        appExecuteRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        appExecuteRequest.setAppReqVO(AppConvert.INSTANCE.convertRequest(app));
        // 执行应用
        AppExecuteRespVO executeResponse = appService.execute(appExecuteRequest);
        if (!executeResponse.getSuccess() || executeResponse.getResult() == null) {
            throw exception(PLUGIN_EXECUTE_ERROR);
        }

        String result = String.valueOf(executeResponse.getResult());

        JSONObject jsonObject;

        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            log.warn("输出结果不是json格式，{}", result);
            throw exception(PLUGIN_EXECUTE_ERROR, "不是json格式输出");
        }
        return jsonObject;
    }


    private AppMarketRespVO getApp(String tag) {
        AppMarketListQuery query = new AppMarketListQuery();
        query.setTags(Collections.singletonList(tag));
        List<AppMarketRespVO> list = appMarketService.list(query);
        if (CollectionUtils.isEmpty(list)) {
            throw exception(PLUGIN_NOT_EXIST);
        }
        return list.get(0);
    }


    private void sendMsg(PluginExecuteReqVO reqVO, Exception e) {
        try {
            PluginRespVO pluginRespVO = pluginsDefinitionService.detail(reqVO.getUuid());
            Long loginUserId = WebFrameworkUtils.getLoginUserId();
            MaterialLibraryRespVO library = libraryService.getMaterialLibraryByUid(reqVO.getLibraryUid());
            SocialUserDO socialUser = socialUserService.getNewSocialUser(Long.valueOf(pluginRespVO.getCozeTokenId()));
            AdminUserRespDTO user = adminUserApi.getUser(loginUserId);

            Map<Long, List<String>> longListMap = UserUtils.mapUserRoleName(Collections.singletonList(loginUserId));
            List<String> roleNames = longListMap.get(loginUserId);

            HashMap<String, Object> msgMap = new HashMap<>();
            msgMap.put("environment", SpringUtil.getActiveProfile());
            msgMap.put("libraryName", library.getName());
            msgMap.put("libraryUid", library.getUid());
            msgMap.put("pluginName", pluginRespVO.getPluginName());
            msgMap.put("pluginScene", PluginSceneEnum.getName(pluginRespVO.getScene()));
            msgMap.put("socialNickName", socialUser.getNickname());
            msgMap.put("executeUserName", user.getNickname());

            if (CollectionUtil.isNotEmpty(roleNames)) {
                msgMap.put("executeUserLevel", String.join(",", roleNames));
            } else {
                msgMap.put("executeUserLevel", "-");
            }

            msgMap.put("dateTime", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));
            msgMap.put("errorMsg", e.getMessage());
            msgMap.put("stackTrace", ExceptionUtil.stackTraceToString(e, 1000));
            msgMap.put("inputParams", JSONUtil.toJsonStr(reqVO.getInputParams()));
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("NOTICE_COZE_WARN")
                            .setTemplateParams(msgMap));
        } catch (Exception ex) {
            log.error("send msg error", ex);
        }

    }

}
