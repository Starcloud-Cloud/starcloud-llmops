package com.starcloud.ops.business.listing.service.sellersprite;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.dal.redis.no.SellerSpriteNoRedisDAO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.KeywordMinerRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.SELLER_SPRITE_ACCOUNT_INVALID;

/**
 * 卖家精灵实现类
 */

@Service
@Slf4j
public class SellerSpriteServiceImpl implements SellerSpriteService {

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private DictDataService dictDataService;


    @Resource
    private SellerSpriteNoRedisDAO sellerSpriteNoRedisDAO;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;

    @Resource
    @Lazy // 循环依赖（自己依赖自己），避免报错
    private SellerSpriteServiceImpl self;

    public static final long SELLER_SPRITE_TIMEOUT_MILLIS = 30 * DateUtils.SECOND_MILLIS;


    /**
     * 卖家精灵 API 地址
     */
    private final static String SELLER_SPRITE_ADDRESS = "https://www.sellersprite.com/v3/api/";

    /**
     * 关键词挖掘
     */
    private final static String SELLER_SPRITE_KEYWORD_MINER = "keyword-miner";

    /**
     * 根据 ASIN 获取变体
     */
    private final static String SELLER_SPRITE_EXTEND_PREPARE = "traffic/extend/prepare";

    /**
     * 根据 ASIN 及其变体获取关键词数据
     */
    private final static String SELLER_SPRITE_EXTEND_ASIN = "traffic/extend/asin";

    /**
     * 通过 ASIN 获取 Listing
     * <p>
     */
    private final static String GET_LISTING_BY_ASIN = "listing-builder/get-listing-by-asin";


    private final static String DICT_DATA_TYPE = "SELLER_SPRITE";
    private final static String SELLER_SPRITE_ACCOUNT = "SELLER_SPRITE_ACCOUNT";



    /**
     * 获取可查询时间
     */
    @Override
    public void getDateList() {

    }

    /**
     * 关键词挖掘- 根据关键词获取数据
     */
    @Override
    public KeywordMinerReposeDTO keywordMiner(String keyword, Integer market) {

        KeywordMinerRequestDTO keywordMinerRequestDTO = new KeywordMinerRequestDTO();
        keywordMinerRequestDTO.setKeyword(keyword);
        keywordMinerRequestDTO.setMarket(market);
        keywordMinerRequestDTO.setPageNum(1);
        keywordMinerRequestDTO.setPageSize(50);
        keywordMinerRequestDTO.setHistoryDate(null);
        keywordMinerRequestDTO.setOrderBy(21);
        keywordMinerRequestDTO.setDesc(true);
        keywordMinerRequestDTO.setFilterRootWord(0);
        keywordMinerRequestDTO.setMatchType(0);
        keywordMinerRequestDTO.setAmazonChoice(false);
        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_KEYWORD_MINER, JSONUtil.toJsonStr(keywordMinerRequestDTO));


