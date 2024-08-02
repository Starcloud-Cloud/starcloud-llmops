package com.starcloud.ops.business.app.api.xhs.material;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialFieldTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private List<MaterialFieldConfigDTO> imageField;

    /**
     * excel的内容
     */
    private List<Map<String, Object>> materialList;

    /**
     * 字段配置
     */
    private List<MaterialFieldConfigDTO> materialFieldConfigDTOList;

    private Long tenantId;

    /**
     * zip解压后绝对路径
     */
    private String unzipDir;

    /**
     * 是否需要截图文档
     */
    private boolean containsDocument;

    public UploadMaterialImageDTO(String parseUid, List<Map<String, Object>> materialList,
                                  List<MaterialFieldConfigDTO> materialFieldConfigDTOList, String unzipDir) {
        this.parseUid = parseUid;
        this.materialList = materialList;
        this.tenantId = TenantContextHolder.getTenantId();
        this.materialFieldConfigDTOList = materialFieldConfigDTOList;
        this.imageField = materialFieldConfigDTOList.stream().filter(materialFieldConfigDTO -> {
            return MaterialFieldTypeEnum.image.getCode().equalsIgnoreCase(materialFieldConfigDTO.getType());
        }).collect(Collectors.toList());
        this.containsDocument = materialFieldConfigDTOList.stream().anyMatch(materialFieldConfigDTO -> {
            return MaterialFieldTypeEnum.document.getCode().equalsIgnoreCase(materialFieldConfigDTO.getType());
        });
        this.unzipDir = unzipDir;
    }

    /**
     * 是否包含图片字段
     *
     * @return
     */
    public boolean containsImage() {
        if (CollectionUtils.isEmpty(imageField) || CollectionUtils.isEmpty(materialList)) {
            log.info("Does not contain images, parseUid = {}", parseUid);
            return false;
        }
        return true;
    }

    public String getDocumentFieldName() {
        return materialFieldConfigDTOList.stream().filter(materialFieldConfigDTO -> {
            return MaterialFieldTypeEnum.document.getCode().equalsIgnoreCase(materialFieldConfigDTO.getType());
        }).findAny().map(MaterialFieldConfigDTO::getFieldName).orElse(StringUtils.EMPTY);
    }

}
