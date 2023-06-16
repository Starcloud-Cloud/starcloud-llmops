package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppEntity implements Serializable {

    private static final long serialVersionUID = -8398050291040492913L;

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
    private AppConfigEntity config;

    /**
     * 应用聊天配置
     */
    private AppChatConfigDTO chatConfig;

    /**
     * 应用步骤图标、多个以逗号分割
     */
    private List<String> stepIcons;

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
     * 应用状态，0：启用，1：禁用
     */
    private Integer status;

    /**
     * 最后一次上传到应用市场时间
     */
    private LocalDateTime lastUpload;


    public void validate() {
        AppValidate.notBlank(this.name, ErrorCodeConstants.APP_NAME_REQUIRED);
        AppValidate.notBlank(this.model, ErrorCodeConstants.APP_MODEL_REQUIRED);
        AppValidate.isTrue(IEnumable.contains(this.model, AppModelEnum.class), ErrorCodeConstants.APP_MODEL_UNSUPPORTED, this.model);
        AppValidate.notBlank(this.type, ErrorCodeConstants.APP_TYPE_REQUIRED);
        AppValidate.isTrue(IEnumable.contains(this.type, AppTypeEnum.class), ErrorCodeConstants.APP_TYPE_UNSUPPORTED, this.type);
        AppValidate.notBlank(this.source, ErrorCodeConstants.APP_SOURCE_REQUIRED);
        AppValidate.isTrue(IEnumable.contains(this.source, AppSourceEnum.class), ErrorCodeConstants.APP_SOURCE_UNSUPPORTED, this.source);
        AppValidate.notEmpty(this.categories, ErrorCodeConstants.APP_CATEGORY_REQUIRED);
        // 场景处理，必须要有默认场景。没有的话，加上默认场景

        // 配置信息校验逻辑
        if (AppModelEnum.COMPLETION.name().equals(this.model)) {
            // 生成式应用，必须要有详细配置
            AppValidate.notNull(this.config, ErrorCodeConstants.APP_CONFIG_REQUIRED);
            // this.config.validate();
        } else if (AppModelEnum.CHAT.name().equals(this.model)) {
            // 聊天式应用，必须要有聊天配置
            AppValidate.notNull(this.chatConfig, ErrorCodeConstants.APP_CHAT_CONFIG_REQUIRED);
            // this.chatConfig.validate();
        }

    }

}
