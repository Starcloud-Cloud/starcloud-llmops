package com.starcloud.ops.business.app.service.image;

import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.ImageTask;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.TaskExecuteRequest;

import java.util.List;
import java.util.Map;

/**
 * 生成商品图服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
public interface ProductImageService {

    /**
     * 获取元数据
     *
     * @return 元数据
     */
    Map<String, List<ImageMetaDTO>> metadata();

    /**
     * 获取任务列表
     *
     * @return 任务列表
     */
    List<ImageTask> list();

    /**
     * 获取任务
     *
     * @param id 任务ID
     * @return 任务详情
     */
    ImageTask get(String id);

    /**
     * 保存任务
     *
     * @param request 请求
     * @return 任务详情
     */
    ImageTask save(ImageTask request);

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    void delete(String id);

    /**
     * 执行任务
     *
     * @param request 请求
     * @return 任务详情
     */
    ImageTask execute(TaskExecuteRequest request);
}
