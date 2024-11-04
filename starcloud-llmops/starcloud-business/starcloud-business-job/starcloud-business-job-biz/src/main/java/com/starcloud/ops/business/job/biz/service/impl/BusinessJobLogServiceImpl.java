package com.starcloud.ops.business.job.biz.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.LibraryJobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.CozeJobLogRespVO;
import com.starcloud.ops.business.job.biz.convert.BusinessJobLogConvert;
import com.starcloud.ops.business.job.biz.dal.dataobject.BindAppDetail;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobLogDO;
import com.starcloud.ops.business.job.biz.dal.dataobject.JobLogDTO;
import com.starcloud.ops.business.job.biz.dal.mysql.BusinessJobLogMapper;
import com.starcloud.ops.business.job.biz.service.BusinessJobLogService;
import com.starcloud.ops.business.job.biz.service.BusinessJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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

    @Resource
    private MaterialLibraryService materialLibraryService;

    @Override
    public Long recordLog(JobLogBaseVO logBaseVO) {
        BusinessJobLogDO businessJobLogDO = BusinessJobLogConvert.INSTANCE.convert(logBaseVO);
        businessJobLogDO.setUid(IdUtil.fastSimpleUUID());
        businessJobLogMapper.insert(businessJobLogDO);
        return businessJobLogDO.getId();
    }

    @Override
    public PageResult<CozeJobLogRespVO> page(JobLogPageReqVO pageReqVO) {
        PageResult<BusinessJobLogDO> page = businessJobLogMapper.page(pageReqVO);
        PageResult<CozeJobLogRespVO> result = BusinessJobLogConvert.INSTANCE.convert(page);
        result.getList().forEach(BusinessJobLogConvert.INSTANCE::convert);
        return result;
    }

    @Override
    public PageResult<CozeJobLogRespVO> libraryPage(LibraryJobLogPageReqVO reqVO) {
        List<PluginConfigRespVO> configList = pluginConfigService.configList(reqVO.getLibraryUid());
        MaterialLibraryRespVO libraryRespVO = materialLibraryService.getMaterialLibraryByUid(reqVO.getLibraryUid());
        String libraryName = Objects.isNull(libraryRespVO) ? StringUtils.EMPTY : libraryRespVO.getName();

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
        PageResult<CozeJobLogRespVO> result = BusinessJobLogConvert.INSTANCE.convert(pageResult);
        result.getList().forEach(resp -> {
            BusinessJobLogConvert.INSTANCE.convert(resp);
            resp.setLibraryName(libraryName);
        });
        return result;
    }

    @Override
    public PageResult<JobLogDTO> pluginLog(PageParam pageParam) {
        Long count = businessJobLogMapper.count();
        if (Objects.isNull(count) || count < 1) {
            return PageResult.empty();
        }
        List<JobLogDTO> result = businessJobLogMapper.pluginLog(PageUtils.getStart(pageParam), pageParam.getPageSize());
        List<Long> libraryIdList = result.stream().map(JobLogDTO::getLibraryId).collect(Collectors.toList());
        Map<Long, BindAppDetail> libraryAppMap = businessJobLogMapper.appDetail(libraryIdList).stream().collect(Collectors.toMap(BindAppDetail::getLibraryId, Function.identity()));

        result.forEach(jobLogDTO -> {
            BusinessJobLogConvert.INSTANCE.convert(jobLogDTO);
            Long libraryId = jobLogDTO.getLibraryId();
            BindAppDetail bindAppDetail = libraryAppMap.get(libraryId);
            if (Objects.isNull(bindAppDetail)) {
                return;
            }
            jobLogDTO.setBindAppType(bindAppDetail.getBindAppType());
            if (Objects.equals(bindAppDetail.getBindAppType(), 30)) {
                jobLogDTO.setAppMarketUid(bindAppDetail.getAppMarketUid());

            } else if (Objects.equals(bindAppDetail.getBindAppType(), 20)) {
                jobLogDTO.setAppMarketUid(bindAppDetail.getBindAppUid());
            } else if (Objects.equals(bindAppDetail.getBindAppType(), 10)) {
                jobLogDTO.setAppUid(bindAppDetail.getBindAppUid());
            }
        });
        return PageResult.of(result, count);
    }
}
