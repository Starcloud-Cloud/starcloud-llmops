// package com.starcloud.ops.business.app.service.xhs.huitun.impl;
//
// import cn.hutool.core.lang.TypeReference;
// import cn.hutool.http.HttpRequest;
// import cn.hutool.json.JSONObject;
// import cn.hutool.json.JSONUtil;
// import cn.iocoder.yudao.module.system.service.dict.DictDataService;
// import com.alibaba.fastjson.JSONException;
// import com.starcloud.ops.business.app.service.xhs.huitun.HuiTunService;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.net.CookieManager;
// import java.net.HttpCookie;
// import java.time.Instant;
// import java.util.List;
//
// /**
//  * 灰豚数据实现类
//  */
// @Service
// public class HuiTunServiceImpl implements HuiTunService {
//
//     @Resource
//     private DictDataService dictDataService;
//
//     /**
//      * 灰豚手机登录接口地址
//      */
//     private static final String PHONE_LOGIN_URL = "https://xhsapi.huitun.com/user/phoneLogin";
//
//
//     /**
//      * 灰豚笔记分类接口地址
//      */
//     private static final String FIRST_NOTE_TAGS = "https://xhsapi.huitun.com/common/getFirstNoteTags";
//
//     /**
//      * 搜索数据集合接口地址 区域
//      */
//     private static final String SEARCH_CONDITIONS = "https://xhsapi.huitun.com/common/getSearchConditions";
//
//     /**
//      * 内容特征
//      */
//     private static final String PERSON_FEAT = "https://xhsapi.huitun.com/common/getPersonFeat";
//
//
//     private final static String COOKIE_KEY = "SELLER_SPRITE_ACCOUNT";
//
//
//     /**
//      * 获取第一层标签
//      */
//     @Override
//     public void getFirstNoteTags() {
//
//         HttpRequest httpRequest = HttpRequest.get(FIRST_NOTE_TAGS);
//
//         httpRequest.form("_t", Instant.now().toEpochMilli());
//
//         try {
//             String responseBody = httpRequest.execute().body();
//
//             JSONObject result = JSONUtil.toBean(responseBody, JSONObject.class);
//             if (result.get("status").equals(0)) {
//                 String extData = result.getStr("extData");
//
//                 List<TagLableDTO> tagLableDTOList = null;
//                 try {
//                     if (extData != null && !extData.isEmpty()) {
//                         TypeReference<List<TagLableDTO>> typeReference = new TypeReference<List<TagLableDTO>>() {
//                         };
//                         tagLableDTOList = JSONUtil.toBean(extData.trim(), typeReference, true);
//                     }
//                 } catch (JSONException e) {
//                     // 处理解析异常，可以记录日志或执行其他恢复策略
//                     System.err.println("解析 extData 失败: " + e.getMessage());
//                 }
//
//             }
//         } catch (Exception e) {
//             e.printStackTrace(); // 更细致的异常处理可以在这里实现
//         }
//
//     }
//
//     /**
//      * 获取搜索条件
//      */
//     @Override
//     public void getSearchConditions() {
//
//         HttpRequest httpRequest = HttpRequest.get(SEARCH_CONDITIONS);
//
//         httpRequest.form("_t", Instant.now().toEpochMilli());
//
//         try {
//             String responseBody = httpRequest.execute().body();
//
//             JSONObject result = JSONUtil.toBean(responseBody, JSONObject.class);
//             if (result.get("status").equals(0)) {
//                 String extData = result.getStr("extData");
//
//                 SearchConditionsDTO searchConditionsDTO = null;
//                 try {
//                     if (extData != null && !extData.isEmpty()) {
//                         TypeReference<List<TagLableDTO>> typeReference = new TypeReference<List<TagLableDTO>>() {
//                         };
//                         searchConditionsDTO = JSONUtil.toBean(extData, SearchConditionsDTO.class);
//                     }
//                 } catch (JSONException e) {
//                     // 处理解析异常，可以记录日志或执行其他恢复策略
//                     System.err.println("解析 extData 失败: " + e.getMessage());
//                 }
//
//             }
//         } catch (Exception e) {
//             e.printStackTrace(); // 更细致的异常处理可以在这里实现
//         }
//
//     }
//
//     /**
//      * 获取灰豚个人便签信息
//      */
//     @Override
//     public void getPersonFeat() {
//
//         HttpRequest httpRequest = HttpRequest.get(PERSON_FEAT);
//
//         httpRequest.form("_t", Instant.now().toEpochMilli());
//
//         try {
//             String responseBody = httpRequest.execute().body();
//
//             JSONObject result = JSONUtil.toBean(responseBody, JSONObject.class);
//             if (result.get("status").equals(0)) {
//                 String extData = result.getStr("extData");
//
//                 PersonFeatDTO searchConditionsDTO = null;
//                 try {
//                     if (extData != null && !extData.isEmpty()) {
//
//                         searchConditionsDTO = JSONUtil.toBean(extData, PersonFeatDTO.class);
//                     }
//                 } catch (JSONException e) {
//                     // 处理解析异常，可以记录日志或执行其他恢复策略
//                     System.err.println("解析 extData 失败: " + e.getMessage());
//                 }
//
//             }
//         } catch (Exception e) {
//             e.printStackTrace(); // 更细致的异常处理可以在这里实现
//         }
//
//     }
//
//     // /**
//     //  * 灰豚数据 小红书数据搜索
//     //  *
//     //  * @param batch
//     //  * @param creativePlan
//     //  */
//     // @Override
//     // public void noteSearch(Long batch, CreativePlanRespVO creativePlan) {
//     //
//     //
//     // }
//
//     /**
//      * 灰豚手机登录接口
//      *
//      * @param mobile   手机号
//      * @param password 密码
//      * @return cookie 信息
//      */
//     @Override
//     public String phoneLogin(String mobile, String password) {
//         HttpRequest httpRequest = HttpRequest.get(PHONE_LOGIN_URL);
//
//         httpRequest.form("_t", Instant.now().toEpochMilli());
//         httpRequest.form("mobile", mobile); // 从参数获取，避免硬编码
//         httpRequest.form("password", password); // 从参数获取，避免硬编码
//         try {
//             String responseBody = httpRequest.execute().body();
//             System.out.println(responseBody);
//
//             JSONObject result = JSONUtil.toBean(responseBody, JSONObject.class);
//             if (result.get("status").equals(0)) {
//                 CookieManager cookieManager = HttpRequest.getCookieManager();
//
//                 if (cookieManager == null || cookieManager.getCookieStore() == null) {
//                     System.out.println("CookieManager or CookieStore is null.");
//                     return null;
//                 }
//                 List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
//                 return extractCookiesAsString(cookies);
//             }
//         } catch (Exception e) {
//
//             e.printStackTrace(); // 更细致的异常处理可以在这里实现
//         }
//         return null;
//     }
//
//     /**
//      *
//      */
//     @Override
//     public void autoLogin() {
//         // // 获取Cookie池
//         // List<DictDataDO> cookiesData = dictDataService.getEnabledDictDataListByType(HUITUN_ACCOUNT_COOKIE);
//         // // 遍历账号
//         // // 遍历账号池
//         // cookiesData.forEach(cookie -> {
//         //
//         //     long maxNums = RandomUtil.randomLong(6) + 6;
//         //     long between = LocalDateTimeUtil.between(cookie.getUpdateTime(), LocalDateTimeUtil.now(), ChronoUnit.HOURS);
//         //
//         //
//         //
//         // });
//         // // 登录账号
//         //
//         // // 更新账号 cookie
//
//     }
//
//
//     private static String extractCookiesAsString(List<HttpCookie> cookies) {
//         StringBuilder cookieStringBuilder = new StringBuilder();
//         for (HttpCookie cookie : cookies) {
//             cookieStringBuilder.append(cookie.getName())
//                     .append("=")
//                     .append(cookie.getValue())
//                     .append("; ");
//         }
//         // 移除最后的分号和空格
//         if (cookieStringBuilder.length() > 0) {
//             cookieStringBuilder.setLength(cookieStringBuilder.length() - 2);
//         }
//         return cookieStringBuilder.toString();
//     }
//
//
// }
