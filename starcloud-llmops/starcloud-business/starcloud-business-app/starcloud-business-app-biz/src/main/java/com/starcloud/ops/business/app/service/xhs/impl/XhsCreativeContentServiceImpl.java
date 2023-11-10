package com.starcloud.ops.business.app.service.xhs.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentCreateReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentPageReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeQueryReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.convert.xhs.XhsCreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDTO;
import com.starcloud.ops.business.app.dal.mysql.xhs.XhsCreativeContentMapper;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentStatusEnums;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeExectueManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class XhsCreativeContentServiceImpl implements XhsCreativeContentService {

    @Resource
    private XhsCreativeContentMapper creativeContentMapper;

    @Resource
    private XhsCreativeExectueManager xhsCreativeExectueManager;


    @Resource
    private DictDataService dictDataService;


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
                    .stream().filter(content -> {
                        return !XhsCreativeContentStatusEnums.EXECUTING.getCode().equals(content.getStatus());
                    }).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(contentList)) {
                return Collections.emptyMap();
            }
            if (XhsCreativeContentTypeEnums.COPY_WRITING.getCode().equals(type)) {
                return xhsCreativeExectueManager.executeCopyWriting(contentList, force);
            } else if (XhsCreativeContentTypeEnums.PICTURE.getCode().equals(type)) {
                return xhsCreativeExectueManager.executePicture(contentList, force);
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

        if (XhsCreativeContentStatusEnums.INIT.getCode().equals(picDO.getStatus())
                || XhsCreativeContentStatusEnums.EXECUTE_ERROR.getCode().equals(picDO.getStatus())) {
            Map<Long, Boolean> picMap = xhsCreativeExectueManager.executePicture(Collections.singletonList(picDO), false);
            if (BooleanUtils.isNotTrue(picMap.get(picDO.getId()))) {
                throw exception(EXECTURE_ERROR, "图片");
            }
        }

        if (XhsCreativeContentStatusEnums.INIT.getCode().equals(textDO.getStatus())
                || XhsCreativeContentStatusEnums.EXECUTE_ERROR.getCode().equals(textDO.getStatus())) {
            Map<Long, Boolean> textMap = xhsCreativeExectueManager.executeCopyWriting(Collections.singletonList(textDO), false);
            if (BooleanUtils.isNotTrue(textMap.get(textDO.getId()))) {
                throw exception(EXECTURE_ERROR, "文案");
            }
        }
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
