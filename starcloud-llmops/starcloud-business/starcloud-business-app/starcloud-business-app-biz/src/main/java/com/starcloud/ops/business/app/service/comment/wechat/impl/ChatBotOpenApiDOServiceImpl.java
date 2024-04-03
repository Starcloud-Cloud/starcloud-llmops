package com.starcloud.ops.business.app.service.comment.wechat.impl;

import com.starcloud.ops.business.app.controller.admin.comment.wechat.vo.ChatBotOpenApiCreateReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.wechat.vo.ChatBotOpenApiUpdateReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.wechat.ChatBotOpenApiDO;
import com.starcloud.ops.business.app.dal.mysql.comment.wechat.ChatBotOpenApiDOMapper;
import com.starcloud.ops.business.app.service.comment.wechat.ChatBotOpenApiDOService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 微信对话开放平台开放 API 实现类
 */
@Service
public class ChatBotOpenApiDOServiceImpl implements ChatBotOpenApiDOService {

    @Resource
    private ChatBotOpenApiDOMapper chatBotOpenApiDOMapper;


    /**
     * 根据API编号 获取指定的API 数据
     *
     * @param id API编号
     */
    @Override
    public ChatBotOpenApiDO get(Long id) {
        return chatBotOpenApiDOMapper.selectById(id);
    }

    /**
     * 获得指定用户 API编号，获取指定的API 数据
     * 根据 ID 获取数据
     *
     * @param userId 用户编号
     * @param id     API编号
     */
    @Override
    public Long get(Long userId, Long id) {
        chatBotOpenApiDOMapper.selectOpenApiByIdAndUserId(id,userId);
        return null;
    }

    /**
     * 添加数据
     *
     * @param userId           用户编号
     * @param createReqVO
     */
    @Override
    public Long add(Long userId, ChatBotOpenApiCreateReqVO createReqVO) {
        return null;
    }

    /**
     * 修改数据
     *
     * @param userId           用户编号
     * @param updateReqVO
     */
    @Override
    public void update(Long userId, ChatBotOpenApiUpdateReqVO updateReqVO) {

    }

    /**
     * 【会员】删除数据
     *
     * @param userId 用户编号
     * @param id     API编号
     */
    @Override
    public void delete(Long userId, Long id) {
        // 1.验证数据是否存在
        ChatBotOpenApiDO chatBotOpenApiDO = chatBotOpenApiDOMapper.selectOpenApiByIdAndUserId(id, userId);
        if (chatBotOpenApiDO == null) {
            throw exception(111);
        }
        // 2. 删除订单
        chatBotOpenApiDOMapper.deleteById(id);
    }
}
