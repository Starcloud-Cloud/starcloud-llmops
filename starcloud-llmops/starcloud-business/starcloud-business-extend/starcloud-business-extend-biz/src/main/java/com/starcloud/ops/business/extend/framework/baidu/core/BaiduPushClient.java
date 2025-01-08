package com.starcloud.ops.business.extend.framework.baidu.core;

import com.starcloud.ops.business.extend.framework.baidu.core.dto.BaiduPushRespDTO;

import java.util.List;

/**
 * 快递客户端接口
 *
 * @author jason
 */
public interface BaiduPushClient {

    /**
     * 数据推送
     *
     */
    BaiduPushRespDTO pushResource(List<String> urls);

}
