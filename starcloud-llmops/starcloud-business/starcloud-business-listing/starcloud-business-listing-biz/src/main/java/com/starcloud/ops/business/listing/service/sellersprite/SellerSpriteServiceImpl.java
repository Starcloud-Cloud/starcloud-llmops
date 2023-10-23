package com.starcloud.ops.business.listing.service.sellersprite;

import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareRepose;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.KeywordMinerRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 卖家精灵实现类
 */

@Service
@Slf4j
public class SellerSpriteServiceImpl implements SellerSpriteService {

    private final static String COOKIE = "MEIQIA_TRACK_ID=2WbpzKeSozvXT6jCxLOV3h5SPA8; MEIQIA_VISIT_ID=2WbpzHqu0m2z4JeaNRZ2ONCiOX0; current_guest=g6KFXKURsWAh_231011-169653; t_size=50; t_order_field=created_time; t_order_flag=2; p_c_size=20; k_size=50; ecookie=lLlQblAIj8wXGXLQ_CN; _ga_GJFEMFESN5=GS1.1.1697177537.3.0.1697177537.0.0.0; e3fe274296fb8b207c31=02e12e5974de0db4df738cf595e9c978; 8e791f6ba7788f7fd337=75dc8fa058609140ba894e341c445739; _fp=e922ffabd141ac6185d3e637c34b6f61; 3dad916b41181eaa1ee1=7face27cbe3bd754a9e28bf2b6ae7946; 7bd3ab8ce917b765cafb=d0cf887b23ed257c631c1af078e18a23; JSESSIONID=8B5AFBD0D02C222578BA874B498EB115; _ga_CN0F80S6GL=GS1.1.1698030375.22.0.1698030375.0.0.0; _gaf_fp=9d5f037c9930ba4c3a947155571e213c; _gid=GA1.2.978172362.1698030377; _gat_gtag_UA_135032196_1=1; rank-login-user=9897808961nzydTh/nNL0RFQX2PIIlezFMgNlw91ge6U48cgA0ZIYM74u29Dr3d9QxQEXSYrQ7; rank-login-user-info=\"eyJuaWNrbmFtZSI6IuadreW3numYv+ivuuS/riIsImlzQWRtaW4iOmZhbHNlLCJhY2NvdW50IjoiMTU2KioqKjYyODEiLCJ0b2tlbiI6Ijk4OTc4MDg5NjFuenlkVGgvbk5MMFJGUVgyUElJbGV6Rk1nTmx3OTFnZTZVNDhjZ0EwWklZTTc0dTI5RHIzZDlReFFFWFNZclE3In0=\"; Sprite-X-Token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2Nzk5NjI2YmZlMDQzZTBiYzI5NTEwMTE4ODA3YWExIn0.eyJqdGkiOiJ3Mk9pSXVkVnhCeFc1bDh5Y0VqN0Z3IiwiaWF0IjoxNjk4MDMwMzg5LCJleHAiOjE2OTgxMTY3ODksIm5iZiI6MTY5ODAzMDMyOSwic3ViIjoieXVueWEiLCJpc3MiOiJyYW5rIiwiYXVkIjoic2VsbGVyU3BhY2UiLCJpZCI6ODM3MDg0LCJwaSI6bnVsbCwibm4iOiLmna3lt57pmL_or7rkv64iLCJzeXMiOiJTU19DTiIsImVkIjoiTiIsInBobiI6IjE1NjU4ODA2MjgxIiwibWwiOiJHIn0.CPei5KkqhWzvtKHuqJhiD4PFzkewDPXRHO3RyksQVR1VpWWroNWsazrVfd-lrjSarm1D8vDb7sQXZWIVYNEA7yADKTi148r4Gz5Li_QJNFXqGNP7JyNj_NhZnqQ0WpAPsfFsVpv7viaRc3pzhtdNA0cwOydV_UJ5_v2TF0ZNIioPmOZVvDVFIEi_hyUhLlqtcw9T7TLAbWdgxGkX3ZO4Dd9JYps2Bk7faCvzzOMVf0gg3hcj8KAKoy0gKt7_al2sKeDTx1-ffApPMcNnE9CkHu4m9qySctizLqyvKV9uTSga0mJ3CijjLrH7WYPI-XzLz_owCnQlRQKUWGV82KIiVQ; rank-guest-user=9897808961nzydTh/nNL0RFQX2PIIle8+FO2cSBMIs67Qek98f3E+balKnwQscwBI3h96NO71h; _ga=GA1.1.1322928940.1697011393; _ga_38NCVF2XST=GS1.1.1698030375.23.1.1698030403.32.0.0";
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
        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_KEYWORD_MINER, JSONUtil.toJsonStr(keywordMinerRequestDTO), "");


        return null;
    }

    @Override
    public KeywordMinerReposeDTO BatchKeywordMiner(List<String> keywordS, Integer market) {
        KeywordMinerRequestDTO keywordMinerRequestDTO = new KeywordMinerRequestDTO();
        keywordMinerRequestDTO.setKeywordList(keywordS);
        keywordMinerRequestDTO.setMarket(market);
        keywordMinerRequestDTO.setPageNum(1);
        keywordMinerRequestDTO.setPageSize(50);
        keywordMinerRequestDTO.setHistoryDate(null);
        keywordMinerRequestDTO.setOrderBy(null);
        keywordMinerRequestDTO.setDesc(true);
        keywordMinerRequestDTO.setFilterRootWord(0);
        keywordMinerRequestDTO.setMatchType(0);
        keywordMinerRequestDTO.setAmazonChoice(false);
        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_KEYWORD_MINER, JSONUtil.toJsonStr(keywordMinerRequestDTO), COOKIE);
        Assert.notBlank(reposeResult, "卖家精灵关键词批量挖掘失败");
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
    public PrepareRepose extendPrepare(PrepareRequestDTO prepareRequestDTO) {

        return null;
    }

    /**
     * 根据 ASIN 拓展流量词
     * 全部变体 asinList为用户输入ASIN 集合 queryVariations 为 true
     * 畅销变体 asinList为查询变体返回的diamondList 集合  queryVariations 为 false
     * 当前变体 asinList为用户输入ASIN 集合   queryVariations 为 false
     */
    @Override
    public ExtendAsinReposeDTO extendAsin(ExtendAsinRequestDTO extendAsinRequestDTO) {
        return null;
    }

    /**
     * 根据 ASIN 获取 Listing
     */
    @Override
    public void getListingByAsin() {

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
    private static String unifiedPostRequest(String url, String requestData, String cookie) {
        try {
            String result = HttpRequest.post(url).cookie(cookie)
                    .body(requestData)
                    .execute().body();
            JSONObject entries = JSONUtil.parseObj(result);
            if (!result.isEmpty() && entries.getBool("success", false)) {
                return JSONUtil.toJsonStr(entries.getObj("data"));
            } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                log.error("卖家精灵登录失效");
            }
            return null;
        } catch (Exception e) {
            return null;
        }


    }

    /**
     * 统一请求
     *
     * @param url
     * @return
     */
    private static String unifiedGetRequest(String url, String requestData, String cookie) {
        try {
            String result = HttpRequest.get(url)
                    .body(requestData).cookie(cookie)
                    .execute().body();
            JSONObject entries = JSONUtil.parseObj(result);
            if (!result.isEmpty() || entries.getBool("success", false)) {
                return JSONUtil.toJsonStr(entries.getObj("data"));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String requestData = "{\n" +
                "  \"queryVariations\": false,\n" +
                "  \"asinList\": [\n" +
                "    \"B098T9ZFB5\",\n" +
                "    \"B09JW5FNVX\",\n" +
                "    \"B0B71DH45N\",\n" +
                "    \"B07MHHM31K\",\n" +
                "    \"B08RYQR1CJ\"\n" +
                "  ],\n" +
                "  \"originAsinList\": [\n" +
                "    \"B098T9ZFB5\",\n" +
                "    \"B09JW5FNVX\",\n" +
                "    \"B0B71DH45N\",\n" +
                "    \"B07MHHM31K\",\n" +
                "    \"B08RYQR1CJ\"\n" +
                "  ],\n" +
                "  \"market\": 1,\n" +
                "  \"page\": 1,\n" +
                "  \"month\": \"\",\n" +
                "  \"size\": 1,\n" +
                "  \"orderColumn\": 12,\n" +
                "  \"desc\": true,\n" +
                "  \"exactly\": false,\n" +
                "  \"ac\": false,\n" +
                "  \"filterDeletedKeywords\": false\n" +
                "}";
        String s = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_EXTEND_ASIN, requestData, "");

        System.out.println(s);
    }
}
