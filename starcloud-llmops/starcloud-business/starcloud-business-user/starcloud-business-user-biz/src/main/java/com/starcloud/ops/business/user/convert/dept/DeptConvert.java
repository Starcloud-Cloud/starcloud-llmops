package com.starcloud.ops.business.user.convert.dept;


import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateUserDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.UserDeptUpdateReqVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.DeptRespVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.dal.dataObject.dept.UserDeptDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeptConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    DeptRespVO convert(DeptDO deptDO);

    UserDeptDO convert(CreateUserDeptReqVO reqVO);

    @Mapping(source = "id",target = "deptId")
    @Mapping(source = "name",target = "deptName")
    UserDeptRespVO convert2(DeptDO deptDO);


    DeptUpdateReqVO convert(UserDeptUpdateReqVO reqVO);

}
