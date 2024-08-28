package com.starcloud.ops.business.job.biz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobConfigBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.LibraryJobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.PluginDetailVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.JobLogRespVO;
import com.starcloud.ops.business.job.biz.convert.BusinessJobConvert;
import com.starcloud.ops.business.job.biz.convert.BusinessJobLogConvert;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobLogDO;
import com.starcloud.ops.business.job.biz.dal.mysql.BusinessJobLogMapper;
import com.starcloud.ops.business.job.biz.service.BusinessJobLogService;
import com.starcloud.ops.business.job.biz.service.BusinessJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessJobLogServiceImpl implements BusinessJobLogService {

    @Resource
    private BusinessJobLogMapper businessJobLogMapper;

    @Resource
    private PluginConfigService pluginConfigService;

    @Resource
    private BusinessJobService jobService;

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
        result.getList().forEach(BusinessJobLogConvert.INSTANCE::convert);
        return result;
    }

    @Override
    public PageResult<JobLogRespVO> libraryPage(LibraryJobLogPageReqVO reqVO) {
        List<PluginConfigRespVO> configList = pluginConfigService.configList(reqVO.getLibraryUid());
        if (CollectionUtil.isEmpty(configList)) {
            return PageResult.empty();
        }
        List<String> configUidList = configList.stream().map(PluginConfigRespVO::getUid).collect(Collectors.toList());
        List<BusinessJobRespVO> jobRespList = jobService.getByForeignKey(configUidList);
        if (CollectionUtil.isEmpty(jobRespList)) {
            return PageResult.empty();
        }
        List<String> jobUidList = jobRespList.stream().map(BusinessJobRespVO::getUid).collect(Collectors.toList());
        PageResult<BusinessJobLogDO> pageResult = businessJobLogMapper.libraryPage(jobUidList, reqVO);
        PageResult<JobLogRespVO> result = BusinessJobLogConvert.INSTANCE.convert(pageResult);
        result.getList().forEach(BusinessJobLogConvert.INSTANCE::convert);
        return result;
    }
}
