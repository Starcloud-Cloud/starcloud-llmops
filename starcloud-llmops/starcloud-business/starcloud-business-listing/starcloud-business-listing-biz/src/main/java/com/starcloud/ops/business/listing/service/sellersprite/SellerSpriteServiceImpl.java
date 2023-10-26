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

    private final static String COOKIE = "MEIQIA_TRACK_ID=2WbpzKeSozvXT6jCxLOV3h5SPA8; MEIQIA_VISIT_ID=2WbpzHqu0m2z4JeaNRZ2ONCiOX0; current_guest=g6KFXKURsWAh_231011-169653; t_size=50; t_order_field=created_time; t_order_flag=2; p_c_size=20; k_size=50; ecookie=lLlQblAIj8wXGXLQ_CN; _ga_GJFEMFESN5=GS1.1.1697177537.3.0.1697177537.0.0.0; _fp=e922ffabd141ac6185d3e637c34b6f61; 3dad916b41181eaa1ee1=7face27cbe3bd754a9e28bf2b6ae7946; 7bd3ab8ce917b765cafb=d0cf887b23ed257c631c1af078e18a23; _gid=GA1.2.1647017292.1698136054; _gaf_fp=988e8bc580692033346d06954cd3252f; rank-login-user=8441628961OZVWHSUVTxPnXpZhKTXe25aR/RustiuBuS2Bz2Ve5FLCbnZQhzfzic7Hhsh1a/x5; rank-login-user-info=\"eyJuaWNrbmFtZSI6IuadreW3numYv+ivuuS/riIsImlzQWRtaW4iOmZhbHNlLCJhY2NvdW50IjoiMTU2KioqKjYyODEiLCJ0b2tlbiI6Ijg0NDE2Mjg5NjFPWlZXSFNVVlR4UG5YcFpoS1RYZTI1YVIvUnVzdGl1QnVTMkJ6MlZlNUZMQ2JuWlFoemZ6aWM3SGhzaDFhL3g1In0=\"; Sprite-X-Token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2Nzk5NjI2YmZlMDQzZTBiYzI5NTEwMTE4ODA3YWExIn0.eyJqdGkiOiJZa0lJR3BuUDV1ZzBDZDFEbXlmZk5BIiwiaWF0IjoxNjk4MjAzODQ4LCJleHAiOjE2OTgyOTAyNDgsIm5iZiI6MTY5ODIwMzc4OCwic3ViIjoieXVueWEiLCJpc3MiOiJyYW5rIiwiYXVkIjoic2VsbGVyU3BhY2UiLCJpZCI6ODM3MDg0LCJwaSI6bnVsbCwibm4iOiLmna3lt57pmL_or7rkv64iLCJzeXMiOiJTU19DTiIsImVkIjoiTiIsInBobiI6IjE1NjU4ODA2MjgxIiwibWwiOiJHIn0.EStqcBIvulYbdH0kT732YmW_DqzZ94_87kw5T6BweG0xeCAXZCGoC2pULthA8v6uueyVGIV4_Nk9gQ-iVAAKMkFRM2zsui8hBRjf2PGAEU3Betsi71b3m0_hC-Rp4Ndw_14Fj0_mI67rULE8cS5X9Me0edeOxWh72NtYjO3CoZ-EuItUPIgz70OJAqdOwgFF-gA7xMMuOBO0JdvniY0RG7FuxbATHjOQNuMzLaK2_MvUKDvhXIM1laqmk9jZmnZC3z6XVlHCCejWP_DnC3XmqROIt5Fnhw9ukIyevcEKEfnc-HKa9qLGZhUYnvSuNNZ20wfWGLo9s1qO-FVDew7Ubw; rank-guest-user=8441628961OZVWHSUVTxPnXpZhKTXe2y81sA/GqKeAx7CGl+LDFWA9QTWiW3XhBy0lIiW/M4iv; _gat_gtag_UA_135032196_1=1; _ga=GA1.1.1322928940.1697011393; JSESSIONID=4EAE9D142582D8A4A77C1126BDBC28D7; _ga_38NCVF2XST=GS1.1.1698227073.34.1.1698227088.45.0.0; _ga_CN0F80S6GL=GS1.1.1698227073.32.1.1698227088.0.0.0";

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
            } else if (entries.getStr("code").equals("ERR_GLOBAL_SESSION_EXPIRED")) {
                log.error("卖家精灵登录失效");
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
