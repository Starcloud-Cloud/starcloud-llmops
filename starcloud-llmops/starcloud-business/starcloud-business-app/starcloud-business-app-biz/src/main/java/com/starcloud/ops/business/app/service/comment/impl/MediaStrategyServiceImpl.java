package com.starcloud.ops.business.app.service.comment.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import com.starcloud.ops.business.app.controller.admin.comment.strategy.vo.MediaStrategyPageReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.strategy.vo.MediaStrategySaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaStrategyDO;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaStrategyMapper;
import com.starcloud.ops.business.app.enums.comment.ExecuteTypeEnum;
import com.starcloud.ops.business.app.enums.comment.KeywordMatchTypeEnum;
import com.starcloud.ops.business.app.service.comment.MediaCommentsActionService;
import com.starcloud.ops.business.app.service.comment.MediaStrategyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MEDIA_STRATEGY_NOT_EXISTS;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MEDIA_STRATEGY_SAME_EXISTS;

/**
 * 媒体回复策略 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class MediaStrategyServiceImpl implements MediaStrategyService {

    @Resource
    private MediaCommentsActionService mediaCommentsActionService;

    @Resource
    private MediaStrategyMapper mediaStrategyMapper;

    @Override
    public Long createMediaStrategy(Long userId, MediaStrategySaveReqVO createReqVO) {
        // 插入
        MediaStrategyDO mediaStrategy = BeanUtils.toBean(createReqVO, MediaStrategyDO.class);
        // 验证存在相同产品下的策略 防止命中多条策略
        if (validateStrategyIsContradict(userId, mediaStrategy)) {
            throw exception(MEDIA_STRATEGY_SAME_EXISTS);
        }
        mediaStrategyMapper.insert(mediaStrategy);
        // 返回
        return mediaStrategy.getId();
    }

    /**
     * 验证策略是否 【相悖】 相悖条件是 同一媒体下
     *
     * @param mediaStrategy 新增策略数据
     */
    private Boolean validateStrategyIsContradict(Long userId, MediaStrategyDO mediaStrategy) {
        MediaStrategyDO mediaStrategyDO = mediaStrategyMapper.selectOneStatusEnableParams(userId, mediaStrategy.getAssignAccount(), mediaStrategy.getAssignMedia(), null, CommonStatusEnum.ENABLE.getStatus());
        if (Objects.isNull(mediaStrategyDO)) {
            return false;
        }
        return true;
    }

    @Override
    public void updateMediaStrategy(Long userId, MediaStrategySaveReqVO updateReqVO) {
        // 校验存在
        validateMediaStrategyExists(userId, updateReqVO.getId());
        // 更新
        MediaStrategyDO updateObj = BeanUtils.toBean(updateReqVO, MediaStrategyDO.class);
        mediaStrategyMapper.updateById(updateObj);
    }

    @Override
    public void deleteMediaStrategy(Long userId, Long id) {
        // 校验存在
        validateMediaStrategyExists(userId, id);
        // 删除
        mediaStrategyMapper.deleteById(id);
    }

    private void validateMediaStrategyExists(Long userId, Long id) {
        if (mediaStrategyMapper.selectOneByUserId(userId, id) == null) {
            throw exception(MEDIA_STRATEGY_NOT_EXISTS);
        }
    }

    @Override
    public MediaStrategyDO getMediaStrategy(Long userId, Long id) {
        return mediaStrategyMapper.selectOneByUserId(userId, id);
    }

    @Override
    public PageResult<MediaStrategyDO> getMediaStrategyPage(Long userId, MediaStrategyPageReqVO pageReqVO) {
        return mediaStrategyMapper.selectPage(userId, pageReqVO);
    }

    /**
     * @param userId          用户 ID
     * @param commentsId      评论 ID
     * @param accountCode     发布人账号 Code
     * @param mediaCode       媒体 Code
     * @param commentUserCode 评论人账户 Code
     * @param commentCode     评论 Code
     * @param commentContent  评论内容
     */
    @Override
    public void validateMediaCommentsMatch(Long userId, Long commentsId, String accountCode, String mediaCode, String commentUserCode, String commentCode, String commentContent) {
        // 1.0 验证生效时间 - 获取用户下当前时间下所有启用的配置 - 验证账号 - 验证作品
        MediaStrategyDO mediaStrategyDOS = mediaStrategyMapper.selectOneStatusEnableParams(userId, accountCode, mediaCode, LocalDateTime.now().toLocalTime(), CommonStatusEnum.ENABLE.getStatus());
        if (Objects.isNull(mediaStrategyDOS)) {
            return;
        }

        // 获取策略下单位时间内的成功数
        int successesNumPerUnitTime = mediaCommentsActionService.selectCountByStrategyAndPerUnitTime(userId, mediaStrategyDOS.getActionType(), mediaStrategyDOS.getFrequency());

        // 2.0 验证回复频率
        if (mediaStrategyDOS.getFrequency() <= successesNumPerUnitTime) {
            return;
        }

        // 3.0 验证关键词匹配
        Boolean KeywordMatchResult = validateKeyWordMatch(mediaStrategyDOS.getKeywordMatchType(), mediaStrategyDOS.getKeywordGroups(), commentContent);

        if (!KeywordMatchResult) {
            return;
        }
        Collections.shuffle(mediaStrategyDOS.getKeywordGroups());

        // 生成具体操作 存入操作表
        mediaCommentsActionService.createMediaCommentsAction(userId, commentUserCode, commentsId, mediaStrategyDOS.getId(), mediaStrategyDOS.getActionType(), ExecuteTypeEnum.AUTO.getCode(), mediaStrategyDOS.getKeywordGroups().get(new Random().nextInt(mediaStrategyDOS.getKeywordGroups().size())), LocalDateTime.now().plusSeconds(mediaStrategyDOS.getInterval()));
    }

    /**
     * 更新策略状态
     *
     * @param userId 用户编号
     * @param status 状态
     */
    @Override
    public void updateMediaStrategyStatus(Long userId, Long strategyId, Integer status) {
        MediaStrategyDO mediaStrategy = getMediaStrategy(userId, strategyId);
        // 校验是否可以更新
        validateStrategyIsContradict(userId, mediaStrategy);

        // 更新状态
        MediaStrategyDO updateObj = new MediaStrategyDO().setId(strategyId).setStatus(status);
        mediaStrategyMapper.updateById(updateObj);
    }

    /**
     * 验证关键词匹配
     *
     * @param keywordMatchType 关键词匹配类型
     * @param keywordGroups    关键词组
     * @param commentContent   评论内容
     * @return 是否匹配
     */
    private Boolean validateKeyWordMatch(Integer keywordMatchType, List<String> keywordGroups, String commentContent) {
        KeywordMatchTypeEnum matchTypeEnum = KeywordMatchTypeEnum.getByCode(keywordMatchType);
        switch (matchTypeEnum) {
            case NO_MATCH:
                return true;
            case ANY_ONE:
                return StrUtil.containsAny(commentContent, (CharSequence) keywordGroups);
            case CONTAIN_ALL:
                return StrUtil.containsAll(commentContent, (CharSequence) keywordGroups);
            case CONTAIN_NO:
                return !StrUtil.containsAll(commentContent, (CharSequence) keywordGroups);
            case EXACT_MATCH:
                return StrUtil.equals(commentContent, (CharSequence) keywordGroups);
            default:
                return false;
        }
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private MediaStrategyServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}