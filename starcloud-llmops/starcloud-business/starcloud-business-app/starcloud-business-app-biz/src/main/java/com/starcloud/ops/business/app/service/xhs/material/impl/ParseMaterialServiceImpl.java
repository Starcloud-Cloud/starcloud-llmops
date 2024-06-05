package com.starcloud.ops.business.app.service.xhs.material.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONException;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.api.xhs.material.UploadMaterialImageDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.MaterialUploadReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ParseXhsReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.material.ParseMaterialService;
import com.starcloud.ops.business.app.service.xhs.material.UploadMaterialImageManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.MaterialTemplateUtils;
import com.starcloud.ops.business.app.util.UnpackUtils;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Resource
    private CreativePlanService creativePlanService;

    @Override
    public Map<String, Object> template(String type) {
        Map<String, Object> result = new HashMap<>();
        result.put("fieldDefine", MaterialTypeEnum.fieldDefine(type));
        return result;
    }

    @Override
    public void downloadTemplate(String uid, String planSource, HttpServletResponse response) {
        AppMarketRespVO appMarketResponse = creativePlanService.getAppRespVO(uid, planSource);
        List<MaterialFieldConfigDTO> materialConfig = MaterialDefineUtil.getMaterialConfig(appMarketResponse);

        try {
            List<String> excelHeader = materialConfig.stream().map(MaterialFieldConfigDTO::getDesc).collect(Collectors.toList());
            String zipNamePrefix = appMarketResponse.getName() + MaterialTemplateUtils.DIVIDER + "模板";
            String excelNamePrefix = "导入模板";
            File file = MaterialTemplateUtils.readTemplate(zipNamePrefix, excelNamePrefix, uid, excelHeader);
            IoUtil.write(response.getOutputStream(), false, FileUtil.readBytes(file));
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        } catch (JSONException e) {
            log.error("JSON Exception", e);
            throw exception(DOWNLOAD_TEMPLATE_ERROR, "自定义配置解析错误");
        } catch (Exception e) {
            log.error("generation template error", e);
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
        return new ParseResult(true, MaterialDefineUtil.parseData(json));
    }

    @Override
    public String parseToRedis(MaterialUploadReqVO uploadReqVO) {
        String parseUid = IdUtil.fastSimpleUUID();
        long start = System.currentTimeMillis();
        MultipartFile file = uploadReqVO.getFile();

        AppMarketRespVO appMarketResponse = creativePlanService.getAppRespVO(uploadReqVO.getUid(), uploadReqVO.getPlanSource());

        List<MaterialFieldConfigDTO> materialConfigList = MaterialDefineUtil.getMaterialConfig(appMarketResponse);

        try {
            //     系统默认临时文件目录/material/parseUid
            String dirPath = Paths.get(MATERIAL_TMP_DIR_PATH, parseUid).toString();
            File dir = new File(dirPath);
            dir.mkdirs();

            File zipFile = Paths.get(dirPath, file.getOriginalFilename()).toFile();
            file.transferTo(zipFile);
            UnpackUtils.unpack(zipFile, dir);
            // 解析excel文件   解压文件下/目录/excel.xlsx
            // 压缩包解压后下面的目录
            File[] childrenDirs = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if (childrenDirs == null) {
                throw exception(EXCEL_NOT_EXIST);
            }

            File excel = null;
            File unzipDir = null;
            for (File childrenDir : childrenDirs) {
                File[] excelFiles = childrenDir.listFiles((File pathname) -> {
                    String[] split = pathname.getName().split("\\.");
                    if (split.length < 2) {
                        return false;
                    }
                    String suffix = split[split.length - 1];
                    // 筛选出文件名为 导入模板.xlsx 的文件
                    if (pathname.isFile() && "导入模板.xlsx".equalsIgnoreCase(pathname.getName())) {
                        return true;
                    }
                    // 从子目录中找后缀为xlsx的文件 且开头不为.
//                    if (pathname.isFile() && "xlsx".equals(suffix) && !pathname.getName().startsWith(".")) {
//                        return true;
//                    }
                    return false;
                });
                if (excelFiles != null && excelFiles.length > 0) {
                    excel = excelFiles[0];
                    unzipDir = childrenDir;
                    break;
                }
            }

            if (Objects.isNull(excel)) {
                throw exception(EXCEL_NOT_EXIST);
            }
            // 读取excel 第一行为表结构说明 第二行为表头
            ExcelReader reader = ExcelUtil.getReader(excel);
            MaterialDefineUtil.addHeaderAlias(reader, materialConfigList);
            List<Map<String, Object>> allExcel = reader.read(1, 2, Integer.MAX_VALUE);
            List<Object> header = reader.readRow(1);
            // 校验表头
            MaterialDefineUtil.verifyExcelHeader(header, materialConfigList);
            // 移除非定义字段 校验必填
            MaterialDefineUtil.cleanMaterialData(materialConfigList, allExcel);
            MaterialDefineUtil.verifyMaterialData(materialConfigList, allExcel);

            // 提交上传任务
            UploadMaterialImageDTO uploadMaterialDTO = new UploadMaterialImageDTO(parseUid, allExcel, materialConfigList, "");
            if (uploadMaterialDTO.containsImage()) {

                uploadMaterialDTO.setUnzipDir(unzipDir.getAbsolutePath());
                // 包含图片上传
                uploadMaterialImageManager.submit(uploadMaterialDTO);
            } else {
                redisTemplate.boundValueOps(MATERIAL_PREFIX + parseUid).set(JSONObject.toJSONString(allExcel), 3, TimeUnit.DAYS);
            }
            long end = System.currentTimeMillis();
            log.info("material parse success, parseUid={}, {} ms", parseUid, end - start);
            return parseUid;
        } catch (FileNotFoundException e) {
            log.info("file not found ", e);
            throw exception(TEMP_IS_NOT_EXIST, e.getMessage());
        } catch (Exception e) {
            log.info("material parse error", e);
            throw exception(MATERIAL_PARSE_ERROR, e.getMessage());
        }
    }

    @Override
    public List<AbstractCreativeMaterialDTO> parseXhs(ParseXhsReqVO parseXhsReqVO) {
        return uploadMaterialImageManager.parseXhs(parseXhsReqVO);
    }

}
