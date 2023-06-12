package com.starcloud.ops.business.log.service.messagesave;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.messagesave.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageSaveConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageSaveDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageSaveMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.*;

/**
 * 应用执行日志结果保存 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppMessageSaveServiceImpl implements LogAppMessageSaveService {

    @Resource
    private LogAppMessageSaveMapper appMessageSaveMapper;

    @Override
    public Long createAppMessageSave(LogAppMessageSaveCreateReqVO createReqVO) {
        // 插入
        LogAppMessageSaveDO appMessageSave = LogAppMessageSaveConvert.INSTANCE.convert(createReqVO);
        appMessageSaveMapper.insert(appMessageSave);
        // 返回
        return appMessageSave.getId();
    }

    @Override
    public void updateAppMessageSave(LogAppMessageSaveUpdateReqVO updateReqVO) {
        // 校验存在
        validateAppMessageSaveExists(updateReqVO.getId());
        // 更新
        LogAppMessageSaveDO updateObj = LogAppMessageSaveConvert.INSTANCE.convert(updateReqVO);
        appMessageSaveMapper.updateById(updateObj);
    }

    @Override
    public void deleteAppMessageSave(Long id) {
        // 校验存在
        validateAppMessageSaveExists(id);
        // 删除
        appMessageSaveMapper.deleteById(id);
    }

    private void validateAppMessageSaveExists(Long id) {
        if (appMessageSaveMapper.selectById(id) == null) {
            throw exception(APP_MESSAGE_SAVE_NOT_EXISTS);
        }
    }

    @Override
    public LogAppMessageSaveDO getAppMessageSave(Long id) {
        return appMessageSaveMapper.selectById(id);
    }

    @Override
    public List<LogAppMessageSaveDO> getAppMessageSaveList(Collection<Long> ids) {
        return appMessageSaveMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LogAppMessageSaveDO> getAppMessageSavePage(LogAppMessageSavePageReqVO pageReqVO) {
        return appMessageSaveMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LogAppMessageSaveDO> getAppMessageSaveList(LogAppMessageSaveExportReqVO exportReqVO) {
        return appMessageSaveMapper.selectList(exportReqVO);
    }

}