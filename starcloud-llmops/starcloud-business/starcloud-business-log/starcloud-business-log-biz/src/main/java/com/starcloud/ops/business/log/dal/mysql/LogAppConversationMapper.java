package com.starcloud.ops.business.log.dal.mysql;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import org.apache.ibatis.annotations.Mapper;


/**
 * 应用执行日志会话 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppConversationMapper extends BaseMapperX<LogAppConversationDO> {

    default PageResult<LogAppConversationDO> selectPage(LogAppConversationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppConversationDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppConversationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppConversationDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppConversationDO::getId));
    }

    default List<LogAppConversationDO> selectList(LogAppConversationExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppConversationDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppConversationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppConversationDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppConversationDO::getId));
    }

}