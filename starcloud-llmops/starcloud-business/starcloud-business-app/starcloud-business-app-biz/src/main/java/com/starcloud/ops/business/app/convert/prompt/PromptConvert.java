package com.starcloud.ops.business.app.convert.prompt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.PromptBaseVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.DeptPromptModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.resp.PromptRespVO;
import com.starcloud.ops.business.app.dal.databoject.prompt.DeptPromptDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PromptConvert {

    PromptConvert INSTANCE = Mappers.getMapper(PromptConvert.class);

    DeptPromptDO convert(PromptBaseVO promptBaseVO);

    DeptPromptDO convert(DeptPromptModifyReqVO reqVO);

    PromptRespVO convert(DeptPromptDO deptPromptDO);

    PageResult<PromptRespVO> convert(PageResult<DeptPromptDO> page);

}
