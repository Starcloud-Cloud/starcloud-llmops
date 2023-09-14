package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.ImageTask;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.ImageTaskConfig;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.ImageTaskResult;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.TaskExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.image.vo.task.TaskImageVO;
import com.starcloud.ops.business.app.enums.image.ImageTaskConfigTypeEnum;
import com.starcloud.ops.business.app.service.image.ProductImageService;
import com.starcloud.ops.business.app.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class ProductImageServiceImpl implements ProductImageService {

    private static final String TASK_PREFIX = "image:product:task:";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取元数据
     *
     * @return 元数据
     */
    @Override
    public Map<String, List<ImageMetaDTO>> metadata() {
        Map<String, List<ImageMetaDTO>> metadata = new HashMap<>(2);
        metadata.put("imageTaskConfigType", ImageUtils.configTaskType());
        metadata.put("productTemplateList", ImageUtils.productTemplates());
        return metadata;
    }

    /**
     * 获取任务列表
     *
     * @return 任务列表
     */
    @Override
    public List<ImageTask> list() {
        String taskKey = getTaskKey();
        return redisTemplate.<String, ImageTask>opsForHash().values(taskKey);
    }

    /**
     * 获取任务
     *
     * @param id          任务ID
     * @param executionId 执行ID
     * @return 任务详情
     */
    @Override
    public ImageTask get(String id) {
        String taskKey = getTaskKey();
        if (redisTemplate.opsForHash().hasKey(taskKey, id)) {
            ImageTask imageTask = (ImageTask) redisTemplate.opsForHash().get(taskKey, id);
            if (imageTask == null) {
                throw new IllegalArgumentException("任务不存在");
            }
            return imageTask;
        }
        throw new IllegalArgumentException("任务不存在");
    }

    /**
     * 保存任务
     *
     * @param request 请求
     * @return 任务详情
     */
    @Override
    public ImageTask save(ImageTask request) {
        String taskKey = getTaskKey();
        if (request == null || StringUtils.isBlank(request.getId())) {
            ImageTask task = new ImageTask();
            task.setId(IdUtil.fastSimpleUUID());
            task.setName("商品图任务-" + RandomUtil.randomNumbers(6));
            task.setCreateTime(new Date());
            task.setType(1);
            task.setSequence(0);
            ImageTaskConfig config = new ImageTaskConfig();
            config.setId(IdUtil.fastSimpleUUID());
            config.setTaskId(task.getId());
            config.setType(ImageTaskConfigTypeEnum.TEXT_PROMPT.getCode());
            config.setCreateTime(new Date());
            config.setSamples(4);
            task.setConfig(config);
            redisTemplate.opsForHash().put(taskKey, task.getId(), task);
            return task;
        }

        redisTemplate.opsForHash().put(taskKey, request.getId(), request);
        return request;
    }

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    @Override
    public void delete(String id) {
        redisTemplate.opsForHash().delete(getTaskKey(), id);
    }

    /**
     * 执行任务
     *
     * @param request 请求
     * @return 任务详情
     */
    @Override
    @Async
    public ImageTask execute(TaskExecuteRequest request) {
        String executionId = IdUtil.fastSimpleUUID();
        ImageTask imageTask = this.get(request.getTaskId());
        imageTask.setSequence(imageTask.getSequence() + 1);
        List<ImageTaskResult> taskResult = CollectionUtil.emptyIfNull(imageTask.getResult());
        ImageTaskResult result = initImageTaskResult(imageTask, executionId);
        imageTask.setResult(Collections.singletonList(result));
        this.save(imageTask);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return this.get(request.getTaskId());
    }

    /**
     * 获取任务的 Redis Key
     *
     * @return 任务的 Redis Key
     */
    private String getTaskKey() {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            throw new IllegalArgumentException("用户未登录");
        }
        return TASK_PREFIX + loginUserId;
    }

    /**
     * 初始化任务结果
     *
     * @param imageTask   任务结果
     * @param executionId 执行id
     * @return 任务结果
     */
    private ImageTaskResult initImageTaskResult(ImageTask imageTask, String executionId) {
        ImageTaskResult result = new ImageTaskResult();
        result.setTaskId(imageTask.getId());
        result.setExecutionId(executionId);
        result.setSequence(imageTask.getSequence());
        result.setImage(imageTask.getImage());
        result.setConfig(imageTask.getConfig());
        List<TaskImageVO> imageVOList = new ArrayList<>();
        for (int i = 0; i < imageTask.getConfig().getSamples(); i++) {
            TaskImageVO imageVO = new TaskImageVO();
            imageVO.setStatus("Pending");
            imageVOList.add(imageVO);
        }
        result.setResult(imageVOList);
        result.setExecutionTime(new Date());
        return result;
    }
}