        return null;
    }

    @Override
    public KeywordMinerReposeDTO BatchKeywordMiner(List<String> keywordS, Integer market) {
        KeywordMinerRequestDTO keywordMinerRequestDTO = new KeywordMinerRequestDTO();
        keywordMinerRequestDTO.setKeywordList(keywordS);
        keywordMinerRequestDTO.setMarket(market);
        keywordMinerRequestDTO.setPageNum(1);
        keywordMinerRequestDTO.setPageSize(50);
        keywordMinerRequestDTO.setHistoryDate("");
        keywordMinerRequestDTO.setOrderBy(null);
        keywordMinerRequestDTO.setDesc(true);
        keywordMinerRequestDTO.setFilterRootWord(0);
        keywordMinerRequestDTO.setMatchType(0);
        keywordMinerRequestDTO.setAmazonChoice(false);
        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_KEYWORD_MINER, JSONUtil.toJsonStr(keywordMinerRequestDTO));
        Assert.notBlank(reposeResult, "关键词批量挖掘失败");
        return JSONUtil.toBean(reposeResult, KeywordMinerReposeDTO.class);

    }

    /**
     * 关键词反查
     */
    @Override
    public void keywordReversing() {

    }

    /**
     * 根据 ASIN 获取变种
     *
     * @param prepareRequestDTO 变种信息 DTO
     */
    @Override
    public PrepareReposeDTO extendPrepare(PrepareRequestDTO prepareRequestDTO) {
        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_EXTEND_PREPARE, JSONUtil.toJsonStr(prepareRequestDTO));
        Assert.notBlank(reposeResult, "系统繁忙，获取变体数据失败");
        return JSONUtil.toBean(reposeResult, PrepareReposeDTO.class);
    }

    /**
     * 根据 ASIN 拓展流量词
     * 全部变体 asinList为用户输入ASIN 集合 queryVariations 为 true
     * 畅销变体 asinList为查询变体返回的diamondList 集合  queryVariations 为 false
     * 当前变体 asinList为用户输入ASIN 集合   queryVariations 为 false
     */
    @Override
    public ExtendAsinReposeDTO extendAsin(ExtendAsinRequestDTO extendAsinRequestDTO) {

        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_EXTEND_ASIN, JSONUtil.toJsonStr(extendAsinRequestDTO));
        Assert.notBlank(reposeResult, "系统繁忙，获取流量词数据失败");
        return JSONUtil.toBean(reposeResult, ExtendAsinReposeDTO.class);
    }


    /**
     * 根据 ASIN 获取 Listing
     */
    @Override
    public SellerSpriteListingVO getListingByAsin(String asin, Integer market) {

        String reposeResult = unifiedGetRequest(SELLER_SPRITE_ADDRESS + GET_LISTING_BY_ASIN, String.format("asin=%s&marketPlace=%s", asin, market));

        Assert.notBlank(reposeResult, "系统繁忙， Listing 数据失败");
        return JSONUtil.toBean(reposeResult, SellerSpriteListingVO.class);
    }

    /**
     * 品牌检测
     */
    @Override
    public void checkBrand() {

    }

    /**
     * 品牌检测
     */
    @Override
    public void AutoUpdateCheckCookies(List<DictDataDO> cookieList) {
        List<DictDataDO> cookies;
        if (CollUtil.isEmpty(cookieList)) {
            // 取出COOKIE池
            cookies = dictDataService.getEnabledDictDataListByType(DICT_DATA_TYPE);
        } else {
            cookies = cookieList;
        }
        // 遍历账号池
        cookies.forEach(cookie -> {
            // 判断当前 cookie 是否过期
            if (!checkCookieIsEnable(cookie.getRemark())) {
                updateSellStripeCookie(cookie);
            }

            long maxNums = RandomUtil.randomLong(6) + 6;
            long between = LocalDateTimeUtil.between(cookie.getUpdateTime(), LocalDateTimeUtil.now(), ChronoUnit.HOURS);
            if (between >= maxNums) {
                updateSellStripeCookie(cookie);
            }
            if (CollUtil.isNotEmpty(cookieList)){
                sendLoginSuccessMessage(cookie.getValue());
            }

        });
    }

    private void updateSellStripeCookie(DictDataDO cookie) {
        DictDataUpdateReqVO updateReqVO = new DictDataUpdateReqVO();
        updateReqVO.setId(cookie.getId())
                .setSort(cookie.getSort())
                .setLabel(cookie.getLabel())
                .setValue(cookie.getValue())
                .setDictType(cookie.getDictType())
                .setStatus(cookie.getStatus())
                .setColorType(cookie.getColorType())
                .setCssClass(cookie.getCssClass());
        // 取出对应账号
        DictDataDO account = dictDataService.getDictData(SELLER_SPRITE_ACCOUNT, cookie.getValue());
        JSONObject accountJson = JSONUtil.parseObj(account.getRemark());
        String cookieData = getCookie(accountJson.getStr("userName"), accountJson.getStr("pwd"));
        if (Objects.nonNull(cookieData)) {
            updateReqVO.setRemark(cookieData);
            dictDataService.updateDictData(updateReqVO);
            log.info("卖家精灵账号更新成功，当前账号为{}", account.getValue());
        }
    }


    /**
     * 统一请求
     *
     * @param url
     * @return
     */
    private String unifiedPostRequest(String url, String requestData) {
        List<DictDataDO> cookies = dictDataService.getEnabledDictDataListByType(DICT_DATA_TYPE);
        Collections.shuffle(cookies);
        String result = null;
        int tag = 0;

        for (DictDataDO data : cookies) {
            try {
                String requestResult = HttpRequest.post(url).cookie(data.getRemark())
                        .body(requestData)
                        .execute().body();
                JSONObject entries = JSONUtil.parseObj(requestResult);
                if (!requestResult.isEmpty() && entries.getBool("success", false)) {
                    log.info("卖家精灵接口数据请求成功，当前账号为{}", data.getValue());
                    result = JSONUtil.toJsonStr(entries.getObj("data"));
                    break; // 找到有效 cookie，退出循环
                } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                    log.error("卖家精灵账号cookie过期，当前账号为{}", data.getValue());
                    tag++;
                    self.executeCookieUpdateAsync(data);
                } else {
                    tag++;
                    log.error("卖家精灵未知问题，数据无法解析，原始数据为:{}", requestResult);
                }
            } catch (Exception e) {
                tag++;
                log.error("卖家精灵未知问题: ", e); // 记录异常信息
            }
        }
        if (StrUtil.isBlank(result) && tag >= cookies.size()) {
            this.sendMessage();
            throw exception(SELLER_SPRITE_ACCOUNT_INVALID);
        }
        return result;
    }

    private Boolean checkCookieIsEnable(String cookie) {
        PrepareRequestDTO prepareRequestDTO = new PrepareRequestDTO().setMarket(1).setAsinList(Collections.singletonList("B098T9ZFB5"));

        try {
            String result = HttpRequest.post(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_EXTEND_PREPARE).cookie(cookie)
                    .body(JSONUtil.toJsonStr(prepareRequestDTO))
                    .execute().body();
            JSONObject entries = JSONUtil.parseObj(result);
            if (!result.isEmpty() && entries.getBool("success", false)) {
                return true;
            } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 统一请求
     *
     * @param url
     * @return
     */
    private String unifiedGetRequest(String url, String requestData) {
        List<DictDataDO> cookies = dictDataService.getEnabledDictDataListByType(DICT_DATA_TYPE);
        Collections.shuffle(cookies);
        String result = null;
        int tag = 0;

        for (DictDataDO data : cookies) {
            try {
                String requestResult = HttpRequest.get(url).body(requestData).cookie(data.getRemark()).execute().body();
                JSONObject entries = JSONUtil.parseObj(requestResult);
                if (!requestResult.isEmpty() && entries.getBool("success", false)) {
                    log.info("卖家精灵接口数据请求成功，当前账号为{}", data.getValue());
                    result = JSONUtil.toJsonStr(entries.getObj("data"));
                    break; // 找到有效 cookie，退出循环
                } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                    log.error("卖家精灵账号cookie过期，当前账号为{}", data.getValue());
                    tag++;
                    self.executeCookieUpdateAsync(data);
                } else {
                    tag++;
                    log.error("卖家精灵未知问题，数据无法解析，原始数据为:{}", requestResult);
                }
            } catch (Exception e) {
                tag++;
                log.error("卖家精灵未知问题: ", e); // 记录异常信息
            }
        }
        if (StrUtil.isBlank(result) && tag >= cookies.size()) {
            this.sendMessage();
            throw exception(SELLER_SPRITE_ACCOUNT_INVALID);
        }
        return result;
    }

    @TenantIgnore
    private void sendMessage() {
        log.error("卖家精灵Cookie 失效，准备发送预警，当前时间【{}】", DateUtil.now());
        try {
            Map<String, Object> templateParams = new HashMap<>();
            String environmentName = dingTalkNoticeProperties.getName().equals("Test") ? "测试环境" : "正式环境";
            templateParams.put("environmentName", environmentName);
            templateParams.put("notifyTime", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("NOTICE_SELLER_SPRITE_WARN")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("卖家精灵通知信息发送失败", e);
        }
    }


    @Nullable
    public String getCookie(String userName, String pwd) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userName", userName);
        map.put("pwd", pwd);

        String cookie;
        try {
            String result = HttpUtil.post("http://cn-test.playwright.hotsalestar.com/playwright/sprite/get-cookie", map, 1000 * 30);
            JSONObject entries = JSONUtil.parseObj(result);
            if (!entries.getBool("success") && !(Boolean) entries.get("success")) {
                cookie = null;
                sendLoginFailMessage(userName);
            } else {
                cookie = JSONUtil.toJsonStr(entries.get("data").toString());
            }

        } catch (RuntimeException e) {
            log.error("卖家精灵账号登录失败");
            cookie = null;
        }

        if (StrUtil.isBlank(cookie)) {
            // 发送报警
            sendLoginFailMessage(userName);
            return null;
        }
        return cookie;
    }


    /**
     * 发送登录失败消息
     */
    @TenantIgnore
    private void sendLoginFailMessage(String account) {
        log.error("卖家精灵登录失败，准备发送预警，当前时间【{}】", DateUtil.now());
        try {
            Map<String, Object> templateParams = new HashMap<>();
            String environmentName = dingTalkNoticeProperties.getName().equals("Test") ? "测试环境" : "正式环境";
            templateParams.put("environmentName", environmentName);
            templateParams.put("account", account);
            templateParams.put("notifyTime", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("NOTICE_SELLER_SPRITE_LOGIN_FAIL")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("卖家精灵登录失败，通知信息发送失败", e);
        }
    }

    @TenantIgnore
    private void sendLoginSuccessMessage(String account) {
        log.error("卖家精灵登录失败，准备发送预警，当前时间【{}】", DateUtil.now());
        try {
            Map<String, Object> templateParams = new HashMap<>();
            String environmentName = dingTalkNoticeProperties.getName().equals("Test") ? "测试环境" : "正式环境";
            templateParams.put("environmentName", environmentName);
            templateParams.put("account", account);
            templateParams.put("notifyTime", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("NOTICE_SELLER_SPRITE_LOGIN_SUCCESS")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("卖家精灵登录成功，通知信息发送失败", e);
        }
    }



    /**
     * 异步更新卖家精灵cookie 更新
     *
     * @param dictDataDO 通知任务
     */
    @Async
    public void executeCookieUpdateAsync(DictDataDO dictDataDO) {
        // 分布式锁，避免并发问题
        sellerSpriteNoRedisDAO.lock(dictDataDO.getId(), SELLER_SPRITE_TIMEOUT_MILLIS, () -> {
            // 执行通知
            self.AutoUpdateCheckCookies(Collections.singletonList(dictDataDO));
        });
    }


}
