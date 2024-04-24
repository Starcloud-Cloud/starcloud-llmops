package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
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
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    @Resource
    private SqlSessionFactory sqlSessionFactory;


    /**
     * 查询-原数据根据关键词和站点关键词
     *
     * @param pageReqVO
     * @return
     */
    @Override
    @TenantIgnore
    public PageResult<KeywordMetadataRespVO> queryMetaData(QueryKeywordMetadataPageReqVO pageReqVO) {
        log.info("分页查询关键词元数据数据，查询参数是【{}】", JSONUtil.toJsonStr(pageReqVO));
        try {
            SellerSpriteMarketEnum.valueOf(pageReqVO.getMarketName());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("站点信息获取失败 请输入正确的站点信息");
        }
        Assert.isFalse(StrUtil.isBlank(pageReqVO.getMarketName()), "站点信息不可以为空");
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

        Assert.isFalse(CollUtil.isEmpty(keywordList), "关键词不可为空");
        // 关键词去重
        keywordList = CollUtil.distinct(keywordList);

        List<KeywordMetadataDO> keywordMetadataDOS = keywrodMetadataMapper.selectList(Wrappers.lambdaQuery(KeywordMetadataDO.class)
                .eq(KeywordMetadataDO::getMarketId, sellerSpriteMarketEnum.getCode())
                .in(KeywordMetadataDO::getKeyword, keywordList));

        // 获取列表中未同步错误或者没有数据的数据
        List<KeywordMetadataDO> retryDataLists = keywordMetadataDOS.stream().filter(obj -> obj.getStatus() == KeywordMetadataStatusEnum.ERROR.getCode() || obj.getStatus() == KeywordMetadataStatusEnum.NO_DATA.getCode()).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(retryDataLists)) {
            List<List<KeywordMetadataDO>> splitNotInKeywords = CollUtil.split(retryDataLists, 20);
            for (List<KeywordMetadataDO> splitNotInKeyword : splitNotInKeywords) {
                log.info("【关键词原数据更新】===》当前站点【{}】下关键词数据【{}】开始更新", marketName, keywordList.toString());
                getSelf().executeUpdateAsyncRequestData(splitNotInKeyword);
            }
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

        // 如果数据库中数据全部存在则直接返回
        if (notInKeywords.isEmpty()) {
            return true;
        }

        List<KeywordMetadataDO> keywordMetadataDOList = insertBatchData(notInKeywords, Long.valueOf(sellerSpriteMarketEnum.getCode()));

        List<List<KeywordMetadataDO>> split = CollUtil.split(keywordMetadataDOList, 20);

        // 等待所有任务完成
        List<Boolean> results = new ArrayList<>();
        List<ListenableFuture<Boolean>> futures = new ArrayList<>();
        split.forEach(keywordMetadataDOS1 ->
                futures.add(getSelf().executeAsyncRequestData(keywordMetadataDOS1)));

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

    private List<KeywordMetadataDO> insertBatchData(List<String> notInKeywords, Long marketId) {

        // 初始化数据
        List<KeywordMetadataDO> initDataList = notInKeywords.stream()
                .map(keyword -> new KeywordMetadataDO()
                        .setKeyword(keyword)
                        .setMarketId(marketId)
                        .setStatus(KeywordMetadataStatusEnum.INIT.getCode()))
                .collect(Collectors.toList());

        // 批量添加初始化数据
        MybatisBatch<KeywordMetadataDO> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, initDataList);
        MybatisBatch.Method<KeywordMetadataDO> method = new MybatisBatch.Method<>(KeywrodMetadataMapper.class);
        mybatisBatch.execute(method.insert());

        return initDataList;
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
                        KeywordMetadataDO::getPurchaseRate,
                        KeywordMetadataDO::getStatus
                ));
        List<KeywordMetadataRespVO> keywordMetadataRespVOS = KeywordMetadataConvert.INSTANCE.convertList(keywordMetadataDOS);
        return BeanUtil.copyToList(keywordMetadataRespVOS, KeywordMetadataBasicRespVO.class);
    }


    /**
     * 异步获取关键词数据
     *
     * @param keywordMetadataDOS 初始化的关键词数据
     */
    @TenantIgnore
    @Async
    public ListenableFuture<Boolean> executeAsyncRequestData(List<KeywordMetadataDO> keywordMetadataDOS) {

        log.info("初始化数据，开始从卖家精灵获取数据");
        List<String> keywords = keywordMetadataDOS.stream().map(KeywordMetadataDO::getKeyword).collect(Collectors.toList());

        Set<Long> marketIds = keywordMetadataDOS.stream().map(KeywordMetadataDO::getMarketId).collect(Collectors.toSet());


        KeywordMinerReposeDTO keywordMinerReposeDTO;
        try {
            keywordMinerReposeDTO = sellerSpriteService.BatchKeywordMiner(keywords, Math.toIntExact(marketIds.iterator().next()));


            List<ItemsDTO> items = keywordMinerReposeDTO.getItems();

            if (items.isEmpty()) {
                keywordMetadataDOS.stream().forEach(data -> data.setStatus(KeywordMetadataStatusEnum.NO_DATA.getCode()));
                keywrodMetadataMapper.updateBatch(keywordMetadataDOS, keywordMetadataDOS.size());
                log.warn("当前关键词【{}】未获取到关键词详细数据", keywords);
                return AsyncResult.forValue(true);
            }

            for (KeywordMetadataDO keywordMetadataDO : keywordMetadataDOS) {
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

            keywrodMetadataMapper.updateBatch(keywordMetadataDOS, keywordMetadataDOS.size());

            return AsyncResult.forValue(true);
        } catch (Exception e) {
            log.error("卖家精灵关键词【{}】获取失败，失败原因是:{}", keywords, e.getMessage(), e);
            keywordMetadataDOS.stream().forEach(data -> data.setStatus(KeywordMetadataStatusEnum.ERROR.getCode()));
            keywrodMetadataMapper.updateBatch(keywordMetadataDOS, keywordMetadataDOS.size());
            return AsyncResult.forValue(false);
        }


    }


    /**
     * 异步获取数据
     *
     * @param keywordMetadataDOS
     */
    @TenantIgnore
    @Async
    public void executeUpdateAsyncRequestData(List<KeywordMetadataDO> keywordMetadataDOS) {
        log.info("更新错误状态关键词，开始从卖家精灵获取数据");

        List<String> keywords = keywordMetadataDOS.stream().map(KeywordMetadataDO::getKeyword).collect(Collectors.toList());
        List<Long> marketIds = keywordMetadataDOS.stream().map(KeywordMetadataDO::getMarketId).collect(Collectors.toList());
        log.info("开始更新错误状态关键词,状态关键词为【{}】", keywords);

        keywordMetadataDOS.stream().forEach(keywordMetadataDO -> keywordMetadataDO.setStatus(KeywordMetadataStatusEnum.SYNCING.getCode()));

        keywrodMetadataMapper.updateBatch(keywordMetadataDOS, keywordMetadataDOS.size());

        KeywordMinerReposeDTO keywordMinerReposeDTO;
        try {
            keywordMinerReposeDTO = sellerSpriteService.BatchKeywordMiner(keywords, marketIds.get(0).intValue());


            List<ItemsDTO> items = keywordMinerReposeDTO.getItems();

            if (items.isEmpty()) {
                keywordMetadataDOS.stream().forEach(data -> data.setStatus(KeywordMetadataStatusEnum.NO_DATA.getCode()));
                keywrodMetadataMapper.updateBatch(keywordMetadataDOS, keywordMetadataDOS.size());
                log.warn("当前关键词【{}】未获取到关键词详细数据", keywords);
            }

            for (KeywordMetadataDO keywordMetadataDO : keywordMetadataDOS) {
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

            keywrodMetadataMapper.updateBatch(keywordMetadataDOS, keywordMetadataDOS.size());

        } catch (Exception e) {
            log.error("卖家精灵关键词【{}】获取失败，失败原因是:{}", keywords, e.getMessage(), e);
            keywordMetadataDOS.stream().forEach(data -> data.setStatus(KeywordMetadataStatusEnum.ERROR.getCode()));
            keywrodMetadataMapper.updateBatch(keywordMetadataDOS, keywordMetadataDOS.size());
        }


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


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private KeywordMetadataServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
