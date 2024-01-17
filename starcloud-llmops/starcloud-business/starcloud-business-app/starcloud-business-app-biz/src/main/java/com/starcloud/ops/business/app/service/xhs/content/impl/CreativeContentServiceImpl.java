package com.starcloud.ops.business.app.service.xhs.content.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeQueryReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeListOptionRespVO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentBusinessPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDTO;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeExecuteManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CREATIVE_CONTENT_CLAIMED;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CREATIVE_CONTENT_GREATER_RETRY;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CREATIVE_CONTENT_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECTURE_ERROR;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Service
@Slf4j
public class CreativeContentServiceImpl implements CreativeContentService {

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private CreativeExecuteManager xlsCreativeExecuteManager;


    @Resource
    private DictDataService dictDataService;

    @Resource
    @Lazy
    private CreativePlanService creativePlanService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(List<CreativeContentCreateReqVO> createReqs) {

        for (CreativeContentCreateReqVO createReq : createReqs) {
            CreativeContentDO contentDO = CreativeContentConvert.INSTANCE.convert(createReq);
            contentDO.setUid(IdUtil.fastSimpleUUID());
            contentDO.setStatus(CreativeContentStatusEnum.INIT.getCode());
            creativeContentMapper.insert(contentDO);
        }
    }

