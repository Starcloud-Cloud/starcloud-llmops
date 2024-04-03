package com.starcloud.ops.business.app.service.comment.wechat;

import com.starcloud.ops.business.app.controller.admin.comment.wechat.vo.ChatBotOpenApiCreateReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.wechat.vo.ChatBotOpenApiUpdateReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.wechat.ChatBotOpenApiDO;

/**
 * 微信对话开放平台开放 API 接口类
 */
public interface ChatBotOpenApiDOService {

    /**
     * 根据API编号 获取指定的API 数据
     */
    ChatBotOpenApiDO get(Long id);

    /**
     * 获得指定用户 API编号，获取指定的API 数据
     * 根据 ID 获取数据
     */
    Long get(Long userId, Long id);

    /**
     * 添加数据
     */
    Long add(Long userId, ChatBotOpenApiCreateReqVO createReqVO);

    /**
     * 修改数据
     */
    void update(Long userId, ChatBotOpenApiUpdateReqVO updateReqVO);

    /**
     * 【会员】删除数据
     *
     * @param userId 用户编号
     * @param id     API编号
     */
    void delete(Long userId, Long id);
}
