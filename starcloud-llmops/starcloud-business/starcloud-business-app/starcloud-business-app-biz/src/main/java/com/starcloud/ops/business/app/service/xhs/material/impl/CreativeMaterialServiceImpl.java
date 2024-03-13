package com.starcloud.ops.business.app.service.xhs.material.impl;

import cn.hutool.core.util.IdUtil;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ModifyMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import com.starcloud.ops.business.app.convert.xhs.material.CreativeMaterialConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.material.CreativeMaterialDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.material.CreativeMaterialMapper;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_NOT_EXIST;

@Slf4j
@Service
public class CreativeMaterialServiceImpl implements CreativeMaterialService {

    @Resource
    private CreativeMaterialMapper materialMapper;

    @Override
    public Map<String, Object> metadata() {
        Map<String, Object> result = new HashMap<>();
        result.put(MaterialTypeEnum.class.getSimpleName(), MaterialTypeEnum.allOptions());
        result.put(FieldTypeEnum.class.getSimpleName(), FieldTypeEnum.options());
        return result;
    }

    @Override
    public void creatMaterial(BaseMaterialVO reqVO) {
        AbstractBaseCreativeMaterialDTO materialDetail = reqVO.getMaterialDetail();
        materialDetail.valid();
        CreativeMaterialDO materialDO = CreativeMaterialConvert.INSTANCE.convert(reqVO, materialDetail);
        materialDO.setUid(IdUtil.fastSimpleUUID());
        materialMapper.insert(materialDO);
    }

    @Override
    public void deleteMaterial(String uid) {
        CreativeMaterialDO materialDO = getByUid(uid);
        materialMapper.deleteById(materialDO.getId());
    }

    @Override
    public void modifyMaterial(ModifyMaterialReqVO reqVO) {
        AbstractBaseCreativeMaterialDTO materialDetail = reqVO.getMaterialDetail();
        materialDetail.valid();
        CreativeMaterialDO materialDO = getByUid(reqVO.getUid());
        CreativeMaterialDO updateDO = CreativeMaterialConvert.INSTANCE.convert(reqVO, materialDetail);
        updateDO.setId(materialDO.getId());
        materialMapper.updateById(updateDO);
    }

    @Override
    public List<MaterialRespVO> filterMaterial(FilterMaterialReqVO queryReq) {
        List<CreativeMaterialDO> creativeMaterialDOList = materialMapper.filterMaterial(queryReq);
        return CreativeMaterialConvert.INSTANCE.convert(creativeMaterialDOList);
    }

    private CreativeMaterialDO getByUid(String uid) {
        CreativeMaterialDO materialDO = materialMapper.getByUid(uid);
        if (Objects.isNull(materialDO)) {
            throw exception(MATERIAL_NOT_EXIST, uid);
        }
        return materialDO;
    }
}
