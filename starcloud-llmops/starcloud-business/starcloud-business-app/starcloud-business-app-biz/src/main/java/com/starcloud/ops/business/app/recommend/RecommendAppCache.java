package com.starcloud.ops.business.app.recommend;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.util.PinyinCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 项目推荐应用缓存，将项目推荐应用缓存到本地缓存中
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-28
 */
@Slf4j
public class RecommendAppCache {

    /**
     * 推荐应用缓存 key
     */
    public static final String CACHE_KEY = "RECOMMENDED_APPS";

    /**
     * 项目推荐应用缓存, 1小时过期
     */
    private static final Cache<String, List<AppRespVO>> RECOMMENDED_APPS_CACHE = CacheUtil.newTimedCache(1000 * 60 * 60);

    /**
     * 获取推荐应用列表，如果本地缓存中没有，则初始化推荐应用列表，并且缓存到本地缓存中
     *
     * @return 推荐应用列表
     */
    public static List<AppRespVO> get() {
        if (RECOMMENDED_APPS_CACHE.containsKey(CACHE_KEY)) {
            return RECOMMENDED_APPS_CACHE.get(CACHE_KEY);
        }
        List<AppRespVO> apps = initRecommendedApps();
        RECOMMENDED_APPS_CACHE.put(CACHE_KEY, apps);
        return apps;
    }

    /**
     * 获取推荐应用列表
     *
     * @param model 应用模型
     * @return 推荐应用列表
     */
    public static List<AppRespVO> get(String model) {
        if (StringUtils.isNotBlank(model) && AppModelEnum.CHAT.name().equals(model)) {
            return get().stream().filter(app -> AppModelEnum.CHAT.name().equals(app.getModel()))
                    .peek(item -> {
                        item.setSpell(PinyinCache.get(item.getName()));
                        item.setSpellSimple(PinyinCache.getSimple(item.getName()));
                    })
                    .collect(Collectors.toList());
        }
        return get().stream().filter(app -> !AppModelEnum.CHAT.name().equals(app.getModel()))
                .peek(item -> {
                    item.setSpell(PinyinCache.get(item.getName()));
                    item.setSpellSimple(PinyinCache.getSimple(item.getName()));
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取推荐应用详情
     *
     * @param code 应用uid
     * @return 应用详情
     */
    public static AppRespVO getRecommendApp(String code) {
        Optional<AppRespVO> appOptional = get().stream().filter(app -> app.getUid().equals(code)).findAny();
        if (appOptional.isPresent()) {
            return appOptional.get();
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NON_EXISTENT, code);
    }

    /**
     * 获取推荐应用列表
     *
     * @return 推荐应用列表
     */
    public static List<AppRespVO> initRecommendedApps() {
        List<AppRespVO> appRespVOS = new ArrayList<AppRespVO>() {
            {
                super.add(RecommendAppFactory.defGenerateTextApp());
                super.add(RecommendAppFactory.defGenerateArticleApp());
            }
        };
        AppMarketMapper marketMapper = SpringUtil.getBean(AppMarketMapper.class);
        List<AppMarketDO> appMarketDOS = marketMapper.listChatMarketApp();
        if (CollectionUtil.isNotEmpty(appMarketDOS)) {
            appRespVOS.addAll(AppMarketConvert.INSTANCE.convert(appMarketDOS));
        }
        return appRespVOS;
    }

}
