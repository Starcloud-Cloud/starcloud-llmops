package com.starcloud.ops.business.log.service.annotations;

import com.starcloud.ops.business.log.api.annotations.vo.*;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageAnnotationsConvert;
import com.starcloud.ops.business.log.dal.dataobject.*;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageAnnotationsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.*;

/**
 * 应用执行日志结果反馈标注 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppMessageAnnotationsServiceImpl implements LogAppMessageAnnotationsService {

    @Resource
    private LogAppMessageAnnotationsMapper appMessageAnnotationsMapper;

    @Override
    public Long createAppMessageAnnotations(LogAppMessageAnnotationsCreateReqVO createReqVO) {
        // 插入
        LogAppMessageAnnotationsDO appMessageAnnotations = LogAppMessageAnnotationsConvert.INSTANCE.convert(createReqVO);
        appMessageAnnotationsMapper.insert(appMessageAnnotations);
        // 返回
        return appMessageAnnotations.getId();
    }

    @Override
    public void updateAppMessageAnnotations(LogAppMessageAnnotationsUpdateReqVO updateReqVO) {
        // 校验存在
        validateAppMessageAnnotationsExists(updateReqVO.getId());
        // 更新
        LogAppMessageAnnotationsDO updateObj = LogAppMessageAnnotationsConvert.INSTANCE.convert(updateReqVO);
        appMessageAnnotationsMapper.updateById(updateObj);
    }

    @Override
    public void deleteAppMessageAnnotations(Long id) {
        // 校验存在
        validateAppMessageAnnotationsExists(id);
        // 删除
        appMessageAnnotationsMapper.deleteById(id);
    }

    private void validateAppMessageAnnotationsExists(Long id) {
        if (appMessageAnnotationsMapper.selectById(id) == null) {
            throw exception(APP_MESSAGE_ANNOTATIONS_NOT_EXISTS);
        }
    }

    @Override
    public LogAppMessageAnnotationsDO getAppMessageAnnotations(Long id) {
        return appMessageAnnotationsMapper.selectById(id);
    }

    @Override
    public List<LogAppMessageAnnotationsDO> getAppMessageAnnotationsList(Collection<Long> ids) {
        return appMessageAnnotationsMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LogAppMessageAnnotationsDO> getAppMessageAnnotationsPage(LogAppMessageAnnotationsPageReqVO pageReqVO) {
        return appMessageAnnotationsMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LogAppMessageAnnotationsDO> getAppMessageAnnotationsList(LogAppMessageAnnotationsExportReqVO exportReqVO) {
        return appMessageAnnotationsMapper.selectList(exportReqVO);
    }

}