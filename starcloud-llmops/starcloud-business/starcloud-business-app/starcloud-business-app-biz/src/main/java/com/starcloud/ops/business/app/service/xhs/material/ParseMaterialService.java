package com.starcloud.ops.business.app.service.xhs.material;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ParseXhsReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
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
     * 应用市场 应用配置区分版本  使用planUid
     * 我的应用 使用appUid
     *
     * @param uid
     * @param response
     */
    void downloadTemplate(String uid, String planSource, HttpServletResponse response);

    /**
     * 解析结构缓存到redis
     *
     * @param file
     * @return 解析任务uid
     */
    String parseToRedis(MultipartFile file);

    /**
     * 解析小红书内容
     */
    List<AbstractCreativeMaterialDTO> parseXhs(ParseXhsReqVO parseXhsReqVO);

    /**
     * 查询解析结果
     *
     * @param parseUid 解析任务uid
     * @return
     */
    ParseResult parseResult(String parseUid);
}
