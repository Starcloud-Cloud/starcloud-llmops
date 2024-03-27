package com.starcloud.ops.business.app.api.xhs.material;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Data
@Slf4j
public class UploadMaterialImageDTO {

    /**
     * 上传uid
     */
    private String parseUid;

    /**
     * 素材中的图片字段
     */
    private List<Field> imageField;

    /**
     * excel的内容
     */
    private List<? extends AbstractBaseCreativeMaterialDTO> materialDTOList;

    private String materialType;

    /**
     * 是否保存数据库
     */
    private boolean saveDb;

    public UploadMaterialImageDTO(String materialType, String parseUid, List<? extends AbstractBaseCreativeMaterialDTO> materialDTOList) {
        this.parseUid = parseUid;
        this.imageField = MaterialTypeEnum.of(materialType).filterField(FieldTypeEnum.image);
        this.materialDTOList = materialDTOList;
        this.materialType = materialType;
    }

    /**
     * 是否包含图片字段
     *
     * @return
     */
    public boolean containsImage() {
        if (CollectionUtils.isEmpty(imageField) || CollectionUtils.isEmpty(materialDTOList)) {
            log.info("Does not contain images, parseUid = {}", parseUid);
            return false;
        }
        return true;
    }

}
