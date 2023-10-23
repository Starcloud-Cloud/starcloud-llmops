package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.convert.KeywordMetadataConvert;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.dal.mysql.KeywrodMetadataMapper;
import com.starcloud.ops.business.listing.enums.KeywordMetadataStatusEnum;
import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.business.listing.service.KeywordMetadataService;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ItemsDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.SellerSpriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 原数据实现类
 */
@Service
@Slf4j

public class KeywordMetadataServiceImpl implements KeywordMetadataService {


    @Resource
    private SellerSpriteService sellerSpriteService;

    @Resource
    private KeywrodMetadataMapper keywrodMetadataMapper;


    /**
     * 查询-原数据根据关键词和站点关键词
     *
     * @param pageReqVO
     * @return
     */
    @Override
    @TenantIgnore
    public PageResult<KeywordMetadataRespVO> queryMetaData(QueryKeywordMetadataPageReqVO pageReqVO) {

        return KeywordMetadataConvert.INSTANCE.convertPage(keywrodMetadataMapper.selectPage(pageReqVO));
    }

    /**
     * 新增原数据 -根据关键词和站点关键词
     *
     * @param keywordList 关键词
     * @param marketName
     * @return
     */
    @Override
    @TenantIgnore
    public Boolean addMetaData(List<String> keywordList, String marketName) {
        // 站点数据转换
        SellerSpriteMarketEnum sellerSpriteMarketEnum = SellerSpriteMarketEnum.valueOf(marketName);

        List<KeywordMetadataDO> keywordMetadataDOS = keywrodMetadataMapper.selectList(Wrappers.lambdaQuery(KeywordMetadataDO.class)
                .eq(KeywordMetadataDO::getMarketId, sellerSpriteMarketEnum.getCode())
                .in(KeywordMetadataDO::getKeyword, keywordList));

        // 如果数据库中数据全部存在则直接返回
        if (!keywordMetadataDOS.isEmpty() && keywordList.size() == keywordMetadataDOS.size()) {
            log.info("【关键词原数据新增】===》当前站点【{}】下关键词数据【{}】全部存在,直接返回", marketName, keywordList.toString());
            return true;
        }

        // 获取不在列表的数据
        List<String> notInKeywords;
        if (CollUtil.isNotEmpty(keywordMetadataDOS)) {
            // 获取不在列表的数据
            notInKeywords = keywordMetadataDOS.stream()
                    .map(KeywordMetadataDO::getKeyword)
                    .filter(keyword -> !keywordList.contains(keyword))
                    .collect(Collectors.toList());
        } else {
            notInKeywords = keywordList;
        }

        List<List<String>> splitNotInKeywords = CollUtil.split(notInKeywords, 20);
        // 等待所有任务完成
        List<Boolean> results = new ArrayList<>();

        for (List<String> splitNotInKeyword : splitNotInKeywords) {

            ListenableFuture<Boolean> booleanListenableFuture = this.executeAsyncRequestData(splitNotInKeyword, sellerSpriteMarketEnum.getCode());
            try {
                Boolean aBoolean = booleanListenableFuture.get();
                results.add(aBoolean);
            } catch (Exception e) {
                results.add(false);
            }
        }

        return results.contains(false);
    }

    /**
     * 异步同步
     *
     * @param keywords
     * @param marketId
     */
    @Async
    @TenantIgnore
    public ListenableFuture<Boolean> executeAsyncRequestData(List<String> keywords, Integer marketId) {
        // 初始化不存在数据
        List<KeywordMetadataDO> keywordMetadataDOInitList = keywords.stream()
                .map(keyword -> new KeywordMetadataDO()
                        .setKeyword(keyword)
                        .setMarketId(Long.valueOf(marketId))
                        .setStatus(KeywordMetadataStatusEnum.INIT.getCode()))
                .collect(Collectors.toList());

        // 插入数据
        keywrodMetadataMapper.insertBatch(keywordMetadataDOInitList);

        KeywordMinerReposeDTO keywordMinerReposeDTO;
        try {
            keywordMinerReposeDTO = sellerSpriteService.BatchKeywordMiner(keywords, marketId);


            List<ItemsDTO> items = keywordMinerReposeDTO.getItems();

            if (items.isEmpty()) {
                keywordMetadataDOInitList.stream().forEach(data -> data.setStatus(KeywordMetadataStatusEnum.NO_DATA.getCode()));
                keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList, keywordMetadataDOInitList.size());
                return AsyncResult.forValue(true);
            }

            for (KeywordMetadataDO keywordMetadataDO : keywordMetadataDOInitList) {
                // 查找匹配的ItemsDTO
                ItemsDTO matchingItem = items.stream()
                        .filter(item -> item.getKeyword().equals(keywordMetadataDO.getKeyword()))
                        .findFirst()
                        .orElse(null);

                if (matchingItem != null) {
                    // 进行对象转换
                    KeywordMetadataDO convertDO = KeywordMetadataConvert.INSTANCE.convert(matchingItem);
                    convertDO.setId(keywordMetadataDO.getId());
                    convertDO.setCreator(keywordMetadataDO.getCreator());
                    convertDO.setCreateTime(keywordMetadataDO.getCreateTime());
                    convertDO.setUpdater(keywordMetadataDO.getUpdater());
                    convertDO.setUpdateTime(keywordMetadataDO.getUpdateTime());
                    convertDO.setDeleted(keywordMetadataDO.getDeleted());
                    BeanUtil.copyProperties(convertDO,keywordMetadataDO);
                    // 匹配成功，设置状态为成功
                    keywordMetadataDO.setStatus(KeywordMetadataStatusEnum.SUCCESS.getCode());

                } else {
                    // 没有匹配，设置状态为失败
                    keywordMetadataDO.setStatus(KeywordMetadataStatusEnum.NO_DATA.getCode());
                }
            }

            keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList, keywordMetadataDOInitList.size());

