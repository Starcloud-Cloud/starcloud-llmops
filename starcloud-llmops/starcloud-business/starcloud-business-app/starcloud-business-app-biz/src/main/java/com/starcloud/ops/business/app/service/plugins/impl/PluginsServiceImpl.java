package com.starcloud.ops.business.app.service.plugins.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.ImageOcrReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.TextExtractionReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.XhsOcrReqVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ImageOcrActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.XhsParseActionHandler;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.plugins.PluginsService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PLUGIN_CONFIG_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PLUGIN_EXECUTE_ERROR;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PLUGIN_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.URL_IS_NOT_IMAGES;

@Slf4j
@Service
public class PluginsServiceImpl implements PluginsService {

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppService appService;

    @Override
    public XhsNoteDTO xhsOcr(XhsOcrReqVO reqVO) {
        XhsDetailConstants.validNoteUrl(reqVO.getXhsNoteUrl());
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.XHS_NOTE_URL, reqVO.getXhsNoteUrl());
        return execute(XhsParseActionHandler.class.getSimpleName(), variableMap).toJavaObject(XhsNoteDTO.class);
    }

    @Override
    public OcrGeneralDTO imageOcr(ImageOcrReqVO reqVO) {
        if (!ImageUploadUtils.isImage(reqVO.getImageUrl())) {
            throw exception(URL_IS_NOT_IMAGES, reqVO.getImageUrl());
        }
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.IMAGE_OCR_URL, reqVO.getImageUrl());

        return execute(ImageOcrActionHandler.class.getSimpleName(), variableMap).toJavaObject(OcrGeneralDTO.class);
    }

    @Override
    public JSONObject intelligentTextExtraction(TextExtractionReqVO reqVO) {
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put("DEFINE", JSONUtil.toJsonPrettyStr(reqVO.getDefine()));
        variableMap.put("PARSE_TEXT", reqVO.getParseText());
        return execute("IntelligentTextExtraction", variableMap);
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
