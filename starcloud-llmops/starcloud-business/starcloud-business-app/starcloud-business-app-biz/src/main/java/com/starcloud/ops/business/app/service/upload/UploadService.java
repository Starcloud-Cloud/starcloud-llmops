package com.starcloud.ops.business.app.service.upload;

import cn.iocoder.yudao.framework.file.core.client.FileClient;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;

/**
 * 图片上传服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface UploadService {

    /**
     * 根据条件获取文件客户端
     *
     * @param tenantId 租户ID
     */
    FileClient getFileClient(Long tenantId);

    /**
     * 保存文件，并返回文件的访问路径
     *
     * @param request 上传请求
     * @return 文件路径
     */
    String createFile(UploadRequest request) throws Exception;

    /**
     * 获取文件内容
     *
     * @param path 文件路径
     * @return 文件内容
     */
    byte[] getContent(Long tenantId, String path) throws Exception;

    /**
     * 上传图片
     *
     * @param request 上传请求
     * @return 图片信息
     */
    UploadImageInfoDTO uploadImage(UploadImageRequest request);

}
