package com.starcloud.ops.business.user.service.notify.adapter;

import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpTemplateSendReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.message.MpMessageDO;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.service.message.MpMessageService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateBaseVO;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.service.notify.NotifyMessageService;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyReqDTO;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyResultDTO;
import com.starcloud.ops.business.user.enums.notify.NotifyMediaEnum;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;


@Slf4j
@Service
public class MpTemplateAdapterImpl implements NotifyMediaAdapter {

    @Resource
    private MpMessageService mpMessageService;

    @Resource
    private NotifyMessageService notifyMessageService;

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private MpUserService mpUserService;

    @Resource
    private WxMpService wxMpService;

    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{([a-zA-Z0-9]*?)}");

    private static final Pattern PATTERN_WX_PARAMS = Pattern.compile("\\{([a-zA-Z0-9]*?)\\.DATA}");


    @Override
    public NotifyMediaEnum supportType() {
        return NotifyMediaEnum.wx_template;
    }

    @Override
    public SendNotifyResultDTO sendNotify(SendNotifyReqDTO reqDTO) {
        try {
            MpTemplateSendReqVO reqVO = new MpTemplateSendReqVO();

            SocialUserDO socialUserDO = socialUserService.getSocialUser(reqDTO.getUserId(), reqDTO.getUserType().getValue());
            if (socialUserDO == null) {
                return SendNotifyResultDTO.error(null, "用户未绑定公共号");
            }
            String appId = wxMpService.getWxMpConfigStorage().getAppId();
            MpUserDO mpUserDO = mpUserService.getUser(appId, socialUserDO.getOpenid());
            if (mpUserDO == null) {
                return SendNotifyResultDTO.error(null, "微信公众号粉丝不存在");
            }

            List<WxMpTemplateData> data = new ArrayList<>(reqDTO.getParams().size());
            Map<String, Object> params = reqDTO.getParams();
            for (String key : params.keySet()) {
                WxMpTemplateData wxMpTemplateData = new WxMpTemplateData();
                wxMpTemplateData.setName(key);
                wxMpTemplateData.setValue(params.get(key).toString());
                data.add(wxMpTemplateData);
            }
            reqVO.setMpUserId(mpUserDO.getId());
            reqVO.setTemplateId(reqDTO.getTemplateCode());
            reqVO.setData(data);
            MpMessageDO mpMessageDO = mpMessageService.sendTemplateMessage(reqVO);
            return SendNotifyResultDTO.success(mpMessageDO.getId());
        } catch (Exception e) {
            log.warn("send mp error, userId = {}", reqDTO.getUserId(), e);
            return SendNotifyResultDTO.error(null, e.getMessage());
        }
    }

    @Override
    public void updateLog(Long logId, SendNotifyResultDTO resultDTO) {
        NotifyMessageDO notifyMessage = notifyMessageService.getNotifyMessage(logId);
        notifyMessage.setMpTempSuccess(resultDTO.getSuccess());
        notifyMessage.setMpTempLog(JSONUtil.toJsonStr(resultDTO));
        notifyMessage.setUpdateTime(LocalDateTime.now());
        notifyMessageService.updateById(notifyMessage);
    }

    @Override
    public void valid(NotifyTemplateBaseVO createReqVO) {
        if (StringUtils.isBlank(createReqVO.getRemoteTemplateCode())) {
            throw exception(MISSING_TEMP_CODE, NotifyMediaEnum.wx_template.getDesc());
        }
        try {
            List<WxMpTemplate> allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
            for (WxMpTemplate wxMpTemplate : allPrivateTemplate) {
                if (Objects.equals(createReqVO.getRemoteTemplateCode(), wxMpTemplate.getTemplateId())) {
                    List<String> createTempParams = ReUtil.findAllGroup1(PATTERN_PARAMS, createReqVO.getContent());
                    String wxTemp = wxMpTemplate.getContent();
                    List<String> wxTempParams = ReUtil.findAllGroup1(PATTERN_WX_PARAMS, wxTemp);
                    if (Objects.equals(createTempParams, wxTempParams)) {
                        return;
                    } else {
                        throw exception(TEMP_PARAMS_NOT_CONSISTENT, createTempParams, wxTempParams);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("wx service error", e);
            throw exception(WX_SERVICE_ERROR, e.getMessage());
        }
        throw exception(TEMP_CODE_NOT_EXITS, createReqVO.getRemoteTemplateCode());
    }

}
