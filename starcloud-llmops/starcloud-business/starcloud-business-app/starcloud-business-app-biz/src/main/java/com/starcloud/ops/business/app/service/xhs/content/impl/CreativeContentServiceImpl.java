package com.starcloud.ops.business.app.service.xhs.content.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentCreateReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeQueryReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentBusinessPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDTO;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.enums.xhs.content.XhsCreativeContentStatusEnums;
import com.starcloud.ops.business.app.enums.xhs.content.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeExecuteManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CREATIVE_CONTENT_GREATER_RETRY;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CREATIVE_CONTENT_NOT_EXIST;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.EXECTURE_ERROR;

/**
 * @author admin
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
            contentDO.setStatus(XhsCreativeContentStatusEnums.INIT.getCode());
            creativeContentMapper.insert(contentDO);
        }
    }

    @Override
    @TenantIgnore
    public Map<Long, Boolean> execute(List<Long> ids, String type, Boolean force) {
        log.info("开始执行 {} 任务 {}", type, ids);
        try {
            List<CreativeContentDO> contentList = creativeContentMapper.selectBatchIds(ids)
                    .stream().filter(content -> !XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(content.getStatus())).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(contentList)) {
                return Collections.emptyMap();
            }
            if (XhsCreativeContentTypeEnums.COPY_WRITING.getCode().equalsIgnoreCase(type)) {
                return xlsCreativeExecuteManager.executeCopyWriting(contentList, force);
            } else if (XhsCreativeContentTypeEnums.PICTURE.getCode().equalsIgnoreCase(type)) {
                return xlsCreativeExecuteManager.executePicture(contentList, force);
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
        CreativeContentDO textDO = creativeContentMapper.selectByType(businessUid, XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
        CreativeContentDO picDO = creativeContentMapper.selectByType(businessUid, XhsCreativeContentTypeEnums.PICTURE.getCode());

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
    public PageResult<CreativeContentRespVO> page(CreativeContentPageReqVO req) {
        Long count = creativeContentMapper.selectCount(req);
        if (count == null || count <= 0) {
            return PageResult.empty();
        }
        List<CreativeContentDTO> pageSelect = creativeContentMapper.pageSelect(req, PageUtils.getStart(req), req.getPageSize());
        return new PageResult<>(CreativeContentConvert.INSTANCE.convertDto(pageSelect), count);
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
                    .anyMatch(x -> XhsCreativeContentStatusEnums.EXECUTE_ERROR.getCode().equals(x.getStatus()));
            boolean success = contentList.stream()
                    .allMatch(x -> XhsCreativeContentStatusEnums.EXECUTE_SUCCESS.getCode().equals(x.getStatus()));
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
    public CreativeContentRespVO modify(CreativeContentModifyReqVO modifyReq) {
        CreativeContentDO contentDO = creativeContentMapper.selectByType(modifyReq.getBusinessUid(), XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
        if (contentDO == null) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, modifyReq.getBusinessUid());
        }
        CreativeContentConvert.INSTANCE.updateSelective(modifyReq, contentDO);
        contentDO.setUpdateTime(LocalDateTime.now());
        contentDO.setUpdater(WebFrameworkUtils.getLoginUserId().toString());
        creativeContentMapper.updateById(contentDO);
        return CreativeContentConvert.INSTANCE.convert(contentDO);
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
            throw exception(CREATIVE_CONTENT_NOT_EXIST, businessUid);
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
