package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.starcloud.ops.business.app.api.chat.ChatRequest;
import com.starcloud.ops.business.app.controller.admin.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.repository.market.AppMarketRepository;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@Data
public class AppMarketEntity extends AppEntity<AppExecuteReqVO, LogAppConversationCreateReqVO> {

    /**
     * 应用版本
     */
    private Integer version;

    private String language;


    private String example;

    /**
     * 应用是否是免费的
     */

    private Boolean free;

    /**
     * 应用收费数
     */

    private BigDecimal cost;

    /**
     * 使用数量
     */
    private Integer usageCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 查看数量
     */
    private Integer viewCount;

    /**
     * 安装数量
     */
    private Integer installCount;

    /**
     * 应用市场数据库操作类
     */
    private static AppMarketRepository appMarketRepository;

    /**
     * 获取应用市场数据库操作类
     *
     * @return 应用市场数据库操作类
     */
    public static AppMarketRepository getAppMarketRepository() {
        if (appMarketRepository == null) {
            appMarketRepository = SpringUtil.getBean(AppMarketRepository.class);
        }
        return appMarketRepository;
    }

    /**
     * 校验
     */
    @Override
    protected void _validate() {
        if (AppModelEnum.COMPLETION.name().equals(this.getModel())) {
            getWorkflowConfig().validate();
        } else if (AppModelEnum.CHAT.name().equals(this.getModel())) {
            getChatConfig().validate();
        }
    }

    @Override
    protected LogAppConversationCreateReqVO _execute(AppExecuteReqVO req) {
        return null;
    }

    /**
     * 新增应用
     */
    @Override
    protected void _insert() {

        getAppMarketRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    protected void _update() {

        getAppMarketRepository().update(this);
    }


}
