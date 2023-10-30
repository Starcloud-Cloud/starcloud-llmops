package com.starcloud.ops.business.listing.service.sellersprite;

import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
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

    private final static String COOKIE = "MEIQIA_TRACK_ID=2WbpzKeSozvXT6jCxLOV3h5SPA8; MEIQIA_VISIT_ID=2WbpzHqu0m2z4JeaNRZ2ONCiOX0; current_guest=g6KFXKURsWAh_231011-169653; t_size=50; t_order_field=created_time; t_order_flag=2; p_c_size=20; k_size=50; ecookie=lLlQblAIj8wXGXLQ_CN; _ga_GJFEMFESN5=GS1.1.1697177537.3.0.1697177537.0.0.0; _fp=e922ffabd141ac6185d3e637c34b6f61; 7bd3ab8ce917b765cafb=d0cf887b23ed257c631c1af078e18a23; c6f03d623f0773934159=ca964141e81295b935213dc2f1958831; _gid=GA1.2.426447997.1698644183; _gaf_fp=02582e03a79a66c2951c2b8c38e98554; rank-login-user=4670178961mUootleoLbIj0RbLIgjOQM1ZXtFb3KdLEyDALzBwBhyo4RlQ25M2Zh5JOCsFDLca; rank-login-user-info=\"eyJuaWNrbmFtZSI6InN0YXJjbG91ZDAxIiwiaXNBZG1pbiI6ZmFsc2UsImFjY291bnQiOiJzdGFyY2xvdWQwMSIsInRva2VuIjoiNDY3MDE3ODk2MW1Vb290bGVvTGJJajBSYkxJZ2pPUU0xWlh0RmIzS2RMRXlEQUx6QndCaHlvNFJsUTI1TTJaaDVKT0NzRkRMY2EifQ==\"; Sprite-X-Token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2Nzk5NjI2YmZlMDQzZTBiYzI5NTEwMTE4ODA3YWExIn0.eyJqdGkiOiJ1Y0daZEFSX2JGc2tYMTVuOVZNN0FRIiwiaWF0IjoxNjk4NjUzMTY0LCJleHAiOjE2OTg3Mzk1NjQsIm5iZiI6MTY5ODY1MzEwNCwic3ViIjoieXVueWEiLCJpc3MiOiJyYW5rIiwiYXVkIjoic2VsbGVyU3BhY2UiLCJpZCI6ODQ3ODA5LCJwaSI6ODQ3NzU4LCJubiI6IuWkp-mjniIsInN5cyI6IlNTX0NOIiwiZWQiOiJOIiwibWwiOiJTIiwiZW5kIjoxNzI5OTI5OTY0MjgyfQ.ZZwnBx0vnf2ZnOsi7Zi8TGdIo4jMSxpdenvg0pViJBH4UCcb4K_gm32DuWQT1xCZHw7FlhvNb2rFdWDm8P0Ly2lf13wGWlVlt7i-NJO9Q5ckciMuiXGinzAe7sENjxmb17ZNk7KapIaPeN2at6EuUUXXADNNBr3wb_r5-wxbOXPSvBF0XAmykbJNzJlCPacb-LXYbEg0ol0a_P7HvIrNo3JamRq3hW8h42tQkad581Sm49c6PsyTIwj6Z2U-v4GK-F8FWx_nAyIZ1XIAPr6NLP7e3tBrmX4sDByKnSfN3_D4m6GMKwVtQ1zGRaErojt3O3f2a9AwXeOGewWgCMWtuA; ao_lo_to_n=\"4670178961mUootleoLbIj0RbLIgjOQH8mXX8DVtFEDSE3j5jpGsCT2AaEZKlgRnENgGTs2hx5u2Ljzyt29CGvaazdwCnec8G0/D6rYLoCgWoBTDWRS1c=\"; _ga=GA1.1.1322928940.1697011393; JSESSIONID=4E58286EB8B53FB741B54E6BAD71D2BD; _ga_38NCVF2XST=GS1.1.1698660956.39.0.1698660956.60.0.0; _ga_CN0F80S6GL=GS1.1.1698660956.37.0.1698660956.0.0.0" ;

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
    public PrepareReposeDTO extendPrepare(PrepareRequestDTO prepareRequestDTO) {
        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_EXTEND_PREPARE, JSONUtil.toJsonStr(prepareRequestDTO), COOKIE);
        Assert.notBlank(reposeResult,"系统繁忙，请稍后再试");
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

        String reposeResult = unifiedPostRequest(SELLER_SPRITE_ADDRESS + SELLER_SPRITE_EXTEND_ASIN, JSONUtil.toJsonStr(extendAsinRequestDTO), COOKIE);
        Assert.notBlank(reposeResult,"系统繁忙，请稍后再试");
        return JSONUtil.toBean(reposeResult, ExtendAsinReposeDTO.class);
    }


    /**
     * 根据 ASIN 获取 Listing
     */
    @Override
    public SellerSpriteListingVO getListingByAsin(String asin, Integer market) {

        String reposeResult = unifiedGetRequest(SELLER_SPRITE_ADDRESS + GET_LISTING_BY_ASIN, String.format("asin=%s&marketPlace=%s",asin,market ), COOKIE);

        Assert.notBlank(reposeResult,"系统繁忙，请稍后再试");
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
                throw new Exception("卖家精灵登录失效");

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
            if (!result.isEmpty() && entries.getBool("success", false)) {
                return JSONUtil.toJsonStr(entries.getObj("data"));
            } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                log.error("卖家精灵登录失效");
                throw new Exception("卖家精灵登录失效");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String asin = "B0949DWJCV";
        Integer marketPlace = 1;
        String reposeResult = unifiedGetRequest(SELLER_SPRITE_ADDRESS + GET_LISTING_BY_ASIN, String.format("asin=%s&marketPlace=%s",asin,marketPlace ), COOKIE);

        System.out.println(reposeResult);
    }

}
