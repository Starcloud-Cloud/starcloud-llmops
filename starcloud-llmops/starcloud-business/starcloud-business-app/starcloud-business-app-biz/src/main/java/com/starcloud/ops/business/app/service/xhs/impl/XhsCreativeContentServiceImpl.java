package com.starcloud.ops.business.app.service.xhs.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentPageReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeQueryReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.convert.xhs.XhsCreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentBusinessPO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDTO;
import com.starcloud.ops.business.app.dal.mysql.xhs.XhsCreativeContentMapper;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeExecuteManager;
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
public class XhsCreativeContentServiceImpl implements XhsCreativeContentService {

    @Resource
    private XhsCreativeContentMapper creativeContentMapper;

    @Resource
    private XhsCreativeExecuteManager xlsCreativeExecuteManager;


    @Resource
    private DictDataService dictDataService;

    @Resource
    @Lazy
    private CreativePlanService creativePlanService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(List<XhsCreativeContentCreateReq> createReqs) {

        for (XhsCreativeContentCreateReq createReq : createReqs) {
            XhsCreativeContentDO contentDO = XhsCreativeContentConvert.INSTANCE.convert(createReq);
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
            List<XhsCreativeContentDO> contentList = creativeContentMapper.selectBatchIds(ids)
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
    public XhsCreativeContentResp retry(String businessUid) {
        XhsCreativeContentDO textDO = creativeContentMapper.selectByType(businessUid, XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
        XhsCreativeContentDO picDO = creativeContentMapper.selectByType(businessUid, XhsCreativeContentTypeEnums.PICTURE.getCode());

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
    public List<XhsCreativeContentDO> jobQuery(XhsCreativeQueryReq queryReq) {
        if (!queryReq.valid()) {
            return Collections.emptyList();
        }
        return creativeContentMapper.jobQuery(queryReq);
    }

    @Override
    @TenantIgnore
    public List<XhsCreativeContentDO> listByPlanUid(String planUid) {
        return creativeContentMapper.selectByPlanUid(planUid);
    }

    /**
     * 计划下的所有任务根据 业务uid 分组
     *
     * @param planUidList 计划uid
     * @return 业务uid
     */
    @Override
    public List<XhsCreativeContentBusinessPO> listGroupByBusinessUid(List<String> planUidList) {
        return creativeContentMapper.listGroupByBusinessUid(planUidList);
    }

    @Override
    public PageResult<XhsCreativeContentResp> page(XhsCreativeContentPageReq req) {
        Long count = creativeContentMapper.selectCount(req);
        if (count == null || count <= 0) {
            return PageResult.empty();
        }
        List<XhsCreativeContentDTO> pageSelect = creativeContentMapper.pageSelect(req, PageUtils.getStart(req), req.getPageSize());
        return new PageResult<>(XhsCreativeContentConvert.INSTANCE.convertDto(pageSelect), count);
    }

    @Override
    public com.starcloud.ops.business.app.controller.admin.xhs.vo.response.PageResult<XhsCreativeContentResp> newPage(XhsCreativeContentPageReq req) {
        XhsCreativeContentPageReq pageReq = new XhsCreativeContentPageReq();
        BeanUtil.copyProperties(req, pageReq);
        PageResult<XhsCreativeContentResp> page = page(pageReq);
        com.starcloud.ops.business.app.controller.admin.xhs.vo.response.PageResult<XhsCreativeContentResp> result = new com.starcloud.ops.business.app.controller.admin.xhs.vo.response.PageResult<>(page.getList(), page.getTotal());

        List<XhsCreativeContentDO> xhsCreativeContents = creativeContentMapper.selectByPlanUid(req.getPlanUid());
        Map<String, List<XhsCreativeContentDO>> contentGroup = xhsCreativeContents.stream().collect(Collectors.groupingBy(XhsCreativeContentDO::getBusinessUid));
        int successCount = 0, errorCount = 0;

        for (String bizId : contentGroup.keySet()) {
            List<XhsCreativeContentDO> contentList = contentGroup.get(bizId);
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
    public XhsCreativeContentResp detail(String businessUid) {
        XhsCreativeContentDTO detail = byBusinessUid(businessUid);
        return XhsCreativeContentConvert.INSTANCE.convert(detail);
    }

    @Override
    public XhsCreativeContentResp modify(XhsCreativeContentModifyReq modifyReq) {
        XhsCreativeContentDO contentDO = creativeContentMapper.selectByType(modifyReq.getBusinessUid(), XhsCreativeContentTypeEnums.COPY_WRITING.getCode());
        if (contentDO == null) {
            throw exception(CREATIVE_CONTENT_NOT_EXIST, modifyReq.getBusinessUid());
        }
        XhsCreativeContentConvert.INSTANCE.updateSelective(modifyReq, contentDO);
        contentDO.setUpdateTime(LocalDateTime.now());
        contentDO.setUpdater(WebFrameworkUtils.getLoginUserId().toString());
        creativeContentMapper.updateById(contentDO);
        return XhsCreativeContentConvert.INSTANCE.convert(contentDO);
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
    public List<XhsCreativeContentResp> bound(List<String> businessUids) {
        List<XhsCreativeContentDTO> xhsCreativeContents = creativeContentMapper.selectByBusinessUid(businessUids);
        if (xhsCreativeContents.size() < businessUids.size()) {
            throw exception(new ErrorCode(500, "存在已绑定的创作内容"));
        }
        creativeContentMapper.claim(businessUids);
        return XhsCreativeContentConvert.INSTANCE.convertDto(xhsCreativeContents);
    }

    private XhsCreativeContentDTO byBusinessUid(String businessUid) {
        XhsCreativeContentDTO detail = creativeContentMapper.detail(businessUid);
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
