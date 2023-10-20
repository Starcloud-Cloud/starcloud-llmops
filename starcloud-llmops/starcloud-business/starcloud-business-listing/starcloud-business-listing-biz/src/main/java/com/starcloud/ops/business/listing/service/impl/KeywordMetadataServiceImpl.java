package com.starcloud.ops.business.listing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.dal.dataobject.KeywordMetadataDO;
import com.starcloud.ops.business.listing.dal.mysql.KeywrodMetadataMapper;
import com.starcloud.ops.business.listing.enums.SellerSpriteMarketEnum;
import com.starcloud.ops.business.listing.service.KeywordMetadataService;
import com.starcloud.ops.business.listing.service.sellersprite.SellerSpriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
//        Assert.isFalse(CollUtil.isEmpty(keywordList), "根据关键词获取原数据信息失败，关键词集合不可以为空");
//        Assert.notBlank(marketName, "根据关键词获取原数据信息失败，站点信息不可以为空");
//        SellerSpriteMarkEnum sellerSpriteMarkEnum = SellerSpriteMarkEnum.valueOf(marketName);
//        // 从数据库获取数据信息
//        List<KeywordMetadataDO> keywordMetadataDOS = keywrodMetadataMapper.selectList(Wrappers.lambdaQuery(KeywordMetadataDO.class)
//                .eq(KeywordMetadataDO::getMarketId, sellerSpriteMarkEnum.getCode())
//                .in(KeywordMetadataDO::getKeywords, keywordList));
//        List<KeywordMetadataRespVO> keywordMetadataRespVOS = new ArrayList<>();
//
//        // 如果数据库中存在 返回数据
//        if (keywordMetadataDOS.isEmpty()) {
//
//            keywordMetadataDOS.forEach(data ->
//            {
//                String jsonStr = JSONUtil.toJsonStr(data);
//                KeywordMetadataRespVO bean = JSONUtil.toBean(jsonStr, KeywordMetadataRespVO.class);
//                keywordMetadataRespVOS.add(bean);
//            });
//
//            return null;
//        }
//        // 如果数据库中存在 返回数据
//        if (!keywordMetadataDOS.isEmpty() && keywordList.size() == keywordMetadataDOS.size()) {
//
//            keywordMetadataDOS.forEach(data ->
//            {
//                String jsonStr = JSONUtil.toJsonStr(data);
//                KeywordMetadataRespVO bean = JSONUtil.toBean(jsonStr, KeywordMetadataRespVO.class);
//                keywordMetadataRespVOS.add(bean);
//            });
//
//            return null;
//        }
//
//        if (CollUtil.isNotEmpty(keywordMetadataDOS)) {
//            // 获取不在列表的数据
//            notInKeywords = keywordMetadataDOS.stream()
//                    .map(KeywordMetadataDO::getKeywords)
//                    .filter(keyword -> !keywordList.contains(keyword))
//                    .collect(Collectors.toList());
//        } else {
//            notInKeywords = keywordList;
//        }
//
//        // 如果数据库中信息为空或者与关键词数量不等 则调用卖家精灵接口查询
//        KeywordMinerReposeDTO keywordMinerReposeDTO = sellerSpriteService.BatchKeywordMiner(notInKeywords, sellerSpriteMarkEnum.getCode());
//        // 数据转换
//        List<ItemsDTO> items = keywordMinerReposeDTO.getItems();
//        List<KeywordMetadataDO> metadataList = items.stream()
//                .map(item -> new KeywordMetadataDO(item))
//                .collect(Collectors.toList());
//
//        // 插入数据
//        keywrodMetadataMapper.insertBatch(metadataList);
//
//        // 返回 数据
//        keywordMetadataDOS.addAll(metadataList);
//
//        keywordMetadataDOS.forEach(data ->
//        {
//            String jsonStr = JSONUtil.toJsonStr(data);
//            KeywordMetadataRespVO bean = JSONUtil.toBean(jsonStr, KeywordMetadataRespVO.class);
//            keywordMetadataRespVOS.add(bean);
//        });
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

        List<KeywordMetadataRespVO> keywordMetadataRespVOS = new ArrayList<>();


        // 如果数据库中数据全部存在则直接返回
        if (!keywordMetadataDOS.isEmpty() && keywordList.size() == keywordMetadataDOS.size()) {
            log.info("【关键词原数据新增】===》当前站点【{}】下关键词数据【{}】全部存在,直接返回",marketName,keywordList.toString());
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
        // 从数据库获取数据信息

        // 异步处理


        return null;
    }

    /**
     * 异步同步
     * @param keywordMetadataDO
     */
    @Async
    public void asyncRequestData(KeywordMetadataDO keywordMetadataDO){


    }


}
