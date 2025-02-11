package com.starcloud.ops.business.app.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class RedSignatureUtil {

    private static String APP_KEY = "red.2AiITQapXH8WFKFH";

    private static String APP_SECRET = "527e9b5dbea294144613f52681a49ae8";


    public static Map<String, Object>  buildSignatureApi() {

        Map<String, Object> params = buildSignature(APP_SECRET);
        String text = HttpUtil.post("https://edith.xiaohongshu.com/api/sns/v1/ext/access/token", JSONUtil.toJsonStr(params), 5000);
        RedTokenDTO json = JSONUtil.toBean(text, RedTokenDTO.class);
        log.info("sdsd: {} {}", json, text);
        if (json.getSuccess()) {
           String token =  String.valueOf(json.getData().get("access_token"));
            return buildSignature(token);
        } else {
            log.error("获取token失败");
        }

        return null;
    }


    public static Map<String, Object> buildSignature(String secret) {

        long tt = System.currentTimeMillis();

        String nonce = RandomUtil.randomString(22);
        Map<String, Object> verifyConfig = new HashMap<>();

        try {


            String signature = buildSignature(APP_KEY, nonce, String.valueOf(tt), secret);

            verifyConfig.put("app_key", APP_KEY);
            verifyConfig.put("nonce", nonce);
            verifyConfig.put("timestamp", String.valueOf(tt));
            verifyConfig.put("signature", signature);

        } catch (Exception e) {
            log.error("buildSignature 加签失败", e);
        }

        return verifyConfig;
    }

    /**
     * 加签demo 生成sigature 工具
     *
     * @param appKey    唯一标识
     * @param nonce     随机字符串，随机生成-需要和接口请求中保持一致
     * @param timeStamp 当前毫秒级时间戳-例如 2023-08-15 20:31:31 对应时间戳 1692102691696-需要和接口请求中保持一致
     * @param appSecret 1、获取access_token第一次加签，使用密钥appSecret 2、分享秘钥生成第二次加签，使用access_token
     * @return signature 签名
     * @throws Exception
     */
    public static String buildSignature(String appKey, String nonce, String timeStamp, String appSecret) throws Exception {
        Map<String, String> params = Maps.newHashMap();
        params.put("appKey", appKey);
        params.put("nonce", nonce);
        params.put("timeStamp", timeStamp);
        return generateSignature(appSecret, params);
    }

    /**
     * 构建
     *
     * @param secretKey 密钥
     * @param params    加签参数
     * @return 签名
     */
    public static String generateSignature(String secretKey, Map<String, String> params) {
        // Step 1: Sort parameters by key
        Map<String, String> sortedParams = new TreeMap<>(params);
        // Step 2: Concatenate sorted parameters
        StringBuilder paramsString = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (paramsString.length() > 0) {
                paramsString.append("&");
            }
            paramsString.append(entry.getKey()).append("=").append(entry.getValue());
        }
        // Step 3: Add secret key to the parameter string
        paramsString.append(secretKey);
        // Step 4: Calculate signature using SHA-256
        String signature = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(paramsString.toString().getBytes(StandardCharsets.UTF_8));
            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
        return signature;
    }


    @Data
    public class RedTokenDTO {

        private Boolean success;
        private String msg;

        private Map<String, Object> data;

        private Integer code;
    }

    /**
     * 测试用例
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        //String signature = buildSignature("red.r9zp39hyPVPdOm58", "nonce", "1692102691696", "052a768a7bb340bd9c45c82956937ec9");

//        Map<String, String>  signature = RedSignatureUtil.buildSignature();
//        System.out.println(signature);

        log.info("{}", buildSignatureApi());


    }

}