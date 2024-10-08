package com.starcloud.ops.business.job.biz.api;

import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.starcloud.ops.business.job.api.BusinessJobApi;
import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.BusinessJobModifyReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.PluginDetailVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.convert.BusinessJobConvert;
import com.starcloud.ops.business.job.biz.service.BusinessJobService;
import com.starcloud.ops.business.job.dto.JobDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BusinessJobApiImpl implements BusinessJobApi {

    @Resource
    private BusinessJobService businessJobService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataPermission(enable = false)
    public void deleteByForeignKey(String foreignKey) {
        BusinessJobRespVO businessJobRespVO = businessJobService.getByForeignKey(foreignKey);
        if (Objects.isNull(businessJobRespVO)) {
            return;
        }
        businessJobService.delete(businessJobRespVO.getUid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyJob(String sourceForeignKey, String targetForeignKey, String libraryUid) {
        BusinessJobRespVO businessJobRespVO = businessJobService.getByForeignKey(sourceForeignKey);
        if (Objects.isNull(businessJobRespVO)) {
            return;
        }
        BusinessJobBaseVO newJob = new BusinessJobBaseVO();
        newJob.setName(businessJobRespVO.getName());
        newJob.setForeignKey(targetForeignKey);
        newJob.setBusinessJobType(businessJobRespVO.getBusinessJobType());
        newJob.setTimeExpression(businessJobRespVO.getTimeExpression());
        newJob.setTimeExpressionType(businessJobRespVO.getTimeExpressionType());
        newJob.setLifecycleStart(businessJobRespVO.getLifecycleStart());
        newJob.setLifecycleEnd(businessJobRespVO.getLifecycleEnd());
        newJob.setDescption(businessJobRespVO.getDescption() + " \n copyFrom uid=" + businessJobRespVO.getUid());
        newJob.setEnable(businessJobRespVO.getEnable());

        PluginDetailVO configBaseVO = (PluginDetailVO) businessJobRespVO.getConfig();
        configBaseVO.setLibraryUid(libraryUid);
        newJob.setConfig(configBaseVO);

        businessJobService.createJob(newJob);

    }

    @Override
    @DataPermission(enable = false)
    public List<JobDetailDTO> queryJob(List<String> foreignKeyList) {
        List<BusinessJobRespVO> businessJobRespList = businessJobService.getByForeignKey(foreignKeyList);
        return BusinessJobConvert.INSTANCE.convertApi(businessJobRespList);
    }

    @Override
    public void updateJob(String sourceForeignKey, String targetForeignKey, String libraryUid) {
        BusinessJobRespVO sourceJob = businessJobService.getByForeignKey(sourceForeignKey);
        BusinessJobRespVO targetJob = businessJobService.getByForeignKey(targetForeignKey);
        if (Objects.isNull(sourceJob) && Objects.isNull(targetJob)) {
            return;
        }

        // job已删除
        if (Objects.isNull(sourceJob) && Objects.nonNull(targetJob)) {
            businessJobService.delete(targetJob.getUid());
            return;
        }

        // 新增job
        if (Objects.nonNull(sourceJob) && Objects.isNull(targetJob)) {
            copyJob(sourceForeignKey, targetForeignKey, libraryUid);
        }

        // 更新job
        if (Objects.nonNull(sourceJob) && Objects.nonNull(targetJob)) {
            BusinessJobModifyReqVO modifyReqVO = BusinessJobConvert.INSTANCE.convert(targetJob);
            modifyReqVO.setBusinessJobType(sourceJob.getBusinessJobType());
            modifyReqVO.setTimeExpression(sourceJob.getTimeExpression());
            modifyReqVO.setTimeExpressionType(sourceJob.getTimeExpressionType());
            modifyReqVO.setLifecycleStart(sourceJob.getLifecycleStart());
            modifyReqVO.setLifecycleEnd(sourceJob.getLifecycleEnd());
            modifyReqVO.setDescption(sourceJob.getDescption() + " \n updateFrom uid=" + sourceJob.getUid());
            modifyReqVO.setEnable(sourceJob.getEnable());

            PluginDetailVO configBaseVO = (PluginDetailVO) sourceJob.getConfig();
            configBaseVO.setLibraryUid(libraryUid);
            modifyReqVO.setConfig(configBaseVO);
            businessJobService.modify(modifyReqVO);
        }
    }
}
