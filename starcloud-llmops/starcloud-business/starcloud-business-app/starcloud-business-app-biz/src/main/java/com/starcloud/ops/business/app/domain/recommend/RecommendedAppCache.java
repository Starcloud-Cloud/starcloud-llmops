package com.starcloud.ops.business.app.domain.recommend;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 项目推荐应用缓存，将项目推荐应用缓存到本地缓存中
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-28
 */
@Slf4j
public class RecommendedAppCache {

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
     * 获取推荐应用详情
     *
     * @param code 应用uid
     * @return 应用详情
     */
    public static AppRespVO getRecommendApp(String code) {
        Optional<AppRespVO> appOptional = get().stream().filter(app -> app.getRecommend().equals(code)).findFirst();
        if (appOptional.isPresent()) {
            return appOptional.get();
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_NO_EXISTS_UID, code);
    }

    /**
     * 获取推荐应用列表
     *
     * @return 推荐应用列表
     */
    public static List<AppRespVO> initRecommendedApps() {
        return Arrays.asList(
                // 生成文本应用
                RecommendedAppFactory.defGenerateTextApp(),
                // 生成文章应用
                RecommendedAppFactory.defGenerateArticleApp()
        );
    }

}
