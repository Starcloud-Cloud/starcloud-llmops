package com.starcloud.ops.business.mission.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.enums.NotificationCenterStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationCreateReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NotificationPageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.convert.NotificationCenterConvert;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.mysql.NotificationCenterMapper;
import com.starcloud.ops.business.mission.service.NotificationCenterService;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.*;

@Slf4j
@Service
public class NotificationCenterServiceImpl implements NotificationCenterService {

    @Resource
    private NotificationCenterMapper notificationCenterMapper;

    @Resource
    @Lazy
    private SingleMissionService singleMissionService;

    @Override
    public NotificationRespVO create(NotificationCreateReqVO reqVO) {
        validName(reqVO.getName());
        NotificationCenterDO createDo = NotificationCenterConvert.INSTANCE.convert(reqVO);
        createDo.setUid(IdUtil.fastSimpleUUID());
        createDo.setStatus(NotificationCenterStatusEnum.init.getCode());
        notificationCenterMapper.insert(createDo);
        return NotificationCenterConvert.INSTANCE.convert(createDo);
    }

    @Override
    public PageResult<NotificationRespVO> page(NotificationPageQueryReqVO reqVO) {
        PageResult<NotificationCenterDO> page = notificationCenterMapper.page(reqVO);
        return NotificationCenterConvert.INSTANCE.convert(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(String uid, Boolean publish) {
        NotificationCenterDO notificationCenterDO = getByUid(uid);
        if (BooleanUtils.isTrue(publish)) {
            notificationCenterDO.setStatus(NotificationCenterStatusEnum.published.getCode());
        } else {
            notificationCenterDO.setStatus(NotificationCenterStatusEnum.cancel_published.getCode());
        }
        notificationCenterMapper.updateById(notificationCenterDO);
        singleMissionService.publish(notificationCenterDO.getStatus(), publish);
    }

    @Override
    public void delete(String uid) {
        NotificationCenterDO notificationCenterDO = getByUid(uid);
        notificationCenterMapper.deleteById(notificationCenterDO.getId());
    }

    @Override
    public NotificationCenterDO getByUid(String uid) {
        NotificationCenterDO notificationCenterDO = notificationCenterMapper.selectByUid(uid);
        if (notificationCenterDO == null) {
            throw exception(NOTIFICATION_NOT_EXISTS, uid);
        }
        return notificationCenterDO;
    }

    @Override
    public NotificationRespVO selectByUid(String uid) {
        return NotificationCenterConvert.INSTANCE.convert(getByUid(uid));
    }

    @Override
    public NotificationRespVO modifySelective(NotificationModifyReqVO reqVO) {
        NotificationCenterDO notificationCenterDO = getByUid(reqVO.getUid());
        if (StringUtils.isNotBlank(reqVO.getName()) && StringUtils.equals(notificationCenterDO.getName(), reqVO.getName())) {
            validName(reqVO.getName());
        }
        NotificationCenterConvert.INSTANCE.updateSelective(reqVO, notificationCenterDO);

        singleMissionService.validBudget(notificationCenterDO);
        notificationCenterDO.setUpdateTime(LocalDateTime.now());
        notificationCenterMapper.updateById(notificationCenterDO);
        return NotificationCenterConvert.INSTANCE.convert(notificationCenterDO);
    }

    private void validName(String name) {
        NotificationCenterDO notificationCenterDO = notificationCenterMapper.selectByName(name);
        if (notificationCenterDO != null) {
            throw exception(NOTIFICATION_NAME_EXISTS, name);
        }
    }
}
