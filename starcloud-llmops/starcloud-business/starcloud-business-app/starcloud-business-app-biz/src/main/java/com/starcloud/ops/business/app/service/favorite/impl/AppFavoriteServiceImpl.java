package com.starcloud.ops.business.app.service.favorite.impl;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoriteListReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoritePageReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.request.AppFavoriteCancelReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.request.AppFavoriteCreateReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.convert.favorite.AppFavoriteConvert;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoriteDO;
import com.starcloud.ops.business.app.dal.databoject.favorite.AppFavoritePO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.favorite.AppFavoriteMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.operate.AppOperateMapper;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.favorite.AppFavoriteTypeEnum;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.service.favorite.AppFavoriteService;
import com.starcloud.ops.business.app.util.CreativeUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
@Slf4j
@Service
public class AppFavoriteServiceImpl implements AppFavoriteService {

    @Resource
    private AppFavoriteMapper appFavoriteMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppOperateMapper appOperateMapper;

    /**
     * 获取用户收藏的应用的详情
     *
     * @param uid 应用 uid
     * @return 收藏应用
     */
    @Override
    public AppFavoriteRespVO getMarketInfo(String uid) {

        // 收藏 UID 非空校验
        AppValidate.notBlank(uid, ErrorCodeConstants.FAVORITE_UID_IS_REQUIRED);

        // 用户ID
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        AppFavoritePO favorite = appFavoriteMapper.getMarketInfo(uid);
        AppValidate.notNull(favorite, ErrorCodeConstants.FAVORITE_APP_NON_EXISTENT);

        AppFavoriteRespVO response = AppFavoriteConvert.INSTANCE.convert(favorite);

        // 操作表中插入一条查看记录, 并且增加查看量
        appOperateMapper.create(favorite.getUid(), favorite.getVersion(), AppOperateTypeEnum.VIEW.name(), String.valueOf(loginUserId));

        // 增加查看量
        Integer viewCount = favorite.getViewCount() + 1;
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate(AppMarketDO.class);
        updateWrapper.set(AppMarketDO::getViewCount, viewCount);
        // 更新时间保持不变
        updateWrapper.set(AppMarketDO::getUpdateTime, favorite.getUpdateTime());
        updateWrapper.eq(AppMarketDO::getUid, favorite.getUid());
        appMarketMapper.update(null, updateWrapper);

        // 转换并且返回应用数据
        response.setViewCount(viewCount);
        return response;
    }

    /**
     * 应用市场应用收藏列表
     *
     * @param query 搜索条件
     * @return 收藏列表
     */
    @Override
    public List<AppFavoriteRespVO> list(AppFavoriteListReqVO query) {
        // 获取当前登录用户并且校验
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        query.setUserId(String.valueOf(loginUserId));
        List<AppFavoritePO> list = appFavoriteMapper.list(query);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<AppFavoriteRespVO> responseList = AppFavoriteConvert.INSTANCE.convertList(list);
        if (CollectionUtils.isEmpty(responseList)) {
            return Collections.emptyList();
        }
        if (AppFavoriteTypeEnum.TEMPLATE_MARKET.name().equals(query.getType())) {
            for (AppFavoriteRespVO response : responseList) {
                String styleUid = response.getStyleUid();
                PosterStyleDTO style = CreativeUtils.getPosterStyleListByUid(styleUid, response);
                if (Objects.nonNull(style)) {
                    response.setStyle(CreativeUtils.getMarketStyle(style));
                    response.setWorkflowConfig(null);
                }
            }
        }
        return responseList;
    }

    /**
     * 应用市场应用收藏分页列表
     *
     * @param query 搜索条件
     * @return 收藏分页列表
     */
    @Override
    public PageResp<AppFavoriteRespVO> page(AppFavoritePageReqVO query) {
        // 获取当前登录用户并且校验
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        query.setUserId(String.valueOf(loginUserId));
        IPage<AppFavoritePO> page = appFavoriteMapper.page(PageUtil.page(query), query);
        return AppFavoriteConvert.INSTANCE.convertPage(page, query);
    }

    /**
     * 将应用加入到收藏夹
     *
     * @param request 请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(AppFavoriteCreateReqVO request) {
        // 应用市场应用 UID 非空校验
        String marketUid = request.getMarketUid();
        AppValidate.notBlank(marketUid, ErrorCodeConstants.MARKET_UID_REQUIRED);

        if (AppFavoriteTypeEnum.TEMPLATE_MARKET.name().equals(request.getType())) {
            AppValidate.notBlank(request.getStyleUid(), "风格UID不能为空！");
        }

        // 校验要收藏的应用是否存在。
        AppMarketDO appMarket = appMarketMapper.get(marketUid, Boolean.TRUE);
        AppValidate.notNull(appMarket, ErrorCodeConstants.MARKET_APP_NON_EXISTENT, marketUid);

        // 用户ID
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        // 校验应用是否已经收藏
        AppFavoriteDO appFavorite = appFavoriteMapper.get(marketUid, String.valueOf(loginUserId), request.getType());
        AppValidate.isNull(appFavorite, ErrorCodeConstants.FAVORITE_APP_ALREADY_EXISTS, marketUid);

        // 保存收藏的应用
        AppFavoriteDO favorite = AppFavoriteConvert.INSTANCE.convertRequest(request, String.valueOf(loginUserId));
        appFavoriteMapper.insert(favorite);

        // 操作记录加一条记录
        appOperateMapper.create(appMarket.getUid(), appMarket.getVersion(), AppOperateTypeEnum.LIKE.name(), String.valueOf(loginUserId));

        // 更新应用的收藏数量
        LambdaUpdateWrapper<AppMarketDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(AppMarketDO::getUid, marketUid);
        updateWrapper.set(AppMarketDO::getLikeCount, appMarket.getLikeCount() + 1);
        updateWrapper.set(AppMarketDO::getUpdateTime, appMarket.getUpdateTime());
        appMarketMapper.update(null, updateWrapper);
    }

    /**
     * 取消收藏
     *
     * @param request 请求参数
     */
    @Override
    public void cancel(AppFavoriteCancelReqVO request) {
        // 应用市场应用 UID 非空校验
        String marketUid = request.getMarketUid();
        AppValidate.notBlank(marketUid, ErrorCodeConstants.MARKET_UID_REQUIRED);

        // 用户ID
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);

        // 删除收藏的应用
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getMarketUid, marketUid);
        wrapper.eq(AppFavoriteDO::getCreator, loginUserId);
        wrapper.eq(AppFavoriteDO::getType, request.getType());
        if (AppFavoriteTypeEnum.TEMPLATE_MARKET.name().equals(request.getType())) {
            AppValidate.notBlank(request.getStyleUid(), "风格UID不能为空！");
            wrapper.eq(AppFavoriteDO::getStyleUid, request.getStyleUid());
        }
        appFavoriteMapper.delete(wrapper);
    }

    /**
     * 取消收藏
     *
     * @param uid 应用 uid
     */
    @Override
    public void delete(String uid) {
        LambdaQueryWrapper<AppFavoriteDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(AppFavoriteDO::getUid, uid);
        wrapper.eq(AppFavoriteDO::getDeleted, Boolean.FALSE);
        appFavoriteMapper.delete(wrapper);
    }

}
