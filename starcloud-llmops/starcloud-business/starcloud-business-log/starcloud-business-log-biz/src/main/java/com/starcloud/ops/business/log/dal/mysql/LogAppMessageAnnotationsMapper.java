package com.starcloud.ops.business.log.dal.mysql;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.log.api.annotations.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageAnnotationsDO;
import org.apache.ibatis.annotations.Mapper;
/**
 * 应用执行日志结果反馈标注 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageAnnotationsMapper extends BaseMapperX<LogAppMessageAnnotationsDO> {

    default PageResult<LogAppMessageAnnotationsDO> selectPage(LogAppMessageAnnotationsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogAppMessageAnnotationsDO>()
                .eqIfPresent(LogAppMessageAnnotationsDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageAnnotationsDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageAnnotationsDO::getAppMessageUid, reqVO.getAppMessageUid())
                .eqIfPresent(LogAppMessageAnnotationsDO::getAppMessageItem, reqVO.getAppMessageItem())
                .eqIfPresent(LogAppMessageAnnotationsDO::getContent, reqVO.getContent())
                .eqIfPresent(LogAppMessageAnnotationsDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppMessageAnnotationsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppMessageAnnotationsDO::getId));
    }

    default List<LogAppMessageAnnotationsDO> selectList(LogAppMessageAnnotationsExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LogAppMessageAnnotationsDO>()
                .eqIfPresent(LogAppMessageAnnotationsDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageAnnotationsDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageAnnotationsDO::getAppMessageUid, reqVO.getAppMessageUid())
                .eqIfPresent(LogAppMessageAnnotationsDO::getAppMessageItem, reqVO.getAppMessageItem())
                .eqIfPresent(LogAppMessageAnnotationsDO::getContent, reqVO.getContent())
                .eqIfPresent(LogAppMessageAnnotationsDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppMessageAnnotationsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppMessageAnnotationsDO::getId));
    }

}