    @Override
    public Map<Long, Boolean> execute(List<Long> ids, String type, Boolean force) {
        log.info("开始执行 {} 任务 {}, {}, {}", type, ids, TenantContextHolder.isIgnore(), TenantContextHolder.getTenantId());
        try {
            List<CreativeContentDO> contentList = creativeContentMapper.selectBatchIds(ids)
                    .stream().filter(content -> !CreativeContentStatusEnum.EXECUTING.getCode().equals(content.getStatus())).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(contentList)) {
                return Collections.emptyMap();
            }

            if (CreativeContentTypeEnum.COPY_WRITING.getCode().equalsIgnoreCase(type)) {
                return xlsCreativeExecuteManager.executeCopyWriting(contentList, force);
            } else if (CreativeContentTypeEnum.PICTURE.getCode().equalsIgnoreCase(type)) {
                return xlsCreativeExecuteManager.executePicture(contentList, force);
            } else if (CreativeContentTypeEnum.ALL.getCode().equalsIgnoreCase(type)) {
                return xlsCreativeExecuteManager.executeAppALl(contentList, force);
            } else {
                log.error("不支持的任务类型 {}", type);
            }
        } catch (Exception e) {
            log.error("执行失败: {}", ids, e);
        }
        return Collections.emptyMap();
    }

    @Override
    public CreativeContentRespVO retry(String businessUid) {
        CreativeContentDO textDO = creativeContentMapper.selectByType(businessUid, CreativeContentTypeEnum.COPY_WRITING.getCode());
        CreativeContentDO picDO = creativeContentMapper.selectByType(businessUid, CreativeContentTypeEnum.PICTURE.getCode());

        if (textDO == null || picDO == null) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, businessUid);
        }

        Integer maxRetry = getMaxRetry(false);

        if (textDO.getRetryCount() >= maxRetry || picDO.getRetryCount() >= maxRetry) {
            throw exception(CREATIVE_CONTENT_GREATER_RETRY, maxRetry);
        }
        Map<Long, Boolean> textMap = xlsCreativeExecuteManager.executeCopyWriting(Collections.singletonList(textDO), true);
        if (BooleanUtils.isNotTrue(textMap.get(textDO.getId()))) {
            throw exception(EXECTURE_ERROR, "文案", textDO.getId());
        }

        Map<Long, Boolean> picMap = xlsCreativeExecuteManager.executePicture(Collections.singletonList(picDO), true);
        if (BooleanUtils.isNotTrue(picMap.get(picDO.getId()))) {
            throw exception(EXECTURE_ERROR, "图片", textDO.getId());
        }

        creativePlanService.updatePlanStatus(textDO.getPlanUid());
        return detail(businessUid);
    }

    @Override
    @TenantIgnore
    public List<CreativeContentDO> jobQuery(CreativeQueryReqVO queryReq) {
        if (!queryReq.valid()) {
            return Collections.emptyList();
        }
        return creativeContentMapper.jobQuery(queryReq);
    }

    @Override
    @TenantIgnore
    public List<CreativeContentDO> listByPlanUid(String planUid) {
        return creativeContentMapper.selectByPlanUid(planUid);
    }

    /**
     * 计划下的所有任务根据 业务uid 分组
     *
     * @param planUidList 计划uid
     * @return 业务uid
     */
    @Override
    public List<CreativeContentBusinessPO> listGroupByBusinessUid(List<String> planUidList) {
        return creativeContentMapper.listGroupByBusinessUid(planUidList);
    }

    @Override
    public PageResult<CreativeContentRespVO> page(CreativeContentPageReqVO query) {
        if (StringUtils.isBlank(query.getPlanUid())) {
            throw exception(CreativeErrorCodeConstants.PLAN_UID_REQUIRED, query.getPlanUid());
        }

        CreativePlanRespVO plan = creativePlanService.get(query.getPlanUid());
        CreativePlanConfigDTO config = plan.getConfig();
        List<CreativeSchemeListOptionRespVO> schemeList = config.getSchemeList();
        // 老数据
        if (CollectionUtils.isEmpty(schemeList)) {
            Long count = creativeContentMapper.selectCount(query);
            if (count == null || count <= 0) {
                return PageResult.empty();
            }
            List<CreativeContentDTO> pageSelect = creativeContentMapper.pageSelect(query, PageUtils.getStart(query), query.getPageSize());
            return new PageResult<>(CreativeContentConvert.INSTANCE.convertDto(pageSelect), count);
        }

        // 新数据，取第一条
        CreativeSchemeListOptionRespVO schemeListOption = schemeList.get(0);
        if (!CreativeSchemeModeEnum.CUSTOM_IMAGE_TEXT.name().equals(schemeListOption.getMode())) {
            Long count = creativeContentMapper.selectCount(query);
            if (count == null || count <= 0) {
                return PageResult.empty();
            }
            List<CreativeContentDTO> pageSelect = creativeContentMapper.pageSelect(query, PageUtils.getStart(query), query.getPageSize());
            return new PageResult<>(CreativeContentConvert.INSTANCE.convertDto(pageSelect), count);
        }

        // 自定义类型
        IPage<CreativeContentDTO> page = new Page<>(query.getPageNo(), query.getPageSize());
        Page<CreativeContentDTO> allTypePage = creativeContentMapper.allTypePage(page, query);
        return new PageResult<>(CreativeContentConvert.INSTANCE.convertDto(allTypePage.getRecords()), allTypePage.getTotal());
    }

    @Override
    public com.starcloud.ops.business.app.api.xhs.content.vo.response.PageResult<CreativeContentRespVO> newPage(CreativeContentPageReqVO req) {
        CreativeContentPageReqVO pageReq = new CreativeContentPageReqVO();
        BeanUtil.copyProperties(req, pageReq);
        PageResult<CreativeContentRespVO> page = page(pageReq);
        com.starcloud.ops.business.app.api.xhs.content.vo.response.PageResult<CreativeContentRespVO> result = new com.starcloud.ops.business.app.api.xhs.content.vo.response.PageResult<>(page.getList(), page.getTotal());

        List<CreativeContentDO> xhsCreativeContents = creativeContentMapper.selectByPlanUid(req.getPlanUid());
        Map<String, List<CreativeContentDO>> contentGroup = xhsCreativeContents.stream().collect(Collectors.groupingBy(CreativeContentDO::getBusinessUid));
        int successCount = 0, errorCount = 0;

        for (String bizId : contentGroup.keySet()) {
            List<CreativeContentDO> contentList = contentGroup.get(bizId);
            if (CollectionUtils.isEmpty(contentList)) {
                continue;
            }
            boolean error = contentList.stream()
                    .anyMatch(x -> CreativeContentStatusEnum.EXECUTE_ERROR.getCode().equals(x.getStatus()));
            boolean success = contentList.stream()
                    .allMatch(x -> CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode().equals(x.getStatus()));
            if (error) {
                errorCount++;
            } else if (success) {
                successCount++;
            }
        }
        result.setSuccessCount(successCount);
        result.setErrorCount(errorCount);
        return result;
    }

    @Override
    public CreativeContentRespVO detail(String businessUid) {
        CreativeContentDTO detail = byBusinessUid(businessUid);
        return CreativeContentConvert.INSTANCE.convert(detail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreativeContentRespVO modify(CreativeContentModifyReqVO modifyReq) {
        if (CollectionUtils.isNotEmpty(modifyReq.getPictureContent())) {
            CreativeContentDO pictDo = creativeContentMapper.selectByType(modifyReq.getBusinessUid(), CreativeContentTypeEnum.PICTURE.getCode());
            if (pictDo == null) {
                throw exception(CREATIVE_CONTENT_NOT_EXIST, modifyReq.getBusinessUid());
            }
            if (BooleanUtils.isTrue(pictDo.getClaim())) {
                throw exception(CREATIVE_CONTENT_CLAIMED, modifyReq.getBusinessUid());
            }
            pictDo.setPictureContent(CreativeContentConvert.INSTANCE.imageToStr(modifyReq.getPictureContent()));
            pictDo.setUpdateTime(LocalDateTime.now());
            pictDo.setUpdater(WebFrameworkUtils.getLoginUserId().toString());
            creativeContentMapper.updateById(pictDo);
        }

        if (StringUtils.isNotBlank(modifyReq.getCopyWritingTitle()) || StringUtils.isNotBlank(modifyReq.getCopyWritingContent())) {
            CreativeContentDO contentDO = creativeContentMapper.selectByType(modifyReq.getBusinessUid(), CreativeContentTypeEnum.COPY_WRITING.getCode());
            if (contentDO == null) {
                throw exception(CREATIVE_CONTENT_NOT_EXIST, modifyReq.getBusinessUid());
            }
            if (BooleanUtils.isTrue(contentDO.getClaim())) {
                throw exception(CREATIVE_CONTENT_CLAIMED, modifyReq.getBusinessUid());
            }
            if (modifyReq.getCopyWritingTitle() != null) {
                contentDO.setCopyWritingTitle(modifyReq.getCopyWritingTitle());
            }
            if (modifyReq.getCopyWritingContent() != null) {
                contentDO.setCopyWritingContent(modifyReq.getCopyWritingContent());
            }
            contentDO.setUpdateTime(LocalDateTime.now());
            contentDO.setUpdater(WebFrameworkUtils.getLoginUserId().toString());
            creativeContentMapper.updateById(contentDO);
        }
        return detail(modifyReq.getBusinessUid());
    }

    @Override
    public void delete(String businessUid) {
        int count = creativeContentMapper.delete(businessUid);
        if (count == 0) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, businessUid);
        }
    }

    /**
     * 删除计划下的所有创作内容
     *
     * @param planUid 计划uid
     */
    @Override
    public void deleteByPlanUid(String planUid) {
        creativeContentMapper.deleteByPlanUid(planUid);
    }

    @Override
    public List<CreativeContentRespVO> bound(List<String> businessUids) {
        List<CreativeContentDTO> xhsCreativeContents = creativeContentMapper.selectByBusinessUid(businessUids, false);
        if (xhsCreativeContents.size() < businessUids.size()) {
            throw exception(new ErrorCode(500, "存在已绑定的创作内容"));
        }
        creativeContentMapper.claim(businessUids, true);
        return CreativeContentConvert.INSTANCE.convertDto(xhsCreativeContents);
    }

    /**
     * 点赞
     *
     * @param businessUid 业务uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(String businessUid) {
        List<CreativeContentDO> xhsCreativeContents = creativeContentMapper.listByBusinessUid(businessUid);
        if (CollectionUtils.isEmpty(xhsCreativeContents)) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, businessUid);
        }
        for (CreativeContentDO content : xhsCreativeContents) {
            content.setLiked(Boolean.TRUE);
            creativeContentMapper.updateById(content);
        }
    }

    /**
     * 取消点赞
     *
     * @param businessUid 业务uid
     */
    @Override
    public void unlike(String businessUid) {
        List<CreativeContentDO> xhsCreativeContents = creativeContentMapper.listByBusinessUid(businessUid);
        if (CollectionUtils.isEmpty(xhsCreativeContents)) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, businessUid);
        }
        for (CreativeContentDO content : xhsCreativeContents) {
            content.setLiked(Boolean.FALSE);
            creativeContentMapper.updateById(content);
        }
    }

    @Override
    public void unBound(List<String> businessUids) {
        if (CollectionUtils.isEmpty(businessUids)) {
            return;
        }
        creativeContentMapper.claim(businessUids, false);
    }

    private CreativeContentDTO byBusinessUid(String businessUid) {
        CreativeContentDTO detail = creativeContentMapper.detail(businessUid);
        if (detail == null) {
            detail = creativeContentMapper.allTypeDetail(businessUid);
            if (detail == null) {
                throw exception(CREATIVE_CONTENT_NOT_EXIST, businessUid);
            }
        }
        return detail;
    }

    private Integer getMaxRetry(Boolean force) {
        if (BooleanUtils.isTrue(force)) {
            return Integer.MAX_VALUE;
        }
        DictDataDO dictDataDO = dictDataService.parseDictData("xhs", "max_retry");
        if (dictDataDO == null || dictDataDO.getValue() == null) {
            return 3;
        }
        return Integer.valueOf(dictDataDO.getValue());
    }

}
