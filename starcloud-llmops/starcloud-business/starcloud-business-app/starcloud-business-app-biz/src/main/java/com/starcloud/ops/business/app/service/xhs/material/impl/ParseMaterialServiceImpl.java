package com.starcloud.ops.business.app.service.xhs.material.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import com.starcloud.ops.business.app.api.xhs.material.UploadMaterialImageDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.ParseMaterialService;
import com.starcloud.ops.business.app.service.xhs.material.UploadMaterialImageManager;
import com.starcloud.ops.business.app.util.MaterialTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.*;
import static com.starcloud.ops.business.app.enums.xhs.CreativeConstants.*;

@Slf4j
@Service
public class ParseMaterialServiceImpl implements ParseMaterialService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private UploadMaterialImageManager uploadMaterialImageManager;

    @Override
    public Map<String, Object> template(String type) {
        Map<String, Object> result = new HashMap<>();
        result.put("fieldDefine", MaterialTypeEnum.fieldDefine(type));
        return result;
    }

    @Override
    public void downloadTemplate(String materialType, HttpServletResponse response) {
        try {
            File file = MaterialTemplateUtils.readTemplate(materialType);
            IoUtil.write(response.getOutputStream(), false, FileUtil.readBytes(file));
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        } catch (Exception e) {
            log.error("download template error", e);
            throw exception(DOWNLOAD_TEMPLATE_ERROR, e.getMessage());
        }
    }

    @Override
    public ParseResult parseResult(String parseUid) {
        String error = redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).get();
        if (StringUtils.isNoneBlank(error)) {
            throw exception(MATERIAL_PARSE_ERROR, error);
        }
        String json = redisTemplate.boundValueOps(MATERIAL_PREFIX + parseUid).get();
        if (StringUtils.isBlank(json)) {
            return new ParseResult();
        }
        List<AbstractBaseCreativeMaterialDTO> materialDTOList = JsonUtils.parseArray(json, AbstractBaseCreativeMaterialDTO.class);
        return new ParseResult(true, materialDTOList);
    }

    @Override
    public String parseToRedis(MultipartFile file) {
        String parseUid = IdUtil.fastSimpleUUID();
        long start = System.currentTimeMillis();
        try {
            //文件名 {materialType}.zip
            String[] split = file.getOriginalFilename().split("\\.");
            if (split.length != 2 || !Objects.equals("zip", split[1])) {
                throw exception(NOT_ZIP_PACKAGE);
            }
            String materialType = split[0];
            MaterialTypeEnum materialTypeEnum = MaterialTypeEnum.of(materialType);

            //     系统默认临时文件目录/material/parseUid
            String dirPath = TMP_DIR_PATH + File.separator + parseUid;
            File dir = new File(dirPath);
            dir.mkdirs();
            File zipFile = new File(dirPath + File.separator + file.getOriginalFilename());
            file.transferTo(zipFile);
            ZipUtil.unzip(zipFile, dir, StandardCharsets.UTF_8);
            // 读取excel 第一行为表结构说明 第二行为表头
            FileInputStream excelFile = new FileInputStream(dirPath + File.separator + materialType + File.separator + materialType + ".xlsx");
            List<? extends AbstractBaseCreativeMaterialDTO> result = ExcelUtils.read(excelFile, materialTypeEnum.getAClass(), 2);
            for (AbstractBaseCreativeMaterialDTO abstractBaseCreativeMaterialDTO : result) {
                abstractBaseCreativeMaterialDTO.valid();
                abstractBaseCreativeMaterialDTO.setType(materialType);
            }
            // 提交上传任务
            UploadMaterialImageDTO uploadMaterialDTO = new UploadMaterialImageDTO(materialType, parseUid, result);
            if (uploadMaterialDTO.containsImage()) {
                // 包含图片上传
                uploadMaterialImageManager.submit(uploadMaterialDTO);
            } else {
                redisTemplate.boundValueOps(MATERIAL_PREFIX + parseUid).set(JsonUtils.toJsonString(result), 3, TimeUnit.DAYS);
            }
            long end = System.currentTimeMillis();
            log.info("material parse success, parseUid={}, {} ms", parseUid, end - start);
            return parseUid;
        } catch (Exception e) {
            log.info("material parse error", e);
            throw exception(MATERIAL_PARSE_ERROR, e.getMessage());
        }

    }


}
