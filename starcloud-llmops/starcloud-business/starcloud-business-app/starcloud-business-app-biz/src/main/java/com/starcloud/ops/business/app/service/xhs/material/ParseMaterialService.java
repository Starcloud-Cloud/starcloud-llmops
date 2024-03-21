package com.starcloud.ops.business.app.service.xhs.material;

import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ParseMaterialService {

    /**
     * 模板详情
     *
     * @return
     */
    Map<String, Object> template(String type);

    /**
     * 下载zip模板
     *
     * @param materialType
     * @param response
     */
    void downloadTemplate(String materialType, HttpServletResponse response);

    /**
     * 解析结构缓存到redis
     *
     * @param file
     * @return 解析任务uid
     */
    String parseToRedis(MultipartFile file);

    /**
     * 查询解析结果
     *
     * @param parseUid 解析任务uid
     * @return
     */
    ParseResult parseResult(String parseUid);
}
