package com.starcloud.ops.business.log.service.conversation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.log.api.conversation.vo.*;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.*;

/**
 * 应用执行日志会话 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppConversationServiceImpl implements LogAppConversationService {

    @Resource
    private LogAppConversationMapper appConversationMapper;

    @Resource
    private LogAppMessageMapper logAppMessageMapper;

    @Override
    public Long createAppConversation(LogAppConversationCreateReqVO createReqVO) {
        // 插入
        LogAppConversationDO appConversation = LogAppConversationConvert.INSTANCE.convert(createReqVO);
        appConversationMapper.insert(appConversation);
        // 返回
        return appConversation.getId();
    }

    @Override
    public void updateAppConversation(LogAppConversationUpdateReqVO updateReqVO) {
        // 校验存在
        // validateAppConversationExists(updateReqVO.getId());
        // 更新
        LogAppConversationDO updateObj = LogAppConversationConvert.INSTANCE.convert(updateReqVO);

        appConversationMapper.update(updateObj, Wrappers.lambdaQuery(LogAppConversationDO.class).eq(LogAppConversationDO::getUid, updateReqVO.getUid()));

    }

    @Override
    public void deleteAppConversation(Long id) {
        // 校验存在
        validateAppConversationExists(id);
        // 删除
        appConversationMapper.deleteById(id);
    }

    private void validateAppConversationExists(Long id) {
        if (appConversationMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
    }

    @Override
    public LogAppConversationDO getAppConversation(Long id) {
        return appConversationMapper.selectById(id);
    }

    @Override
    public LogAppConversationDO getAppConversation(String uid) {

        return appConversationMapper.selectOne(LogAppConversationDO::getUid, uid);
    }

    @Override
    public List<LogAppConversationDO> getAppConversationList(Collection<Long> ids) {
        return appConversationMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LogAppConversationDO> getAppConversationPage(LogAppConversationPageReqVO pageReqVO) {
        return appConversationMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LogAppConversationDO> getAppConversationList(LogAppConversationExportReqVO exportReqVO) {
        return appConversationMapper.selectList(exportReqVO);
    }


    /**
     * 查询应用执行日志信息接口
     *
     * @param pageReqVO
     * @return
     */
    @Override
    public PageResult<LogAppConversationInfoPO> getAppConversationInfoPage(LogAppConversationInfoPageReqVO pageReqVO) {

        return appConversationMapper.selectPage(pageReqVO);
    }


    @Override
    public List<LogAppMessageStatisticsListPO> getAppMessageStatisticsList(LogAppMessageStatisticsListReqVO statisticsListReqVO) {


        statisticsListReqVO.getTimeType();


        statisticsListReqVO.setStartTime(null);
        statisticsListReqVO.setEndTime(null);


        return logAppMessageMapper.getAppMessageStatisticsList(statisticsListReqVO);

    }


}