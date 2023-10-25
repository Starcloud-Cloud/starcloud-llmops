package com.starcloud.ops.business.listing.service;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.starcloud.ops.business.listing.convert.KeywordMetadataConvert;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.dal.mysql.KeywrodMetadataMapper;
import com.starcloud.ops.business.listing.enums.KeywordMetadataStatusEnum;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ItemsDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.SellerSpriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 */
@Slf4j
@Repository
public class KeyWordMetadataRepository {

    @Resource
    private SellerSpriteService sellerSpriteService;

    @Resource
    private KeywrodMetadataMapper keywrodMetadataMapper;


    /**
     * 异步同步
     *
     * @param keywords
     * @param marketId
     */
    @TenantIgnore
    @Async
    public ListenableFuture<Boolean> executeAsyncRequestData(List<String> keywords, Integer marketId) {
        log.info("初始化数据，开始从卖家精灵获取数据");
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
                log.warn("当前关键词【{}】未获取到关键词详细数据", keywords);
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
                    BeanUtil.copyProperties(convertDO, keywordMetadataDO);
                    // 匹配成功，设置状态为成功
                    keywordMetadataDO.setStatus(KeywordMetadataStatusEnum.SUCCESS.getCode());

                } else {
                    log.warn("当前关键词【{}】未获取到关键词详细数据", keywordMetadataDO.getKeyword());
                    // 没有匹配，设置状态为失败
                    keywordMetadataDO.setStatus(KeywordMetadataStatusEnum.NO_DATA.getCode());
                }
            }

            keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList, keywordMetadataDOInitList.size());

            return AsyncResult.forValue(true);
        } catch (Exception e) {
            log.error("卖家精灵关键词【{}】获取失败，失败原因是:{}", keywords, e.getMessage(), e);
            keywordMetadataDOInitList.stream().forEach(data -> data.setStatus(KeywordMetadataStatusEnum.ERROR.getCode()));
            keywrodMetadataMapper.updateBatch(keywordMetadataDOInitList, keywordMetadataDOInitList.size());
            return AsyncResult.forValue(false);
        }


    }

}
