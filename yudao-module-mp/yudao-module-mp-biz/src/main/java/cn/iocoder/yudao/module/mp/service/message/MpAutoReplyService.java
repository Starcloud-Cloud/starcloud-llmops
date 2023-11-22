package cn.iocoder.yudao.module.mp.service.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.autoreply.MpAutoReplyCreateReqVO;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.autoreply.MpAutoReplyUpdateReqVO;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpMessagePageReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.message.MpAutoReplyDO;
import cn.iocoder.yudao.module.mp.enums.message.MpAutoReplyTypeEnum;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import java.util.Map;

/**
 * 公众号的自动回复 Service 接口
 *
 * @author 芋道源码
 */
public interface MpAutoReplyService {

    /**
     * 获得公众号自动回复分页
     *
     * @param pageVO 分页请求
     * @return 自动回复分页结果
     */
    PageResult<MpAutoReplyDO> getAutoReplyPage(MpMessagePageReqVO pageVO);


    /**
     * 获得一个公众号自动
     *
     * @param appId
     * @param requestMessageType
     * @return
     */
    MpAutoReplyDO selectListByAppIdAndMessage(String appId, String requestMessageType);

    MpAutoReplyDO selectOneByAppIdAndType(String appId, Integer type);

    /**
     * 获得公众号自动回复
     *
     * @param id 编号
     * @return 自动回复
     */
    MpAutoReplyDO getAutoReply(Long id);


    /**
     * 创建公众号自动回复
     *
     * @param createReqVO 创建请求
     * @return 自动回复的编号
     */
    Long createAutoReply(MpAutoReplyCreateReqVO createReqVO);

    /**
     * 更新公众号自动回复
     *
     * @param updateReqVO 更新请求
     */
    void updateAutoReply(MpAutoReplyUpdateReqVO updateReqVO);

    /**
     * 删除公众号自动回复
     *
     * @param id 自动回复的编号
     */
    void deleteAutoReply(Long id);

    /**
     * 当收到消息时，自动回复
     *
     * @param appId     微信公众号 appId
     * @param wxMessage 消息
     * @return 回复的消息
     */
    WxMpXmlOutMessage replyForMessage(String appId, WxMpXmlMessage wxMessage);

    /**
     * 当粉丝关注时，自动回复
     *
     * @param appId     微信公众号 appId
     * @param wxMessage 消息
     * @return 回复的消息
     */
    WxMpXmlOutMessage replyForSubscribe(String appId, WxMpXmlMessage wxMessage);

    WxMpXmlOutMessage replyForSubscribe(String appId, String suffix, WxMpXmlMessage wxMessage);

    /**
     * 当粉丝关注时，自动回复
     *
     * @param appId
     * @param wxMessage
     * @param params
     * @return
     */
    WxMpXmlOutMessage replyForSubscribe(String appId, WxMpXmlMessage wxMessage, Map<String, Object> params);

}
