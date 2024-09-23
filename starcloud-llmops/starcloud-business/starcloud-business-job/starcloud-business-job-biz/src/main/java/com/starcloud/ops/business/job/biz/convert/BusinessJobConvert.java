package com.starcloud.ops.business.job.biz.convert;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.BusinessJobModifyReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobConfigBaseVO;
import com.starcloud.ops.business.job.dto.JobDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

@Mapper
public interface BusinessJobConvert {

    BusinessJobConvert INSTANCE = Mappers.getMapper(BusinessJobConvert.class);

    BusinessJobDO convert(BusinessJobBaseVO businessJobBaseVO);

    BusinessJobDO convert(BusinessJobModifyReqVO businessJobBaseVO);

    BusinessJobRespVO convert(BusinessJobDO businessJobDO);

    List<BusinessJobRespVO> convert(List<BusinessJobDO> businessJobDOList);

    List<JobDetailDTO> convertApi(List<BusinessJobRespVO> businessJobRespVOList);

    default JobConfigBaseVO convert(String config) {
        if (StringUtils.isBlank(config)) {
            return null;
        }
        return JsonUtils.parseObject(config, JobConfigBaseVO.class);
    }

    default String convert(JobConfigBaseVO config) {
        if (Objects.isNull(config)) {
            return StringUtils.EMPTY;
        }
        return JsonUtils.toJsonString(config);
    }
}
