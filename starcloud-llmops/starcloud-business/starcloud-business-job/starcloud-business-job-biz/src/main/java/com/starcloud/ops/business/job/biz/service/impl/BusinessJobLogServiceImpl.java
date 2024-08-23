package com.starcloud.ops.business.job.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.PluginDetailVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.JobLogRespVO;
import com.starcloud.ops.business.job.biz.convert.BusinessJobLogConvert;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobLogDO;
import com.starcloud.ops.business.job.biz.dal.mysql.BusinessJobLogMapper;
import com.starcloud.ops.business.job.biz.service.BusinessJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BusinessJobLogServiceImpl implements BusinessJobLogService {

    @Resource
    private BusinessJobLogMapper businessJobLogMapper;

    @Override
    public Long recordLog(JobLogBaseVO logBaseVO) {
        BusinessJobLogDO businessJobLogDO = BusinessJobLogConvert.INSTANCE.convert(logBaseVO);
        businessJobLogDO.setUid(IdUtil.fastSimpleUUID());
        businessJobLogMapper.insert(businessJobLogDO);
        return businessJobLogDO.getId();
    }

    @Override
    public PageResult<JobLogRespVO> page(JobLogPageReqVO pageReqVO) {
        PageResult<BusinessJobLogDO> page = businessJobLogMapper.page(pageReqVO);
        PageResult<JobLogRespVO> result = BusinessJobLogConvert.INSTANCE.convert(page);
        result.getList().forEach(logVo -> {
            String executeConfig = logVo.getExecuteConfig();
            if (StringUtils.isBlank(executeConfig)) {
                return;
            }
            PluginDetailVO pluginDetailVO = JSON.parseObject(executeConfig, PluginDetailVO.class);
            if (Objects.isNull(pluginDetailVO)) {
                return;
            }
            logVo.setPluginName(pluginDetailVO.getPluginName());
            logVo.setPluginUid(pluginDetailVO.getPluginUid());
        });
        return result;
    }
}
