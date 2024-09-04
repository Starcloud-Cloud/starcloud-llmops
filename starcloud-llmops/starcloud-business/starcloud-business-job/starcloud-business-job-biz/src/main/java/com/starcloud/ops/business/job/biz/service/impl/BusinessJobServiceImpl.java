package com.starcloud.ops.business.job.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.BusinessJobModifyReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.convert.BusinessJobConvert;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;
import com.starcloud.ops.business.job.biz.dal.mysql.BusinessJobMapper;
import com.starcloud.ops.business.job.biz.enums.BusinessJobTypeEnum;
import com.starcloud.ops.business.job.biz.enums.TriggerTypeEnum;
import com.starcloud.ops.business.job.biz.powerjob.PowerjobManager;
import com.starcloud.ops.business.job.biz.service.BusinessJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.job.biz.enums.JobErrorCodeConstants.EXIST_JOB;
import static com.starcloud.ops.business.job.biz.enums.JobErrorCodeConstants.JOB_NOT_EXIST;

@Slf4j
@Service
public class BusinessJobServiceImpl implements BusinessJobService{

    @Resource
    private PowerjobManager powerjobManager;

    @Resource
    private BusinessJobMapper businessJobMapper;

    @Override
    public Map<String, Object> metadata() {
        Map<String, Object> metadata = Maps.newHashMap();
        metadata.put("businessType", BusinessJobTypeEnum.options());
        metadata.put("triggerType", TriggerTypeEnum.options());
        return metadata;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessJobRespVO createJob(BusinessJobBaseVO businessJobBaseVO) {
        // valid 每个素材库一个
        businessJobBaseVO.getConfig().valid();
        BusinessJobDO existJob = getByForeignKey0(businessJobBaseVO.getForeignKey());
        if (Objects.nonNull(existJob)) {
            throw exception(EXIST_JOB, existJob.getForeignKey());
        }

        Long jobId = powerjobManager.saveJob(businessJobBaseVO, null);
        BusinessJobDO businessJobDO = BusinessJobConvert.INSTANCE.convert(businessJobBaseVO);
        businessJobDO.setUid(IdUtil.fastSimpleUUID());
        businessJobDO.setJobId(jobId);
        businessJobMapper.insert(businessJobDO);
        return BusinessJobConvert.INSTANCE.convert(businessJobDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(BusinessJobModifyReqVO reqVO) {
        reqVO.getConfig().valid();
        BusinessJobDO businessJobDO = getByUid(reqVO.getUid());
        BusinessJobDO updateDO = BusinessJobConvert.INSTANCE.convert(reqVO);
        updateDO.setId(businessJobDO.getId());
        powerjobManager.saveJob(reqVO, businessJobDO.getJobId());
        businessJobMapper.updateById(updateDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        BusinessJobDO businessJobDO = getByUid(uid);
        businessJobMapper.deleteById(businessJobDO.getId());
        powerjobManager.deleteJob(businessJobDO.getJobId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stop(String uid) {
        BusinessJobDO businessJobDO = getByUid(uid);
        businessJobDO.setEnable(false);
        businessJobMapper.updateById(businessJobDO);
        powerjobManager.disable(businessJobDO.getJobId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(String uid) {
        BusinessJobDO businessJobDO = getByUid(uid);
        businessJobDO.setEnable(true);
        businessJobMapper.updateById(businessJobDO);
        powerjobManager.enable(businessJobDO.getJobId());
    }

    @Override
    public BusinessJobDO getByJobId(Long jobId) {
        BusinessJobDO businessJobDO = businessJobMapper.getByJobId(jobId);
        if (Objects.isNull(businessJobDO)) {
            throw exception(JOB_NOT_EXIST, "jobId", jobId);
        }
        return businessJobDO;
    }

    @Override
    public void runJob(String uid) {
        BusinessJobDO jobDO = getByUid(uid);
        powerjobManager.runJob(jobDO.getJobId(), null, 0L);
    }

    @Override
    public BusinessJobRespVO getByForeignKey(String foreignKey) {
        BusinessJobDO businessJobDO = getByForeignKey0(foreignKey);
        return BusinessJobConvert.INSTANCE.convert(businessJobDO);
    }

    @Override
    public List<BusinessJobRespVO> getByForeignKey(List<String> foreignKeys) {
        List<BusinessJobDO> jobDOList = businessJobMapper.getByForeignKey(foreignKeys);
        return  BusinessJobConvert.INSTANCE.convert(jobDOList);
    }

    private BusinessJobDO getByForeignKey0(String foreignKey) {
        return businessJobMapper.getByForeignKey(foreignKey);
    }

    private BusinessJobDO getByUid(String uid) {
        BusinessJobDO businessJobDO = businessJobMapper.getByUid(uid);
        if (Objects.isNull(businessJobDO)) {
            throw exception(JOB_NOT_EXIST, "uid", uid);
        }
        return businessJobDO;
    }
}
