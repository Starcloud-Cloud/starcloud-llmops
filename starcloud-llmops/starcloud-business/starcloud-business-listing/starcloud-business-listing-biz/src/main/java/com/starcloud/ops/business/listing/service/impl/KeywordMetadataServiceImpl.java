package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataBasicRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.convert.KeywordMetadataConvert;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.dal.mysql.KeywrodMetadataMapper;
import com.starcloud.ops.business.listing.enums.KeywordMetadataStatusEnum;
import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.business.listing.service.KeywordMetadataService;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ItemsDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.KeywordMinerReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.SellerSpriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
        SellerSpriteMarketEnum sellerSpriteMarketEnum = getMarketInfo(marketName);

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

            List<String> inKeywords = keywordMetadataDOS.stream().map(KeywordMetadataDO::getKeyword).collect(Collectors.toList());
            // 获取不在列表的数据
            notInKeywords = keywordList.stream()
                    .filter(keyword -> !inKeywords.contains(keyword))
                    .collect(Collectors.toList());

        } else {
            notInKeywords = keywordList;
        }

        List<List<String>> splitNotInKeywords = CollUtil.split(notInKeywords, 20);
        // 等待所有任务完成
        List<Boolean> results = new ArrayList<>();
        List<ListenableFuture<Boolean>> futures = new ArrayList<>();
        for (List<String> splitNotInKeyword : splitNotInKeywords) {
            ListenableFuture<Boolean> booleanListenableFuture = this.executeAsyncRequestData(splitNotInKeyword, sellerSpriteMarketEnum.getCode());
            futures.add(booleanListenableFuture);
        }

        for (ListenableFuture<Boolean> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                // 处理异常
                e.printStackTrace();
                results.add(false);
            }
        }

        return !results.contains(false);
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
    public List<KeywordMetadataBasicRespVO> getKeywordsBasic(List<String> keywordList, String marketName) {
        Assert.isFalse(CollUtil.isEmpty(keywordList), "关键词列表不能为空");
        SellerSpriteMarketEnum marketInfo = getMarketInfo(marketName);
        List<KeywordMetadataDO> keywordMetadataDOS = keywrodMetadataMapper.selectList(Wrappers.lambdaQuery(KeywordMetadataDO.class)
                .eq(KeywordMetadataDO::getMarketId, marketInfo.getCode())
                .in(KeywordMetadataDO::getKeyword, keywordList)
                .select(KeywordMetadataDO::getId,
                        KeywordMetadataDO::getMarketId,
                        KeywordMetadataDO::getKeyword,
                        KeywordMetadataDO::getSearches,
                        KeywordMetadataDO::getPurchases,
                        KeywordMetadataDO::getPurchaseRate
                ));
        List<KeywordMetadataRespVO> keywordMetadataRespVOS = KeywordMetadataConvert.INSTANCE.convertList(keywordMetadataDOS);
        return BeanUtil.copyToList(keywordMetadataRespVOS, KeywordMetadataBasicRespVO.class);
    }

    /**
     * 新增原数据 -根据关键词和站点关键词
     *
     * @param asin       关键词
     * @param marketName
     * @return
     */
    @Override
    public SellerSpriteListingVO getListingByAsin(String asin, String marketName) {
        Assert.notBlank(asin, "ASIN 不可以为空");
        SellerSpriteMarketEnum marketInfo = getMarketInfo(marketName);
        return sellerSpriteService.getListingByAsin(asin, marketInfo.getCode());
    }

    /**
     * 根据 ASIN获取变体
     *
     * @param prepareRequestDTO
     * @return
     */
    @Override
    public PrepareReposeDTO extendPrepare(PrepareRequestDTO prepareRequestDTO) {
        Assert.notNull(prepareRequestDTO, "根据 ASIN获取变体失败，请求对象不可为空");
        return sellerSpriteService.extendPrepare(prepareRequestDTO);
    }

    /**
     * 根据 ASIN获取关键词拓展数据
     *
     * @param extendAsinRequestDTO
     * @return
     */
    @Override
    public ExtendAsinReposeDTO extendAsin(ExtendAsinRequestDTO extendAsinRequestDTO) {
        Assert.notNull(extendAsinRequestDTO, "根据 ASIN获取变体失败，请求对象不可为空");
        return sellerSpriteService.extendAsin(extendAsinRequestDTO);
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


    private SellerSpriteMarketEnum getMarketInfo(String marketName) {
        Assert.notBlank(marketName, "卖家精灵站点数据不可以为空，数据请求失败");
        try {
            return SellerSpriteMarketEnum.valueOf(marketName);
        } catch (Exception e) {
            throw new RuntimeException("卖家精灵站点数据不存在，数据请求失败", e);
        }

    }
    // 站点数据转换


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
