package com.starcloud.ops.business.app.service.xhs.material.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.ParseMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_PARSE_ERROR;

@Slf4j
@Service
public class ParseMaterialServiceImpl implements ParseMaterialService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private DictDataService dataService;

    private static final String prefix = "material_parse_";

    @Override
    public Map<String, Object> template(String type) {
        Map<String, Object> result = new HashMap<>();
        result.put("fieldDefine", MaterialTypeEnum.fieldDefine(type));
        DictDataDO dictDataDO = dataService.parseDictData("material-template", type);
        if (Objects.nonNull(dictDataDO)) {
            result.put("templateUrl", dictDataDO.getValue());
        }
        return result;
    }

    @Override
    public String parseToRedis(MultipartFile file) {
        // sheet 名为素材类型 同步解析文字 解析图片需并发处理
        long start = System.currentTimeMillis();
        String parseUid = IdUtil.fastSimpleUUID();
        try {
            String materialType = ExcelUtil.getReader(file.getInputStream()).getSheet().getSheetName();
            List<? extends AbstractBaseCreativeMaterialDTO> result = ExcelUtils.read(file, MaterialTypeEnum.of(materialType).getAClass());
            for (AbstractBaseCreativeMaterialDTO abstractBaseCreativeMaterialDTO : result) {
                abstractBaseCreativeMaterialDTO.setType(materialType);
            }
            long end = System.currentTimeMillis();
            log.info("material parse success, {} ms", end - start);
            redisTemplate.boundValueOps("material_parse_" + parseUid).set(JsonUtils.toJsonString(result), 3, TimeUnit.DAYS);
        } catch (IOException e) {
            log.info("material parse error");
            throw exception(MATERIAL_PARSE_ERROR, e.getMessage());
        }
        return parseUid;
    }

    @Override
    public ParseResult parseResult(String parseUid) {
        String json = redisTemplate.boundValueOps(prefix + parseUid).get();
        if (StringUtils.isBlank(json)) {
            return new ParseResult();
        }
        List<AbstractBaseCreativeMaterialDTO> materialDTOList = JsonUtils.parseArray(json, AbstractBaseCreativeMaterialDTO.class);
        return new ParseResult(true, materialDTOList);
    }
}
