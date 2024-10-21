package com.starcloud.ops.business.log.service.conversation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationListReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS;

/**
 * 应用执行日志会话 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppConversationServiceImpl implements LogAppConversationService {

    @Resource
    private LogAppConversationMapper appConversationMapper;

    /**
     * 创建应用执行日志会话
     *
     * @param request 创建信息
     * @return 编号
     */
    @Override
    public Long createAppLogConversation(LogAppConversationCreateReqVO request) {
        LogAppConversationDO appConversation = LogAppConversationConvert.INSTANCE.convert(request);
        //手动设置，不走用户态
        appConversation.setCreator(request.getCreator());
        appConversation.setUpdater(request.getUpdater());
        appConversation.setDeptId(request.getDeptId());
        appConversation.setTenantId(request.getTenantId());
        appConversation.setCreateTime(LocalDateTime.now());
        appConversation.setUpdateTime(LocalDateTime.now());
        appConversationMapper.insert(appConversation);
        // 返回
        return appConversation.getId();
    }

    /**
     * 更新应用执行日志会话
     *
     * @param request 更新信息
     */
    @Override
    public void updateAppLogConversation(LogAppConversationUpdateReqVO request) {
        LogAppConversationDO updateObj = LogAppConversationConvert.INSTANCE.convert(request);
        updateObj.setUpdateTime(LocalDateTime.now());
        appConversationMapper.update(updateObj, Wrappers.lambdaQuery(LogAppConversationDO.class).eq(LogAppConversationDO::getUid, request.getUid()));
    }

    /**
     * 更新应用执行日志会话状态
     *
     * @param request 更新信息
     */
    @Override
    public void updateAppLogConversationStatus(LogAppConversationStatusReqVO request) {
        appConversationMapper.update(null, Wrappers.lambdaUpdate(LogAppConversationDO.class)
                .eq(LogAppConversationDO::getUid, request.getUid())
                .set(StringUtils.isNotBlank(request.getAiModel()), LogAppConversationDO::getAiModel, request.getAiModel())
                .set(LogAppConversationDO::getStatus, request.getStatus())
                .set(LogAppConversationDO::getErrorCode, request.getErrorCode())
                .set(LogAppConversationDO::getErrorMsg, request.getErrorMsg())
                .set(LogAppConversationDO::getUpdateTime, LocalDateTime.now()));
    }

    /**
     * 删除应用执行日志会话
     *
     * @param id 编号
     */
    @Override
    public void deleteAppLogConversation(Long id) {
        // 校验存在
        validateAppConversationExists(id);
        // 删除
        appConversationMapper.deleteById(id);
    }

    /**
     * 获得应用执行日志会话
     *
     * @param id 编号
     * @return 应用执行日志会话
     */
    @Override
    public LogAppConversationDO getAppLogConversation(Long id) {
        return appConversationMapper.selectById(id);
    }

    /**
     * 获得应用执行日志会话
     *
     * @param uid 编号
     * @return 应用执行日志会话
     */
    @Override
    public LogAppConversationDO getAppLogConversation(String uid) {
        return appConversationMapper.selectOne(LogAppConversationDO::getUid, uid);
    }

    /**
     * 获取用户最新会话
     *
     * @param appUid  应用uid
     * @param creator 用户uid
     * @return 应用执行日志会话
     */
    @Override
    public LogAppConversationDO getUserRecentlyConversation(String appUid, String creator, String scene) {
        LambdaQueryWrapper<LogAppConversationDO> wrapper = Wrappers.lambdaQuery(LogAppConversationDO.class)
                .eq(LogAppConversationDO::getAppUid, appUid)
                .eq(LogAppConversationDO::getCreator, creator)
                .eq(LogAppConversationDO::getFromScene, scene)
                .orderByDesc(LogAppConversationDO::getCreateTime)
                .last("limit 1");
        return appConversationMapper.selectOne(wrapper);
    }

    /**
     * 获得应用执行日志会话列表
     *
     * @param ids 编号
     * @return 应用执行日志会话列表
     */
    @Override
    public List<LogAppConversationDO> listAppLogConversation(Collection<Long> ids) {
        return appConversationMapper.selectBatchIds(ids);
    }

    /**
     * 获得应用执行日志会话列表
     *
     * @param query 查询条件
     * @return 应用执行日志会话列表
     */
    @Override
    public List<LogAppConversationDO> listAppLogConversation(LogAppConversationListReqVO query) {
        return appConversationMapper.selectList(query);
    }

    /**
     * 获得应用执行日志会话分页
     *
     * @param query 分页查询
     * @return 应用执行日志会话分页
     */
    @Override
    public PageResult<LogAppConversationDO> pageAppLogConversation(LogAppConversationPageReqVO query) {
        return appConversationMapper.selectPage(query);
    }

    /**
     * 根据 应用 UID 分页查询应用执行日志会话数据 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    @Override
    public PageResult<LogAppConversationInfoPO> pageLogAppConversation(AppLogConversationInfoPageUidReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());

        Page<LogAppConversationDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LogAppConversationInfoPO> infoPage = appConversationMapper.pageLogAppConversationByAppUid(page, query);
        return new PageResult<>(infoPage.getRecords(), infoPage.getTotal());
    }

    /**
     * 获取 应用执行分页信息
     *
     * @param query 分页查询
     * @return 应用执行日志会话分页
     */
    @Override
    public PageResult<LogAppConversationInfoPO> pageLogAppConversation(AppLogConversationInfoPageReqVO query) {
        // 日志时间类型
        LogTimeTypeEnum logTimeTypeEnum = IEnumable.nameOf(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType(), LogTimeTypeEnum.class);
        // 设置开始时间和结束时间
        query.setStartTime(logTimeTypeEnum.getStartTime());
        query.setEndTime(logTimeTypeEnum.getEndTime());

        Page<LogAppConversationDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        IPage<LogAppConversationInfoPO> infoPage = appConversationMapper.pageLogAppConversation(page, query);
        return new PageResult<>(infoPage.getRecords(), infoPage.getTotal());
    }

    /**
     * 校验应用执行日志会话是否存在
     *
     * @param id 应用执行日志会话编号
     */
    private void validateAppConversationExists(Long id) {
        if (appConversationMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
    }
}