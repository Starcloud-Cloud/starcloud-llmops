package com.starcloud.ops.business.app.domain.entity.workflow.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeVar;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.poster.PosterStyleEntity;
import com.starcloud.ops.business.app.domain.entity.poster.PosterTemplateEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.domain.handler.poster.PosterGenerationHandler;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import com.starcloud.ops.business.app.service.xhs.executor.PosterTemplateThreadPoolHolder;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
@Slf4j
@TaskComponent
public class PosterActionHandler extends BaseActionHandler {

    /**
     * 线程池
     */
    private static final PosterTemplateThreadPoolHolder POSTER_TEMPLATE_THREAD_POOL_HOLDER = SpringUtil.getBean(PosterTemplateThreadPoolHolder.class);

    /**
     * 流程执行器，action 执行入口
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @NoticeVar
    @TaskService(name = "PosterActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        return super.execute(context, scopeDataOperator);
    }

    /**
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @Override
    protected AdminUserRightsTypeEnum getUserRightsType() {
        return AdminUserRightsTypeEnum.MAGIC_IMAGE;
    }

    /**
     * 获取当前handler消耗的权益点数
     *
     * @return 权益点数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Integer getCostPoints() {
        return 0;
    }

    /**
     * 执行OpenApi生成的步骤
     *
     * @return 执行结果
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ActionResponse doExecute() {

        log.info("海报生成 Action 执行开始......");

        Map<String, Object> params = this.getAppContext().getContextVariablesValues();

        //@todo 找上游的段落节点，然后获取节点参数 和 结果 》 step

        //

        // 图片类型参数
        String posterMode = String.valueOf(params.getOrDefault("POSTER_MODE", CreativeSchemeModeEnum.RANDOM_IMAGE_TEXT.name()));
        // 海报模版参数
        String posterStyle = String.valueOf(params.getOrDefault("STEP.CODE.POSTER_STYLE", "{}"));
        // 海报素材参数
        String posterMaterial = String.valueOf(params.getOrDefault("POSTER_MATERIAL", "[]"));
        // 海报内容参数
        String posterContent = String.valueOf(params.getOrDefault("POSTER_CONTENT", "{}"));

        //posterContent 转成 DTO

        //判断上游需要几张图片？


        // 转为海报模版对象
        PosterStyleEntity style = JSONUtil.toBean(posterStyle, PosterStyleEntity.class);
        // 转为图片素材对象
        List<String> posterMaterialList = JSONUtil.toList(posterMaterial, String.class);

        // 校验海报模版
        style.validate();
        // 处理海报模版参数
        style.assemble(posterMode, posterMaterialList, posterContent);

        // 获取线程池
        ThreadPoolExecutor executor = POSTER_TEMPLATE_THREAD_POOL_HOLDER.executor();
        // 任务列表
        List<CompletableFuture<HandlerResponse<PosterGenerationHandler.Response>>> futureList = CollectionUtil.emptyIfNull(style.getTemplateList()).stream()
                .map(item -> CompletableFuture.supplyAsync(() -> poster(item), executor)).collect(Collectors.toList());
        // 任务合并
        CompletableFuture<List<HandlerResponse<PosterGenerationHandler.Response>>> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> futureList.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        // 获取结果
        List<HandlerResponse<PosterGenerationHandler.Response>> handlerResponseList = allFuture.join();
        // 如果有一个失败，则返回失败
        Optional<HandlerResponse<PosterGenerationHandler.Response>> failureOption = handlerResponseList.stream().filter(item -> !item.getSuccess()).findFirst();
        if (failureOption.isPresent()) {
            HandlerResponse<PosterGenerationHandler.Response> failure = failureOption.get();
            log.info("海报生成 Action 执行失败......");
            ActionResponse response = new ActionResponse();
            response.setSuccess(Boolean.FALSE);
            response.setErrorCode(String.valueOf(failure.getErrorCode()));
            response.setErrorMsg(failure.getErrorMsg());
            response.setType(failure.getType());
            response.setIsShow(Boolean.TRUE);
            response.setStepConfig(JSONUtil.toJsonStr(style));
            response.setCostPoints(0);
            return response;
        }

        // 构建响应结果
        List<PosterGenerationHandler.Response> list = handlerResponseList.stream().map(HandlerResponse::getOutput).collect(Collectors.toList());
        ActionResponse response = new ActionResponse();
        response.setSuccess(Boolean.TRUE);
        response.setType(handlerResponseList.get(0).getType());
        response.setIsShow(Boolean.TRUE);
        response.setStepConfig(JSONUtil.toJsonStr(style));
        response.setMessage(JSONUtil.toJsonStr(style));
        response.setAnswer(JSONUtil.toJsonStr(list));
        response.setOutput(JsonData.of(list));
        response.setCostPoints(list.size());
        log.info("海报生成 Action 执行结束......");
        return response;
    }

    /**
     * 生成海报图片
     *
     * @param posterTemplate 海报模版
     * @return 生成结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private HandlerResponse<PosterGenerationHandler.Response> poster(PosterTemplateEntity posterTemplate) {
        try {
            // 构建请求
            PosterGenerationHandler.Request handlerRequest = new PosterGenerationHandler.Request();
            handlerRequest.setId(posterTemplate.getId());
            handlerRequest.setName(posterTemplate.getName());
            handlerRequest.setIsMain(posterTemplate.getIsMain());
            handlerRequest.setIndex(posterTemplate.getIndex());
            Map<String, Object> params = CollectionUtil.emptyIfNull(posterTemplate.getVariables()).stream()
                    .collect(Collectors.toMap(VariableItemEntity::getField, VariableItemEntity::getValue));
            handlerRequest.setParams(params);

            // 构建请求
            HandlerContext<PosterGenerationHandler.Request> handlerContext = HandlerContext.createContext(
                    this.getAppUid(),
                    this.getAppContext().getConversationUid(),
                    this.getAppContext().getUserId(),
                    this.getAppContext().getEndUserId(),
                    this.getAppContext().getScene(),
                    handlerRequest
            );
            PosterGenerationHandler handler = new PosterGenerationHandler();
            return handler.execute(handlerContext);
        } catch (ServiceException exception) {
            log.info("海报图片生成: 生成图片失败(ServiceException): 错误码：{}，错误信息：{}", exception.getCode(), exception.getMessage());
            HandlerResponse<PosterGenerationHandler.Response> handlerResponse = new HandlerResponse<>();
            handlerResponse.setSuccess(Boolean.FALSE);
            handlerResponse.setErrorCode(exception.getCode());
            handlerResponse.setErrorMsg(exception.getMessage());
            return handlerResponse;
        } catch (Exception exception) {
            log.info("海报图片生成: 生成图片失败(Exception): 错误码：{}，错误信息：{}", 350400200, exception.getMessage());
            HandlerResponse<PosterGenerationHandler.Response> handlerResponse = new HandlerResponse<>();
            handlerResponse.setSuccess(Boolean.FALSE);
            handlerResponse.setErrorCode(350400200);
            handlerResponse.setErrorMsg(exception.getMessage());
            return handlerResponse;
        }
    }

}