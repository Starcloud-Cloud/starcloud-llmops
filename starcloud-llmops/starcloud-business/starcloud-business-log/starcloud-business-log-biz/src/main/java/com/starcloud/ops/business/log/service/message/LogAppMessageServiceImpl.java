package com.starcloud.ops.business.log.service.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.message.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.*;

/**
 * 应用执行日志结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppMessageServiceImpl implements LogAppMessageService {

    @Resource
    private LogAppMessageMapper appMessageMapper;

    @Override
    public Long createAppMessage(LogAppMessageCreateReqVO createReqVO) {
        // 插入
        LogAppMessageDO appMessage = LogAppMessageConvert.INSTANCE.convert(createReqVO);

        appMessage.setCreator(createReqVO.getCreator());
        appMessage.setUpdater(createReqVO.getCreator());

        appMessage.setCreateTime(createReqVO.getCreateTime());
        appMessage.setUpdateTime(createReqVO.getUpdateTime());

        appMessage.setTenantId(createReqVO.getTenantId());

        appMessageMapper.insert(appMessage);
        // 返回
        return appMessage.getId();
    }

    @Override
    public void updateAppMessage(LogAppMessageUpdateReqVO updateReqVO) {
        // 校验存在
        validateAppMessageExists(updateReqVO.getId());
        // 更新
        LogAppMessageDO updateObj = LogAppMessageConvert.INSTANCE.convert(updateReqVO);
        appMessageMapper.updateById(updateObj);
    }

    @Override
    public void deleteAppMessage(Long id) {
        // 校验存在
        validateAppMessageExists(id);
        // 删除
        appMessageMapper.deleteById(id);
    }

    private void validateAppMessageExists(Long id) {
        if (appMessageMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
    }

    @Override
    public LogAppMessageDO getAppMessage(Long id) {
        return appMessageMapper.selectById(id);
    }

    @Override
    public LogAppMessageDO getAppMessage(String uid) {
        return appMessageMapper.selectOne("uid", uid);
    }

    @Override
    public List<LogAppMessageDO> getAppMessageList(Collection<Long> ids) {
        return appMessageMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LogAppMessageDO> getAppMessagePage(LogAppMessagePageReqVO pageReqVO) {
        return appMessageMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LogAppMessageDO> getAppMessageList(LogAppMessageExportReqVO exportReqVO) {
        return appMessageMapper.selectList(exportReqVO);
    }

}