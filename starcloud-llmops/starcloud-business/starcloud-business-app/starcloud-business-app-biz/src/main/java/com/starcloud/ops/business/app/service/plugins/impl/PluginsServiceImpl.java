package com.starcloud.ops.business.app.service.plugins.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ImageOcrActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.XhsParseActionHandler;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.plugins.PluginsService;
import com.starcloud.ops.business.app.service.plugins.handler.PluginExecuteFactory;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

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


    @Override
    @DataPermission(enable = false)
    public String executePlugin(PluginExecuteReqVO reqVO) {
        return pluginExecuteFactory.getHandlerByUid(reqVO.getUuid()).executePlugin(reqVO);
    }

    /**
     * 获取执行返回结果
     */
    @Override
    @DataPermission(enable = false)
    public PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO) {
        return pluginExecuteFactory.getHandlerByUid(pluginResultReqVO.getUuid()).getPluginResult(pluginResultReqVO);
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
    public JSONObject intelligentTextExtraction(TextExtractionReqVO reqVO) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("DEFINE", JSONUtil.toJsonPrettyStr(reqVO.getDefine()));
        variableMap.put("PARSE_TEXT", reqVO.getParseText());
        return execute("IntelligentTextExtraction", variableMap);
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

        return JSONObject.parseObject(String.valueOf(executeResponse.getResult()));
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


}
