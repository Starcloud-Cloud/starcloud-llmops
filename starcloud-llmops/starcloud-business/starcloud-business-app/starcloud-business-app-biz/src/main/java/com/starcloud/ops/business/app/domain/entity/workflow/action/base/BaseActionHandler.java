package com.starcloud.ops.business.app.domain.entity.workflow.action.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.kstry.framework.core.annotation.NoticeResult;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.app.workflow.app.process.AppProcessParser;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@SuppressWarnings("all")
public abstract class BaseActionHandler<Q, R> {

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

    /**
     * 执行具体的步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract ActionResponse doExecute(Q request);

    /**
     * 获取应用的UID
     *
     * @return 应用的UID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getAppUid() {
        return this.getAppContext().getApp().getUid();
    }

    /**
     * 获取当前handler消耗的权益类型，如果返回自动扣除权益，返回null,则不处理权益扣除
     *
     * @return 权益类型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected BenefitsTypeEnums getBenefitsType() {
        return BenefitsTypeEnums.COMPUTATIONAL_POWER;
    }


    /**
     * 获取当前handler消耗的权益点数
     *
     * @return 权益点数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract Integer getCostPoints(Q request);

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @NoticeResult
    protected ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        log.info("Action 执行开始...");
        try {
            Optional<String> property = scopeDataOperator.getTaskProperty();

            AppProcessParser.ServiceTaskPropertyDTO serviceTaskPropertyDTO = JSONUtil.toBean(property.get(), AppProcessParser.ServiceTaskPropertyDTO.class);
            context.setStepId(serviceTaskPropertyDTO.getStepId());

            this.appContext = context;
            Q request = this.parseInput();

            // 执行具体的步骤
            ActionResponse actionResponse = this.doExecute(request);

            //设置到上下文中
            this.appContext.setActionResponse(actionResponse);

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

            // 权益放在此处是为了准确的扣除权益 并且控制不同action不同权益的情况
            if (this.getBenefitsType() != null && actionResponse.getCostPoints() > 0 && actionResponse.getTotalTokens() > 0) {
                // 权益类型
                BenefitsTypeEnums benefitsType = this.getBenefitsType();
                // 权益点数
                Integer costPoints = actionResponse.getCostPoints();
                // 扣除权益
                ADMIN_USER_RIGHTS_API.reduceRights(
                        context.getUserId(), // 用户ID
                        AdminUserRightsTypeEnum.MAGIC_BEAN, // 权益类型
                        costPoints, // 权益点数
                        UserRightSceneUtils.getUserRightsBizType(context.getScene().name()).getType(), // 业务类型
                        context.getConversationUid() // 会话ID
                );
                log.info("扣除权益成功，权益类型：{}，权益点数：{}，用户ID：{}，会话ID：{}", benefitsType.getCode(), costPoints, context.getUserId(), context.getConversationUid());
            }

            log.info("Action 执行成功...");
            return actionResponse;
        } catch (ServiceException exception) {
            log.error("Action 执行异常：异常码: {}, 异常信息: {}", exception.getCode(), exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Action 执行失败：异常信息: {}", exception.getMessage());
            throw exception;
        }
    }

    /**
     * 解析输入参数
     *
     * @return 输入参数
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    protected Q parseInput() {
        Map<String, Object> stepParams = this.appContext.getContextVariablesValues();
        // 将 MODEL 传入到 stepParams 中
        stepParams.put("MODEL", this.appContext.getAiModel());
        // 将 N 传入到 stepParams 中
        stepParams.put("N", this.appContext.getN());

        Type query = TypeUtil.getTypeArgument(this.getClass());
        Class<Q> inputCls = (Class<Q>) query;
        return BeanUtil.toBean(new HashMap<String, Object>() {
            private static final long serialVersionUID = -5958990436311575905L;

            {
                put("stepParams", stepParams);
            }
        }, inputCls);
    }


    /**
     * 因为 parseInput 内逻辑已经约定了Request实体，所以实体都放在这里，如果要新定义实体也需要复写parseInput
     */

    /**
     * 请求实体
     */
    @Data
    public static class Request {

        /**
         * 老参数直接传入
         */
        @Deprecated
        private Map<String, Object> stepParams;


        /**
         * 后续新参数 都是一个个独立字段即可
         */
        private String prompt;


        private Boolean enabledDateset = false;

        /**
         * 数据集支持
         */
        private List<String> datesetList;

    }

    /**
     * 响应实体
     */
    @Data
    public static class Response {

        private String content;

        public Response(String content) {
            this.content = content;
        }
    }

}
