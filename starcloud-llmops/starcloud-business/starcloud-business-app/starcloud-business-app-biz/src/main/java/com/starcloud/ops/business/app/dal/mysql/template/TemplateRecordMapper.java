package com.starcloud.ops.business.app.dal.mysql.template;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.TemplateRecordRespVO;
import com.starcloud.ops.business.app.dal.databoject.template.TemplateRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TemplateRecordMapper extends BaseMapperX<TemplateRecordDO> {

    default List<TemplateRecordDO> selectList(String creator) {
        LambdaQueryWrapper<TemplateRecordDO> wrapper = Wrappers.lambdaQuery(TemplateRecordDO.class)
                .eq(TemplateRecordDO::getCreator, creator)
                .orderByDesc(TemplateRecordDO::getId);
        return selectList(wrapper);
    }


    default List<TemplateRecordRespVO> templateList(String creator) {
        MPJLambdaWrapper<TemplateRecordDO> wrapper = new MPJLambdaWrapper<TemplateRecordDO>("t")
                .selectAs(TemplateRecordDO::getUid, TemplateRecordRespVO::getUid)
                .selectAs(TemplateRecordDO::getTemplateCode, TemplateRecordRespVO::getTemplateCode)
                .selectAs("p.name", TemplateRecordRespVO::getName)
                .selectAs("p.thumbnail", TemplateRecordRespVO::getExample)
                .leftJoin("poster_material p on t.template_code = p.uid")
                .last("and t.creator = " + creator + " order by t.id desc");

        return selectJoinList(TemplateRecordRespVO.class, wrapper);
    }
}
