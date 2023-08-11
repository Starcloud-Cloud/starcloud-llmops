package com.starcloud.ops.business.chat.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.dto.WecomGroupChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.chat.controller.admin.wecom.vo.request.WecomCreateGroupReqVO;
import com.starcloud.ops.business.chat.controller.admin.wecom.vo.response.WecomGroupRespVO;
import com.starcloud.ops.business.chat.convert.WecomGroupDetailConvert;
import com.starcloud.ops.business.chat.service.WecomGroupService;
import com.starcloud.ops.business.chat.worktool.WorkToolClient;
import com.starcloud.ops.business.chat.worktool.request.BaseReq;
import com.starcloud.ops.business.chat.worktool.request.CreateGroupReq;
import com.starcloud.ops.business.chat.worktool.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.starcloud.ops.business.app.enums.app.AppSceneEnum.WECOM_GROUP;
import static com.starcloud.ops.business.chat.enums.DictTypeConstants.WECOM_ROBOT;

@Slf4j
@Service
public class WecomGroupServiceImpl implements WecomGroupService {

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private AppPublishService appPublishService;

    @Resource
    private WorkToolClient workToolClient;

    @Resource
    private DictDataService dictDataService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initGroup(WecomCreateGroupReqVO reqVO) {
        String appUid = reqVO.getAppUid();
        ChatAppEntity chatAppEntity = AppFactory.factoryChatApp(appUid);
        ChatRequestVO chatRequestVO = new ChatRequestVO();
        chatRequestVO.setScene(WECOM_GROUP.name());
        // worktool 建群
        String groupRemark = createGroup(reqVO);
        // 发布表
        AppPublishReqVO appPublishReqVO = new AppPublishReqVO();
        appPublishReqVO.setAppUid(appUid);
        appPublishReqVO.setLanguage(AppUtils.detectLanguage(chatAppEntity.getName()));
        AppPublishRespVO appPublishRespVO = appPublishService.create(appPublishReqVO);

        // 渠道
        AppPublishChannelReqVO channelReqVO = new AppPublishChannelReqVO();
        WecomGroupChannelConfigDTO channelConfigDTO = new WecomGroupChannelConfigDTO();
        channelConfigDTO.setGroupRemark(groupRemark);
        channelConfigDTO.setGroupName(reqVO.getGroupName());
        channelReqVO.setUid(IdUtil.fastSimpleUUID());
        channelReqVO.setStatus(0);
        channelReqVO.setAppUid(appUid);
        channelReqVO.setPublishUid(appPublishRespVO.getUid());
        channelReqVO.setType(AppPublishChannelEnum.WX_WORK.getCode());
        channelReqVO.setConfig(channelConfigDTO);
        channelReqVO.setMediumUid(groupRemark);
        appPublishChannelService.create(channelReqVO);
    }

    @Override
    public List<WecomGroupRespVO> listGroupDetail(String appUid) {
        List<AppPublishChannelRespVO> channelRespVOS = appPublishChannelService.listByAppUid(appUid);
        List<WecomGroupRespVO> groupRespVOS = new ArrayList<>(channelRespVOS.size());
        for (AppPublishChannelRespVO channelRespVO : channelRespVOS) {
            BaseChannelConfigDTO baseChannelConfigDTO = channelRespVO.getConfig();
            if (baseChannelConfigDTO instanceof WecomGroupChannelConfigDTO) {
                groupRespVOS.add(WecomGroupDetailConvert.INSTANCE.convert((WecomGroupChannelConfigDTO) baseChannelConfigDTO));
            }
        }
        return groupRespVOS;
    }

    private String createGroup(WecomCreateGroupReqVO reqVO) {
        DictDataDO dictDataDO = dictDataService.parseDictData(WECOM_ROBOT, "robotId");
        String robotId = dictDataDO.getValue();
        String groupRemark = IdUtil.fastSimpleUUID();
        BaseReq<CreateGroupReq> baseReq = new BaseReq();
        CreateGroupReq createGroupReq = new CreateGroupReq();
        createGroupReq.setGroupRemark(groupRemark);
        createGroupReq.setGroupName(reqVO.getGroupName());
        createGroupReq.setGroupAnnouncement(reqVO.getGroupAnnouncement());
        baseReq.setList(Collections.singletonList(createGroupReq));
        BaseResponse<String> resp = workToolClient.createGroup(robotId, baseReq);
        if (resp == null || resp.getCode() != 200) {
            log.error("发送建群指令失败: {}", resp);
            throw new ServiceException(new ErrorCode(500,resp.getMessage()));
        }
        return groupRemark;
    }
}
