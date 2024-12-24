package com.starcloud.ops.business.app.dal.mysql.prompt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.PromptPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.prompt.DeptPromptDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptPromptMapper extends BaseMapperX<DeptPromptDO> {

    default DeptPromptDO selectByUid(String uid) {
        LambdaQueryWrapper<DeptPromptDO> wrapper = Wrappers.lambdaQuery(DeptPromptDO.class)
                .eq(DeptPromptDO::getUid, uid);
        return selectOne(wrapper);
    }

    default PageResult<DeptPromptDO> page(PromptPageReqVO reqVO) {
        LambdaQueryWrapper<DeptPromptDO> wrapper = Wrappers.lambdaQuery(DeptPromptDO.class);
        return selectPage(reqVO, wrapper);
    }

    default PageResult<DeptPromptDO> sysPage(PromptPageReqVO reqVO) {
        LambdaQueryWrapper<DeptPromptDO> wrapper = Wrappers.lambdaQuery(DeptPromptDO.class)
                .eq(DeptPromptDO::getSysEnable, true);
        return selectPage(reqVO, wrapper);
    }

}
