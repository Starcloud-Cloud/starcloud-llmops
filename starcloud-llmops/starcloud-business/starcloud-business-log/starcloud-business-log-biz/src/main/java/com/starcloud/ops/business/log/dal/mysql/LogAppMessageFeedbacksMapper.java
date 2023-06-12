package com.starcloud.ops.business.log.dal.mysql;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.log.api.feedbacks.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.*;
import org.apache.ibatis.annotations.Mapper;


/**
 * 应用执行日志结果反馈 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageFeedbacksMapper extends BaseMapperX<LogAppMessageFeedbacksDO> {

    default PageResult<LogAppMessageFeedbacksDO> selectPage(LogAppMessageFeedbacksPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogAppMessageFeedbacksDO>()
                .eqIfPresent(LogAppMessageFeedbacksDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageFeedbacksDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageFeedbacksDO::getAppMessageUid, reqVO.getAppMessageUid())
                .eqIfPresent(LogAppMessageFeedbacksDO::getAppMessageItem, reqVO.getAppMessageItem())
                .eqIfPresent(LogAppMessageFeedbacksDO::getRating, reqVO.getRating())
                .eqIfPresent(LogAppMessageFeedbacksDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppMessageFeedbacksDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppMessageFeedbacksDO::getId));
    }

    default List<LogAppMessageFeedbacksDO> selectList(LogAppMessageFeedbacksExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LogAppMessageFeedbacksDO>()
                .eqIfPresent(LogAppMessageFeedbacksDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageFeedbacksDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageFeedbacksDO::getAppMessageUid, reqVO.getAppMessageUid())
                .eqIfPresent(LogAppMessageFeedbacksDO::getAppMessageItem, reqVO.getAppMessageItem())
                .eqIfPresent(LogAppMessageFeedbacksDO::getRating, reqVO.getRating())
                .eqIfPresent(LogAppMessageFeedbacksDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppMessageFeedbacksDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppMessageFeedbacksDO::getId));
    }

}