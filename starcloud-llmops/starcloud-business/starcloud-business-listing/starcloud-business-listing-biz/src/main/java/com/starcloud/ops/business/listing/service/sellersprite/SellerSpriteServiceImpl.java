package com.starcloud.ops.business.listing.service.sellersprite;

import cn.hutool.core.util.StrUtil;
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
import org.springframework.stereotype.Service;

/**
 * 卖家精灵实现类
 */

@Service
public class SellerSpriteServiceImpl implements SellerSpriteService {
    /**
     * 卖家精灵 API 地址
     */
    private final static String SELLER_SPRITE_ADDRESS = "https://www.sellersprite.com/v3/api/";

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
    public KeywordMinerReposeDTO keywordMiner(KeywordMinerRequestDTO keywordMinerRequestDTO) {

        return null;
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
            String result = HttpRequest.post(url)
                    .body(requestData)
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

    /**
     * 统一请求
     *
     * @param url
     * @return
     */
    private static String unifiedGetRequest(String url, String requestData, String cookie) {
        try {
            String result = HttpRequest.get(url)
                    .body(requestData)
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
        String requestData ="{\n" +
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
