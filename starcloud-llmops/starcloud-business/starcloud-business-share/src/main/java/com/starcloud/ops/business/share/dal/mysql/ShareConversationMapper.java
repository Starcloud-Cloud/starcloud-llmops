package com.starcloud.ops.business.share.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareReq;
import com.starcloud.ops.business.share.dal.dataobject.ShareConversationDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ShareConversationMapper extends BaseMapperX<ShareConversationDO> {
    default ShareConversationDO getByKey(String shareKey) {
        LambdaQueryWrapper<ShareConversationDO> wrapper = Wrappers.lambdaQuery(ShareConversationDO.class)
                .eq(ShareConversationDO::getShareKey, shareKey);
        return selectOne(wrapper);
    }

    default List<ShareConversationDO> selectList(String conversationUid) {
        LambdaQueryWrapper<ShareConversationDO> wrapper = Wrappers.lambdaQuery(ShareConversationDO.class)
                .eq(ShareConversationDO::getConversationUid, conversationUid);
        return selectList(wrapper);
    }

    default void modify(ConversationShareReq req) {
        LambdaUpdateWrapper<ShareConversationDO> updateWrapper = Wrappers.lambdaUpdate(ShareConversationDO.class)
                .eq(ShareConversationDO::getUid, req.getUid())
                .set(req.getDisable() != null, ShareConversationDO::getDisabled, req.getDisable())
                .set(req.getExpiresTime() != null, ShareConversationDO::getExpiresTime, LocalDateTime.now().plusDays(req.getExpiresTime() == null ? 1 : req.getExpiresTime()));
        this.update(null, updateWrapper);
    }
}
