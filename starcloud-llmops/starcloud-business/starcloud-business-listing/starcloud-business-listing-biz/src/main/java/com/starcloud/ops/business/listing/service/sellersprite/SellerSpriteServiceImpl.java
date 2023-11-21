package com.starcloud.ops.business.listing.service.sellersprite;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.KeywordMinerRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 统一请求
     *
     * @param url
     * @return
     */
    private String unifiedPostRequest(String url, String requestData) {


        String cookie = dictDataService.getDictData("SELLER_SPRITE", "COOKIE").getRemark();

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

    /**
     * 统一请求
     *
     * @param url
     * @return
     */
    private String unifiedGetRequest(String url, String requestData) {
        String cookie = dictDataService.getDictData("SELLER_SPRITE", "COOKIE").getRemark();
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
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                             .setTemplateCode("NOTICE_SELLER_SPRITE_WARN")
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("系统支付通知信息发送失败", e);
        }

    }


}
