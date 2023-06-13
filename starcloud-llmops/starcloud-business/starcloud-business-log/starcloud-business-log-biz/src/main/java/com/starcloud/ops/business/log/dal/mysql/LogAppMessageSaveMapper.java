package com.starcloud.ops.business.log.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.log.api.messagesave.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 应用执行日志结果保存 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageSaveMapper extends BaseMapperX<LogAppMessageSaveDO> {

    default PageResult<LogAppMessageSaveDO> selectPage(LogAppMessageSavePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogAppMessageSaveDO>()
                .eqIfPresent(LogAppMessageSaveDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageSaveDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageSaveDO::getAppMessageUid, reqVO.getAppMessageUid())
                .eqIfPresent(LogAppMessageSaveDO::getAppMessageItem, reqVO.getAppMessageItem())
                .betweenIfPresent(LogAppMessageSaveDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppMessageSaveDO::getId));
    }

    default List<LogAppMessageSaveDO> selectList(LogAppMessageSaveExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LogAppMessageSaveDO>()
                .eqIfPresent(LogAppMessageSaveDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageSaveDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageSaveDO::getAppMessageUid, reqVO.getAppMessageUid())
                .eqIfPresent(LogAppMessageSaveDO::getAppMessageItem, reqVO.getAppMessageItem())
                .betweenIfPresent(LogAppMessageSaveDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppMessageSaveDO::getId));
    }

}