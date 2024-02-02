package com.starcloud.ops.business.listing.service.sellersprite;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.ColorScheme;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.KeywordMinerRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.listing.enums.ErrorCodeConstant.SELLER_SPRITE_ACCOUNT_INVALID;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    private final static String DICT_DATA_VALUE = "COOKIE";


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
     * @param prepareRequestDTO
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
    public void AutoUpdateCookies() {
        // 取出账号池
        List<DictDataDO> enabledDictDataListByType = dictDataService.getEnabledDictDataListByType(DICT_DATA_TYPE);
        // 遍历账号池
        enabledDictDataListByType.forEach(cookie -> {
            DictDataUpdateReqVO updateReqVO = new DictDataUpdateReqVO();
            updateReqVO.setId(cookie.getId())
                    .setSort(cookie.getSort())
                    .setLabel(cookie.getLabel())
                    .setValue(cookie.getValue())
                    .setDictType(cookie.getDictType())
                    .setStatus(cookie.getStatus())
                    .setColorType(cookie.getColorType())
                    .setCssClass(cookie.getCssClass());

            // 判断当前 cookie 是否过期
            if (!checkCookieIsEnable(cookie.getValue())) {
                String cookieData = getCookie();
                if (Objects.nonNull(cookieData)) {
                    updateReqVO.setRemark(cookie.getRemark());
                    // 过期则直接 更新
                    dictDataService.updateDictData(updateReqVO);
                }
            }

            Long maxNums = RandomUtil.randomLong(6) + 6;
            long between = LocalDateTimeUtil.between(cookie.getUpdateTime(), LocalDateTimeUtil.now(), ChronoUnit.HOURS);
            if (between >= maxNums) {
                String cookieData = getCookie();
                if (Objects.nonNull(cookieData)) {
                    updateReqVO.setRemark(cookie.getRemark());
                    // 过期则直接 更新
                    dictDataService.updateDictData(updateReqVO);
                }
            }

        });
    }


    /**
     * 统一请求
     *
     * @param url
     * @return
     */
    private String unifiedPostRequest(String url, String requestData) {
        List<DictDataDO> cookies = dictDataService.getEnabledDictDataListByType(DICT_DATA_TYPE);

        // 将列表转换为ArrayList，并随机打乱元素顺序
        Collections.shuffle(cookies);

        // 获取第一个元素作为随机选择的值
        String cookie = cookies.get(0).getRemark();
        // String cookie = dictDataService.getDictData("SELLER_SPRITE", "COOKIE").getRemark();
        try {
            String result = HttpRequest.post(url).cookie(cookie)
                    .body(requestData)
                    .execute().body();
            JSONObject entries = JSONUtil.parseObj(result);
            if (!result.isEmpty() && entries.getBool("success", false)) {
                return JSONUtil.toJsonStr(entries.getObj("data"));
            } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                log.error("卖家精灵登录失效");
                this.sendMessage();
                throw exception(SELLER_SPRITE_ACCOUNT_INVALID);
            }
            return null;
        } catch (Exception e) {
            throw exception(SELLER_SPRITE_ACCOUNT_INVALID);
        }
    }

    private Boolean checkCookieIsEnable(String cookie) {
        PrepareRequestDTO prepareRequestDTO = new PrepareRequestDTO().setMarket(1).setAsinList(Arrays.asList("B098T9ZFB5", "B09JW5FNVX", "B0B71DH45N", "B07MHHM31K", "B08RYQR1CJ"));

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

        // 将列表转换为ArrayList，并随机打乱元素顺序
        Collections.shuffle(cookies);

        // 获取第一个元素作为随机选择的值
        String cookie = cookies.get(0).getRemark();
        // String cookie = dictDataService.getDictData("SELLER_SPRITE", "COOKIE").getRemark();
        try {
            String result = HttpRequest.get(url)
                    .body(requestData).cookie(cookie)
                    .execute().body();
            JSONObject entries = JSONUtil.parseObj(result);
            if (!result.isEmpty() && entries.getBool("success", false)) {
                return JSONUtil.toJsonStr(entries.getObj("data"));
            } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                log.error("卖家精灵登录失效");
                this.sendMessage();
                throw exception(SELLER_SPRITE_ACCOUNT_INVALID);
            }
            return null;
        } catch (Exception e) {
            throw exception(SELLER_SPRITE_ACCOUNT_INVALID);
        }
    }

    @TenantIgnore
    private void sendMessage() {
        log.error("卖家精灵Cookie 失效，准备发送预警，当前时间【{}】", DateUtil.now());
        try {
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("notifyTime", LocalDateTimeUtil.formatNormal(LocalDateTime.now()));
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            .setTemplateCode("NOTICE_SELLER_SPRITE_WARN")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("系统支付通知信息发送失败", e);
        }

    }


    @Nullable
    public static String getCookie() {

        StringBuilder result = new StringBuilder();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://www.sellersprite.com/w/user/login");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("中文")).click(new Locator.ClickOptions()
                    .setClickCount(3));
            page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("账号登录")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("手机号/邮箱/子账号")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("手机号/邮箱/子账号")).fill("starcloud02");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("密 码")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("密 码")).fill("SXqTXRxi8hYRwt:");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("立即登录")).click();

            // 等待页面加载完成
            // page.waitForLoadState(LoadState.NETWORKIDLE);

            page.navigate("https://www.sellersprite.com/v3/keyword-reverse");

            // page.waitForLoadState(LoadState.NETWORKIDLE);

            List<Cookie> cookies = page.context().cookies();

            cookies.stream().forEach(cookie -> {
                result.append(cookie.name + "=" + cookie.value + ";");
            });
            browser.close();

            // =================脚本录制=====================
            // Playwright playwright = Playwright.create();
            // Browser browser = playwright.firefox().launch(
            //         new BrowserType.LaunchOptions().setHeadless(false) //取消无头模式，我们才能看见浏览器操作
            //                 .setSlowMo(100) //减慢执行速度，以免太快
            //                 .setDevtools(false)); //打开浏览器开发者工具，默认不打开
            // BrowserContext browserContext = browser.newContext(
            //         new Browser.NewContextOptions().setColorScheme(ColorScheme.DARK) //设置浏览器主题，chromium设置了dark好像没用
            //                 .setViewportSize(1200, 900) //设置浏览器打开后窗口大小
            // );
            // Page page = browserContext.newPage();
            // page.navigate("https://www.sellersprite.com/w/user/login");
            //
            // page.pause();//暂停脚本
            return result.toString();
        } catch (Exception e) {
            return null;
        }

    }


}
