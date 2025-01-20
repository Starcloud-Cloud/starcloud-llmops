package com.starcloud.ops.business.app.dal.mysql.opus;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.starcloud.ops.business.app.controller.admin.opus.vo.OpusBindPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusBindDO;
import com.starcloud.ops.business.app.dal.databoject.opus.OpusCreativeBindDTO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OpusBindMapper extends BaseMapperX<OpusBindDO> {

    default OpusBindDO selectBind(String opusUid, String creativeUid) {
        LambdaQueryWrapper<OpusBindDO> wrapper = Wrappers.lambdaQuery(OpusBindDO.class)
                .eq(OpusBindDO::getCreativeContentUid, creativeUid)
                .eq(OpusBindDO::getOpusUid, opusUid);
        return selectOne(wrapper);
    }

    default PageResult<OpusCreativeBindDTO> page(OpusBindPageReqVO pageParam) {
        MPJLambdaWrapper<OpusBindDO> wrapper = new MPJLambdaWrapper<OpusBindDO>()
                .selectAll(OpusBindDO.class)
                .selectAs(CreativeContentDO::getExecuteResult, OpusCreativeBindDTO::getExecuteResult)
                .selectAs("t2.nickname", OpusCreativeBindDTO::getCreateUser)
                .selectAs("t3.nickname", OpusCreativeBindDTO::getUpdaterUser)
                .leftJoin(CreativeContentDO.class, CreativeContentDO::getUid, OpusBindDO::getCreativeContentUid)
                .leftJoin(AdminUserDO.class, AdminUserDO::getId, OpusBindDO::getCreator)
                .leftJoin(AdminUserDO.class, AdminUserDO::getId, OpusBindDO::getUpdater)
                .eq(StringUtils.isNotBlank(pageParam.getOpusUid()), OpusBindDO::getOpusUid, pageParam.getOpusUid())
                .eq(StringUtils.isNotBlank(pageParam.getDirUid()), OpusBindDO::getDirUid, pageParam.getDirUid())
                .orderByDesc(OpusBindDO::getId);
        return selectJoinPage(pageParam, OpusCreativeBindDTO.class, wrapper);
    }
}
