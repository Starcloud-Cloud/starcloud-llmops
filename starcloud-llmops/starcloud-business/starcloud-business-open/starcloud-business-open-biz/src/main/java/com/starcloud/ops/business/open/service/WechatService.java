package com.starcloud.ops.business.open.service;

import cn.iocoder.yudao.module.mp.dal.dataobject.account.MpAccountDO;
import com.starcloud.ops.business.open.api.dto.WeChatRequestDTO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WechatWebChannelReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WeChatBindReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WeChatBindRespVO;

import java.util.List;

public interface WechatService {


    /**
     * 绑定企业微信
     * @param reqVO
     */
    WeChatBindRespVO bindWxAccount(WeChatBindReqVO reqVO);

    /**
     * 异步回复
     *
     * @param chatRequestDTO
     */
    void asynReplyMsg(WeChatRequestDTO chatRequestDTO);

    /**
     * 是否是内部帐号
     * @param wxAppId
     * @return
     */
    Boolean isInternalAccount(String wxAppId);

    /**
     * 绑定的帐号
     * @param appUid
     * @return
     */
    List<MpAccountDO> getAccount(String appUid);

    /**
     * 删除微信公共号
     *
     * @param uid
     */
    void delete(String uid);

    /**
     * 修改绑定的公众号
     * @param uid
     * @param reqVO
     */
    void modify(String uid, WeChatBindReqVO reqVO);

    /**
     * 公共号自动回复/菜单
     * @param req
     */
    void createWebChannel(WechatWebChannelReqVO req);
}
