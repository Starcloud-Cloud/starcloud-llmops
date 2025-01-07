package com.starcloud.ops.business.extend.framework.feishu.core.client;


import com.lark.oapi.Client;

import java.util.List;
import java.util.Map;

/**
 * 快递客户端工厂接口：用于创建和缓存快递客户端
 *
 * @author jason
 */
public interface FeiShuClientFactory {

    /**
     * 获取默认的快递客户端
     */
    Client getClient();

    String getTenantAccessToken();

    Boolean addAppRecords(List<Map<String, Object>> params);


}
