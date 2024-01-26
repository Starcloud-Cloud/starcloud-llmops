package com.starcloud.ops.business.app.domain.entity.workflow.action.base;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.kstry.framework.core.annotation.NoticeResult;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.app.workflow.app.process.AppProcessParser;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@SuppressWarnings("all")
public abstract class BaseActionHandler extends Object {

    /**
     * 扣除权益
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AdminUserRightsApi ADMIN_USER_RIGHTS_API = SpringUtil.getBean(AdminUserRightsApi.class);

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤描述
     */
    private String description;

    /**
     * 请求应用上下文
     */
    private AppContext appContext;

    private ScopeDataOperator scopeDataOperator;

    /**
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract AdminUserRightsTypeEnum getUserRightsType();

    /**
     * 获取当前handler消耗的权益点数
     *
     * @return 权益点数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract Integer getCostPoints();

    /**
     * 执行具体的步骤
     *
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract ActionResponse doExecute();

    /**
     * 获取应用的UID
     *
     * @return 应用的UID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getAppUid() {
        return Optional.ofNullable(this.getAppContext())
                .map(AppContext::getApp)
                .map(BaseAppEntity::getUid)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_UID_REQUIRED));
    }

    /**
     * 获取应用执行模型
     *
     * @return 应用执行模型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getAiModel() {
        return Optional.ofNullable(this.getAppContext()).map(AppContext::getAiModel).orElse(null);
    }

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        log.info("Action[{}]执行开始，步骤：{}, 当前用户信息 {}, {}, {}, {}", this.getClass().getSimpleName(), context.getStepId(), context.getUserId(), TenantContextHolder.getTenantId(), TenantContextHolder.isIgnore(), SecurityFrameworkUtils.getLoginUser());
        try {
            if (Objects.isNull(context)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CONTEXT_REQUIRED);
            }

            Optional<String> property = scopeDataOperator.getTaskProperty();
            AppProcessParser.ServiceTaskPropertyDTO serviceTaskPropertyDTO = JSONUtil.toBean(property.get(), AppProcessParser.ServiceTaskPropertyDTO.class);
            context.setStepId(serviceTaskPropertyDTO.getStepId());
            // 设置到上下文中
            this.setAppContext(context);
            this.setScopeDataOperator(scopeDataOperator);
            // 执行具体的步骤
            ActionResponse actionResponse = this.doExecute();
            //设置到上下文中
            context.setActionResponse(actionResponse);

            // 执行结果校验, 如果失败，抛出异常
            if (!actionResponse.getSuccess()) {
                String errorCode = StringUtils.isNoneBlank(actionResponse.getErrorCode()) ? actionResponse.getErrorCode() : ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode().toString();
                String errorMsg = StringUtils.isNoneBlank(actionResponse.getErrorMsg()) ? actionResponse.getErrorMsg() : "应用Action执行失败，请稍后重试或者联系管理员！";
                Integer code;
                try {
                    code = Integer.parseInt(errorCode);
                } catch (Exception e) {
                    code = ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode();
                }
                throw ServiceExceptionUtil.exception(new ErrorCode(code, errorMsg));
            }

            AdminUserRightsTypeEnum userRightsType = this.getUserRightsType();
            Integer costPoints = actionResponse.getCostPoints();
            // 权益放在此处是为了准确的扣除权益 并且控制不同action不同权益的情况
            if (userRightsType != null && costPoints > 0) {
                // 扣除权益
                ADMIN_USER_RIGHTS_API.reduceRights(
                        context.getUserId(),null,null, // 用户ID
                        userRightsType, // 权益类型
                        costPoints, // 权益点数
                        UserRightSceneUtils.getUserRightsBizType(context.getScene().name()).getType(), // 业务类型
                        context.getConversationUid() // 会话ID
                );
                log.info("扣除权益成功，权益类型：{}，权益点数：{}，用户ID：{}，会话ID：{}", userRightsType.name(), costPoints, context.getUserId(), context.getConversationUid());
            }

            log.info("Action[{}] 执行成功, 步骤：{} ...", this.getClass().getSimpleName(), context.getStepId());
            return actionResponse;
        } catch (ServiceException exception) {
            log.error("Action[{}] 执行异常： 步骤：{}, 异常码: {}, 异常信息: {}", this.getClass().getSimpleName(), context.getStepId(), exception.getCode(), exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Action[{}] 执行失败：步骤: {}, 异常信息: {}", this.getClass().getSimpleName(), context.getStepId(), exception.getMessage());
            throw exception;
        }
    }

    /**
     * 因为@Data重写了hasCode, equals, 导致子类比较都相等，所以这里改成继承object, 重写equals即可
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {

        boolean ff = super.equals(obj);
        return ff;

    }

}
