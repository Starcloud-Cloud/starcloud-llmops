package com.starcloud.ops.business.app.service.log;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.log.api.message.vo.AppLogMessagePageReqVO;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
public interface AppLogService {

    /**
     * 获取文本生成消息详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    PageResult<AppLogMessageRespVO> getLogAppMessageDetail(AppLogMessagePageReqVO query);

    /**
     * 获取图片生成消息详情
     *
     * @param query 查询条件
     * @return ImageRespVO
     */
    PageResult<ImageLogMessageRespVO> getLogImageMessageDetail(AppLogMessagePageReqVO query);

}
