package com.starcloud.ops.business.user.convert.dept;


import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.dto.DeptConfigDTO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateUserDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.UserDeptUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.dal.dataobject.dept.UserDeptDO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeptConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    DeptRespVO convert(DeptDO deptDO);

    UserDeptDO convert(CreateUserDeptReqVO reqVO);

    @Mapping(source = "id", target = "deptId")
    @Mapping(source = "name", target = "deptName")
    UserDeptRespVO convert2(DeptDO deptDO);


    DeptUpdateReqVO convert(UserDeptUpdateReqVO reqVO);

    DeptDO convert(CreateDeptReqVO createDeptReqVO);

    default DeptConfigDTO convert(String config) {
        if (StringUtils.isBlank(config)) {
            return new DeptConfigDTO();
        }
        return JSONUtil.toBean(config, DeptConfigDTO.class);
    }

    default String convert(DeptConfigDTO configDTO) {
        if (configDTO == null) {
            return StringUtils.EMPTY;
        }
        return JSONUtil.toJsonStr(configDTO);
    }

}
