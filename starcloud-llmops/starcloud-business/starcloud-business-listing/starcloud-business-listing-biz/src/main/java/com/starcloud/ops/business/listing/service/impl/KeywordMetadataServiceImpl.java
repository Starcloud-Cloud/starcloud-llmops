package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import com.starcloud.ops.business.listing.service.KeyWordMetadataRepository;
import com.starcloud.ops.business.listing.service.KeywordMetadataService;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.SellerSpriteService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
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

    @Resource
    private KeyWordMetadataRepository keyWordMetadataRepository;


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
        Assert.isFalse(StrUtil.isBlank(pageReqVO.getMarketName()),"站点信息不可以为空");
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


        // 如果数据库中数据全部存在则直接返回
        if (!keywordMetadataDOS.isEmpty() && keywordList.size() == keywordMetadataDOS.size()) {
            log.info("【关键词原数据新增】===》当前站点【{}】下关键词数据【{}】全部存在,直接返回", marketName, keywordList.toString());
            return true;
        }
        //获取列表中未同步错误或者没有数据的数据
        List<KeywordMetadataDO> retryDatas = keywordMetadataDOS.stream().filter(obj -> obj.getStatus() == KeywordMetadataStatusEnum.ERROR.getCode()||obj.getStatus() == KeywordMetadataStatusEnum.NO_DATA.getCode()).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(retryDatas)){
            List<List<KeywordMetadataDO>> splitNotInKeywords = CollUtil.split(retryDatas, 20);
            for (List<KeywordMetadataDO> splitNotInKeyword : splitNotInKeywords) {
                keyWordMetadataRepository.executeAsyncRequestData(splitNotInKeyword);
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

        List<List<String>> splitNotInKeywords = CollUtil.split(notInKeywords, 20);
        // 等待所有任务完成
        List<Boolean> results = new ArrayList<>();
        List<ListenableFuture<Boolean>> futures = new ArrayList<>();
        for (List<String> splitNotInKeyword : splitNotInKeywords) {
            ListenableFuture<Boolean> booleanListenableFuture = keyWordMetadataRepository.executeAsyncRequestData(splitNotInKeyword, sellerSpriteMarketEnum.getCode());
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
                        KeywordMetadataDO::getPurchaseRate,
                        KeywordMetadataDO::getStatus
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
