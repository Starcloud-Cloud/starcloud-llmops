package com.starcloud.ops.business.open.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.dto.WecomGroupChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelModifyReqVO;
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
import com.starcloud.ops.business.chat.context.RobotContextHolder;
import com.starcloud.ops.business.chat.worktool.dto.WorktoolFriendDTO;
import com.starcloud.ops.business.chat.worktool.request.AddFriendReq;
import com.starcloud.ops.business.chat.worktool.request.ModifyGroupReq;
import com.starcloud.ops.business.chat.worktool.response.ExecuteResultResp;
import com.starcloud.ops.business.open.controller.admin.vo.request.AddFriendReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.GroupCallbackReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.QaCallbackReqVO;
import com.starcloud.ops.business.open.convert.WecomGroupDetailConvert;
import com.starcloud.ops.business.chat.worktool.WorkToolClient;
import com.starcloud.ops.business.chat.worktool.request.BaseReq;
import com.starcloud.ops.business.chat.worktool.request.CreateGroupReq;
import com.starcloud.ops.business.chat.worktool.response.BaseResponse;
import com.starcloud.ops.business.open.controller.admin.vo.request.WecomCreateGroupReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WecomGroupRespVO;
import com.starcloud.ops.business.open.service.WecomChatService;
import com.starcloud.ops.business.open.service.WecomGroupService;
import com.starcloud.ops.business.open.service.manager.WorkToolManager;
import com.starcloud.ops.business.open.service.manager.dto.WorkToolRobotDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private WorkToolManager workToolManager;

    @Resource
    private WecomChatService wecomChatService;


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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFriend(AddFriendReqVO reqVO) {
        WorkToolRobotDTO robotDTO = workToolManager.getRobotId(reqVO.getMobile());
        String robotId = robotDTO.getRobotId();

        AppPublishChannelReqVO channelReqVO = new AppPublishChannelReqVO();
        WecomGroupChannelConfigDTO channelConfigDTO = new WecomGroupChannelConfigDTO();
        channelConfigDTO.setRobotName(robotDTO.getRobotId());
        channelConfigDTO.setRobotId(robotDTO.getRobotName());
        channelReqVO.setName(reqVO.getName());
        channelReqVO.setStatus(0);
        channelReqVO.setAppUid(reqVO.getAppUid());
        channelReqVO.setPublishUid(reqVO.getPublishUid());
        channelReqVO.setType(AppPublishChannelEnum.WX_WORK.getCode());
        channelReqVO.setConfig(channelConfigDTO);
        appPublishChannelService.create(channelReqVO);

        BaseReq<AddFriendReq> baseReq = new BaseReq();
        AddFriendReq addFriendReq = new AddFriendReq();
        addFriendReq.setFriend(WorktoolFriendDTO.newInstance(reqVO.getMobile()));
        baseReq.setList(Collections.singletonList(addFriendReq));
        BaseResponse<String> response = workToolClient.addFriend(robotId, baseReq);
        if (response == null || response.getCode() != 200) {
            log.error("发送加指令失败: {}", response);
            throw new ServiceException(new ErrorCode(500, response.getMessage()));
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPublishChannel(QaCallbackReqVO qaCallbackReqVO) {
        if (StringUtils.isNotBlank(qaCallbackReqVO.getGroupRemark())) {
            AppPublishChannelRespVO oldChannel = appPublishChannelService.getAllByMediumUid(qaCallbackReqVO.getGroupRemark());
            if (oldChannel != null) {
                log.info("此群已绑定发布渠道 {}", qaCallbackReqVO.getGroupRemark());
                wecomChatService.sendMsg(qaCallbackReqVO.getGroupRemark(), "此群已绑定发布渠道", qaCallbackReqVO.getReceivedName());
                return;
            }
        }
        String robotId = RobotContextHolder.getRobotId();

        String mediumUid = qaCallbackReqVO.getSpoken().trim();
        try {
            AppPublishChannelRespVO publishChannel = appPublishChannelService.getAllByMediumUid(mediumUid);
            if (publishChannel == null
                    || !(publishChannel.getConfig() instanceof WecomGroupChannelConfigDTO)) {

                wecomChatService.sendMsg(qaCallbackReqVO.getGroupName(), "此渠道类型不支持绑定群聊", qaCallbackReqVO.getReceivedName());
                return;
            }

            if (publishChannel.getStatus() == null || publishChannel.getStatus() != 0) {
                wecomChatService.sendMsg(qaCallbackReqVO.getGroupName(), "请先启用此渠道", qaCallbackReqVO.getReceivedName());
                return;
            }

            WecomGroupChannelConfigDTO config = (WecomGroupChannelConfigDTO) publishChannel.getConfig();
            if (config.getIsBind() != null && config.getIsBind()) {
                log.info("此渠道已绑定群聊");
                wecomChatService.sendMsg(qaCallbackReqVO.getGroupName(), "此发布渠道已绑定群聊", qaCallbackReqVO.getReceivedName());
                return;
            }

            config.setRobotId(robotId);
            config.setGroupName(qaCallbackReqVO.getGroupName());
            config.setGroupRemark(mediumUid);
            config.setIsBind(true);
            config.setRobotName(workToolManager.getRobotName(robotId));

            AppPublishChannelModifyReqVO reqVO = new AppPublishChannelModifyReqVO();
            reqVO.setConfig(config);
            reqVO.setUid(publishChannel.getUid());
            reqVO.setName(publishChannel.getName());
            reqVO.setStatus(publishChannel.getStatus());
            reqVO.setDescription(publishChannel.getDescription());
            reqVO.setUpdater(publishChannel.getCreator());
            appPublishChannelService.modify(reqVO);

            BaseReq<ModifyGroupReq> baseReq = new BaseReq<>();
            ModifyGroupReq modifyGroupReq = new ModifyGroupReq();
            modifyGroupReq.setGroupName(qaCallbackReqVO.getGroupName());
            modifyGroupReq.setGroupRemark(mediumUid);
            baseReq.setList(Collections.singletonList(modifyGroupReq));
            // 修改群备注
            BaseResponse<String> modified = workToolClient.modifyGroup(robotId, baseReq);
            if (modified == null || modified.getCode() != 200) {
                log.error("修改群备注失败: {}", modified);
                throw new ServiceException(new ErrorCode(500, modified.getMessage()));
            }
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException e) {
            log.info("休眠失败", e);
            throw new ServiceException(new ErrorCode(500, "休眠失败"));
        } catch (Exception e) {
            log.info("修改群备注失败", e);
            wecomChatService.sendMsg(qaCallbackReqVO.getGroupName(), "修改群备注失败，联系管理员", qaCallbackReqVO.getReceivedName());
            throw e;
        }
        wecomChatService.sendMsg(mediumUid, "绑定应用成功", qaCallbackReqVO.getReceivedName());
    }


    private String createGroup(WecomCreateGroupReqVO reqVO) {
        // 废弃
        DictDataDO dictDataDO = dictDataService.parseDictData(WECOM_ROBOT, "robotId");
        String robotId = dictDataDO.getValue();
        String groupRemark = IdUtil.nanoId(16);
        BaseReq<CreateGroupReq> baseReq = new BaseReq();
        CreateGroupReq createGroupReq = new CreateGroupReq();
        createGroupReq.setGroupRemark(groupRemark);
        createGroupReq.setGroupName(reqVO.getGroupName());
        createGroupReq.setGroupAnnouncement(reqVO.getGroupAnnouncement());
        baseReq.setList(Collections.singletonList(createGroupReq));
        BaseResponse<String> resp = workToolClient.createGroup(robotId, baseReq);
        if (resp == null || resp.getCode() != 200) {
            log.error("发送建群指令失败: {}", resp);
            throw new ServiceException(new ErrorCode(500, resp.getMessage()));
        }
        return groupRemark;
    }
}
