package com.starcloud.ops.business.extend.framework.feishu.core.client.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.bitable.v1.model.AppTableRecord;
import com.lark.oapi.service.bitable.v1.model.BatchCreateAppTableRecordReq;
import com.lark.oapi.service.bitable.v1.model.BatchCreateAppTableRecordReqBody;
import com.lark.oapi.service.bitable.v1.model.BatchCreateAppTableRecordResp;
import com.starcloud.ops.business.extend.framework.feishu.config.FeiShuProperties;
import com.starcloud.ops.business.extend.framework.feishu.core.client.FeiShuClientFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 飞书客户端工厂实现类
 *
 * @author jason
 */
@AllArgsConstructor
@Service
@Slf4j
public class FeiShuClientFactoryImpl implements FeiShuClientFactory {
    private final FeiShuProperties feiShuProperties;

    @Override
    public Client getClient() {
        return Client.newBuilder(feiShuProperties.getAppId(), feiShuProperties.getSecret()) // 默认配置为自建应用
                // .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers、body 等信息。
                .build();
    }

    @Override
    public String getTenantAccessToken() {


        // 定义请求的URL
        String url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";

        // 定义请求头
        HttpRequest request = HttpUtil.createPost(url);
        request.header("Content-Type", "application/json; charset=utf-8");

        // 定义请求体

        String requestBody = "{\"app_id\": \"" + feiShuProperties.getAppId() + "\", \"app_secret\": \"" + feiShuProperties.getSecret() + "\"}";

        // 发送请求
        HttpResponse response = request.body(requestBody).execute();
        if (response.isOk()) {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body());

                // 获取tenant_access_token的值
                return jsonNode.get("tenant_access_token").asText();
            } catch (Exception e) {
                return null;
            }

        }
        return null;
    }

    @Override
    public Boolean addAppRecords(List<Map<String, Object>> params) {
        String tenantAccessToken = getTenantAccessToken();
        if (tenantAccessToken == null) {
            log.error("getTenantAccessToken error, msg: getTenantAccessToken is null");
            return false;
        }
        AppTableRecord[] records = params.stream()
                .map(param -> AppTableRecord.newBuilder()
                        .fields(param)
                        .build())
                .toArray(AppTableRecord[]::new);


        // 创建请求对象
        BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                .appToken(feiShuProperties.getAppToken())
                .tableId(feiShuProperties.getTableId())
                .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                        .records(records)
                        .build())
                .build();

        // 发起请求
        BatchCreateAppTableRecordResp resp;
        try {
            resp = getClient().bitable().appTableRecord().batchCreate(req, RequestOptions.newBuilder()
                    .tenantAccessToken(tenantAccessToken)
                    .build());
        } catch (Exception e) {
            log.error("addAppRecords error, msg: {}", e.getMessage());
            return false;
        }

        // 处理服务端错误
        if (!resp.success()) {
            log.error(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8)))));
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        // 定义请求的URL
        String url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";

        // 定义请求头
        HttpRequest request = HttpUtil.createPost(url);
        request.header("Content-Type", "application/json; charset=utf-8");

        // 定义请求体
        String requestBody = "{\"app_id\": \"cli_a7e0dc5c6b06d00e\", \"app_secret\": \"j9hc78VkA5GiRtKubkvHEbN5DD8phzKA\"}";
        // String requestBody = "{\"app_id\": \"" + feiShuProperties.getAppId() + "\", \"app_secret\": \"" + feiShuProperties.getSecret() + "\"}";

        // 发送请求
        HttpResponse response = request.body(requestBody).execute();
        if (response.isOk()) {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body());

                // 获取tenant_access_token的值
                System.out.println(jsonNode.get("tenant_access_token").asText());
            } catch (Exception e) {
                System.out.println("解析JSON出错：" + e.getMessage());
            }

        }
    }

}
