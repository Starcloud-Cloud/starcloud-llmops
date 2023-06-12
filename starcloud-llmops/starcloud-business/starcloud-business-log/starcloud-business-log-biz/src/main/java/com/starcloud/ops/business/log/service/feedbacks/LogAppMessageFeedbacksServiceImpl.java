package com.starcloud.ops.business.log.service.feedbacks;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.feedbacks.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageFeedbacksConvert;
import com.starcloud.ops.business.log.dal.dataobject.*;
import com.starcloud.ops.business.log.dal.mysql.*;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.*;

/**
 * 应用执行日志结果反馈 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppMessageFeedbacksServiceImpl implements LogAppMessageFeedbacksService {

    @Resource
    private LogAppMessageFeedbacksMapper appMessageFeedbacksMapper;

    @Override
    public Long createAppMessageFeedbacks(LogAppMessageFeedbacksCreateReqVO createReqVO) {
        // 插入
        LogAppMessageFeedbacksDO appMessageFeedbacks = LogAppMessageFeedbacksConvert.INSTANCE.convert(createReqVO);
        appMessageFeedbacksMapper.insert(appMessageFeedbacks);
        // 返回
        return appMessageFeedbacks.getId();
    }

    @Override
    public void updateAppMessageFeedbacks(LogAppMessageFeedbacksUpdateReqVO updateReqVO) {
        // 校验存在
        validateAppMessageFeedbacksExists(updateReqVO.getId());
        // 更新
        LogAppMessageFeedbacksDO updateObj = LogAppMessageFeedbacksConvert.INSTANCE.convert(updateReqVO);
        appMessageFeedbacksMapper.updateById(updateObj);
    }

    @Override
    public void deleteAppMessageFeedbacks(Long id) {
        // 校验存在
        validateAppMessageFeedbacksExists(id);
        // 删除
        appMessageFeedbacksMapper.deleteById(id);
    }

    private void validateAppMessageFeedbacksExists(Long id) {
        if (appMessageFeedbacksMapper.selectById(id) == null) {
            throw exception(APP_MESSAGE_FEEDBACKS_NOT_EXISTS);
        }
    }

    @Override
    public LogAppMessageFeedbacksDO getAppMessageFeedbacks(Long id) {
        return appMessageFeedbacksMapper.selectById(id);
    }

    @Override
    public List<LogAppMessageFeedbacksDO> getAppMessageFeedbacksList(Collection<Long> ids) {
        return appMessageFeedbacksMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LogAppMessageFeedbacksDO> getAppMessageFeedbacksPage(LogAppMessageFeedbacksPageReqVO pageReqVO) {
        return appMessageFeedbacksMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LogAppMessageFeedbacksDO> getAppMessageFeedbacksList(LogAppMessageFeedbacksExportReqVO exportReqVO) {
        return appMessageFeedbacksMapper.selectList(exportReqVO);
    }

}