package com.starcloud.ops.business.job.biz.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobConfigBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.CozeJobLogRespVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobLogDO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.PluginDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper
public interface BusinessJobLogConvert {

    BusinessJobLogConvert INSTANCE = Mappers.getMapper(BusinessJobLogConvert.class);

    BusinessJobLogDO convert(JobLogBaseVO jobLogBaseVO);

    PageResult<CozeJobLogRespVO> convert(PageResult<BusinessJobLogDO> jobLogBaseVO);

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

    default void convert(CozeJobLogRespVO respVO) {
        String executeConfig = respVO.getExecuteConfig();
        if (StringUtils.isBlank(executeConfig)) {
            return;
        }
        JobConfigBaseVO configBaseVO = BusinessJobConvert.INSTANCE.convert(executeConfig);
        if (Objects.isNull(configBaseVO) || !(configBaseVO instanceof PluginDetailVO)) {
            return;
        }
        PluginDetailVO pluginDetailVO = (PluginDetailVO) configBaseVO;
        respVO.setPluginName(pluginDetailVO.getPluginName());
        respVO.setPluginUid(pluginDetailVO.getPluginUid());
        respVO.setLibraryUid(pluginDetailVO.getLibraryUid());
        if (respVO.getSuccess()) {
            respVO.setCount(respVO.getExecuteResult());
        }else {
            respVO.setCount("0");
        }
        respVO.setExecuteResult(pluginDetailVO.getLibraryUid());
    }
}