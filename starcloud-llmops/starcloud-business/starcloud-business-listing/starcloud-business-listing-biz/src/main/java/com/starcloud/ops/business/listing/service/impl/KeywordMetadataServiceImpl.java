package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
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
    public PageResult<KeywordMetadataRespVO> queryMetaData(QueryKeywordMetadataPageReqVO pageReqVO) {
         keywrodMetadataMapper.selectPage(pageReqVO);

        return null;
    }

    /**
     * 新增原数据 -根据关键词和站点关键词
     *
     * @param keywordList 关键词
     * @param marketName
     * @return
     */
    @Override
    public Boolean addMetaData(List<String> keywordList, String marketName) {
        // 站点数据转换
        SellerSpriteMarketEnum sellerSpriteMarketEnum = SellerSpriteMarketEnum.valueOf(marketName);

        List<KeywordMetadataDO> keywordMetadataDOS = keywrodMetadataMapper.selectList(Wrappers.lambdaQuery(KeywordMetadataDO.class)
                .eq(KeywordMetadataDO::getMarketId, sellerSpriteMarketEnum.getCode())
                .in(KeywordMetadataDO::getKeywords, keywordList));

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
                    .map(KeywordMetadataDO::getKeywords)
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

        return  results.contains(false);
    }

    /**
     * 异步同步
     *
     * @param keywords
     * @param marketId
     */
    @Async
    public ListenableFuture<Boolean> executeAsyncRequestData(List<String> keywords,Integer marketId) {
                // 初始化不存在数据
        List<KeywordMetadataDO> keywordMetadataDOInitList = keywords.stream()
                .map(keyword -> new KeywordMetadataDO()
                        .setKeywords(keyword)
                        .setMarketId(Long.valueOf(marketId))
                        .setStatus(KeywordMetadataStatusEnum.INIT.getCode()))
                .collect(Collectors.toList());

        // 插入数据
        keywrodMetadataMapper.insertBatch(keywordMetadataDOInitList);

        KeywordMinerReposeDTO keywordMinerReposeDTO;
        try {
            keywordMinerReposeDTO = sellerSpriteService.BatchKeywordMiner(keywords, marketId);


            List<ItemsDTO> items = keywordMinerReposeDTO.getItems();

            if (items.isEmpty()){
                keywordMetadataDOInitList.stream().forEach(data->data.setStatus(KeywordMetadataStatusEnum.NO_DATA.getCode()));
                keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList,keywordMetadataDOInitList.size());
                return AsyncResult.forValue(true);
            }

            for (KeywordMetadataDO keywordMetadataDO : keywordMetadataDOInitList) {
                // 查找匹配的ItemsDTO
                ItemsDTO matchingItem = items.stream()
                        .filter(item -> item.getKeywords().equals(keywordMetadataDO.getKeywords()))
                        .findFirst()
                        .orElse(null);

                if (matchingItem != null) {
                    // 进行对象转换
                    BeanUtil.copyProperties(matchingItem,keywordMetadataDO);
                    // 匹配成功，设置状态为成功
                    keywordMetadataDO.setStatus(KeywordMetadataStatusEnum.SUCCESS.getCode());

                } else {
                    // 没有匹配，设置状态为失败
                    keywordMetadataDO.setStatus(KeywordMetadataStatusEnum.NO_DATA.getCode());
                }
            }

            keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList,keywordMetadataDOInitList.size());

            return AsyncResult.forValue(true);
        } catch (Exception e) {
            keywordMetadataDOInitList.stream().forEach(data->data.setStatus(KeywordMetadataStatusEnum.ERROR.getCode()));
            keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList,keywordMetadataDOInitList.size());
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


}