            return AsyncResult.forValue(true);
        } catch (Exception e) {
            keywordMetadataDOInitList.stream().forEach(data -> data.setStatus(KeywordMetadataStatusEnum.ERROR.getCode()));
            keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList, keywordMetadataDOInitList.size());
            return AsyncResult.forValue(false);
        }


    }


    // 将一个列表拆分为多个子列表，每个子列表包含指定数量的元素
    private static <T> List<List<T>> splitList(List<T> list, int batchSize) {

        List<List<T>> subLists = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, list.size());
            subLists.add(list.subList(i, endIndex));
        }
        return subLists;
    }


    public static void main(String[] args) {
        String jsonString = "[GkDatasDTO(station=COM, asin=B0C6VQBSX6, asinImage=null, bigAsinImage=null, asinPrice=null, asinReviews=null, asinRating=null, asinBrand=null, asinTitle=, keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B08P1D991N, asinImage=null, bigAsinImage=null, asinPrice=null, asinReviews=null, asinRating=null, asinBrand=null, asinTitle=, keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B0912MRSDK, asinImage=null, bigAsinImage=null, asinPrice=null, asinReviews=null, asinRating=null, asinBrand=null, asinTitle=, keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B09Q5T4RTX, asinImage=null, bigAsinImage=null, asinPrice=null, asinReviews=null, asinRating=null, asinBrand=null, asinTitle=, keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B0C6VR3FCL, asinImage=null, bigAsinImage=null, asinPrice=null, asinReviews=null, asinRating=null, asinBrand=null, asinTitle=, keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B09Q1J6JHG, asinImage=https://m.media-amazon.com/images/I/41JIXk4iFTL._AC_SR200,200_.jpg, bigAsinImage=null, asinPrice=94.94, asinReviews=240, asinRating=3.8, asinBrand=null, asinTitle=Moto G Pure | 2021 | 2-Day battery | Unlocked | Made for US by Motorola | 3/32GB | 13MP Camera | Deep Indigo (Renewed), keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B07P6SWG7T, asinImage=https://m.media-amazon.com/images/I/31z+Ovy1i+L._AC_SR200,200_.jpg, bigAsinImage=null, asinPrice=134, asinReviews=13418, asinRating=4.3, asinBrand=null, asinTitle=Samsung Galaxy S10e, 128GB, Prism Black - GSM Carriers (Renewed), keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B09353YZR8, asinImage=https://m.media-amazon.com/images/I/41vNnn1VeYS._AC_SR200,200_.jpg, bigAsinImage=null, asinPrice=199.98, asinReviews=4535, asinRating=4.2, asinBrand=null, asinTitle=Samsung Galaxy S20 FE (5G) 256GB 6.5\" Display Unlocked - Cloud Navy (Renewed), keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B09ZQGFH52, asinImage=null, bigAsinImage=null, asinPrice=null, asinReviews=null, asinRating=null, asinBrand=null, asinTitle=, keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null), GkDatasDTO(station=COM, asin=B0BCG4JXWB, asinImage=null, bigAsinImage=null, asinPrice=null, asinReviews=null, asinRating=null, asinBrand=null, asinTitle=, keyword=null, categoryId=null, maxPage=null, asinUrl=null, rank=null, rankPage=null, rankPagesize=null, rankIndex=null, position=null, products=null, sku=null, maxRankPage=null, ad=null, amazonChoice=null, badges=null)]";

    }


}
