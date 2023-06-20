package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.domain.entity.action.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.config.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.repository.AppRepository;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import lombok.Data;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class AppEntity {

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    private String uid;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    private String model;

    /**
     * 应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用
     */
    private String type;

    /**
     * 应用来源类型：表示应用的是从那个平台创建，或者下载的。
     */
    private String source;

    /**
     * 应用标签，多个以逗号分割
     */
    private List<String> tags;

    /**
     * 应用类别，多个以逗号分割
     */
    private List<String> categories;

    /**
     * 应用场景，多个以逗号分割
     */
    private List<String> scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    private List<String> images;

    /**
     * 应用图标
     */
    private String icon;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    private WorkflowConfigEntity workflowConfig;

    /**
     * 应用聊天配置
     */
    private ChatConfigEntity chatConfig;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用上传成功后，应用市场 UID
     */
    private String uploadUid;

    /**
     * 应用下载成功后，应用市场 UID
     */
    private String downloadUid;

    /**
     * 最后一次上传到应用市场时间
     */
    private LocalDateTime lastUpload;

    @Resource
    private AppRepository appRepository;

    /**
     * 校验
     */
    public void validate() {
        if (AppModelEnum.COMPLETION.name().equals(this.model)) {
            workflowConfig.validate();
        } else if (AppModelEnum.CHAT.name().equals(this.model)) {
            chatConfig.validate();
        }
    }

    /**
     * 根据 uid 获取应用
     *
     * @return AppEntity
     */
    public AppEntity entityByUid() {
        return appRepository.getByUid(this.uid);
    }

    /**
     * 新增应用
     */
    public void insert() {
        validate();
        appRepository.insert(this);
    }

    /**
     * 更新应用
     */
    public void update() {
        validate();
        appRepository.update(this);
    }

    /**
     * 删除应用
     */
    public void deleteByUid() {
        appRepository.deleteByUid(this.uid);
    }

    /**
     * 应用步骤图标、多个以逗号分割
     *
     * @return List<String>
     */
    public List<String> getActionIcons() {
        return CollectionUtil.emptyIfNull(this.workflowConfig.getSteps()).stream()
                .map(WorkflowStepWrapper::getFlowStep)
                .map(WorkflowStepEntity::getIcon)
                .collect(Collectors.toList());
    }

}
