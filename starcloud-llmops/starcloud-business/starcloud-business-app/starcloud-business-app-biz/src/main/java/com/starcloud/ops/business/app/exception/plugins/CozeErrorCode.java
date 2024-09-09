package com.starcloud.ops.business.app.exception.plugins;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Data
public class CozeErrorCode extends ErrorCode {

    private static Integer DEF_ERROR_CODE = 760100012;

    private String originalMessage = "";

    public CozeErrorCode(String message) {

        super(DEF_ERROR_CODE, "coze工作流输出参数应该是json对象或者集合对象");

        this.setOriginalMessage(message);
    }


    @Override
    public Integer getCode() {
        String BizStatusCode = "BizStatusCode:\\[(\\d+)\\]";
        String findBizStatusCode = findPatternError(BizStatusCode, this.getOriginalMessage());

        if (StrUtil.isNotBlank(findBizStatusCode)) {
            return Integer.valueOf(findBizStatusCode);
        }

        return super.getCode();
    }

    @Override
    public String getMsg() {

        try {

            ErrorCode errorCode =  this.checkUserLimitation();
            if (ObjectUtil.isNotNull(errorCode)) {
                return errorCode.getMsg();
            }

            ErrorCode blockTypeCode =  this.checkBlockType();
            if (ObjectUtil.isNotNull(blockTypeCode)) {
                return blockTypeCode.getMsg();
            }

            ErrorCode bizError =  this.checkBizError();
            if (ObjectUtil.isNotNull(bizError)) {
                return bizError.getMsg();
            }

        } catch (Exception e) {

            log.error("CozeErrorCode getMsg is fail", e);
        }

        return super.getMsg();
    }


    private ErrorCode checkUserLimitation() {

        String errType = "ErrType:\\[(\\w+)\\]";
        String findErrType = findPatternError(errType, this.getOriginalMessage());

        List<String> messageList = new ArrayList();
        messageList.add("["+findErrType+"]");


        if (ReUtil.contains("block_type=UserLimitation", this.getOriginalMessage())) {
            messageList.add("coze工作流执行限流");
            return new ErrorCode(DEF_ERROR_CODE,  StrUtil.join(" ", messageList));
        }

        if (StrUtil.isNotBlank(findErrType)) {
            return new ErrorCode(DEF_ERROR_CODE,  findErrType);
        }

        return null;
    }


    private ErrorCode checkBlockType() {

        String blockType = findPatternError("block_type=(\\w+)", this.getOriginalMessage());

        if (StrUtil.isNotBlank(blockType)) {
            return new ErrorCode(DEF_ERROR_CODE,  blockType);

        }
        return null;
    }

    private ErrorCode checkBizError() {

        String bizCode = findPatternError("biz error: code=(\\d+)", this.getOriginalMessage());
        String bizError = findPatternError("msg=(.*)", this.getOriginalMessage());

        if (StrUtil.isNotBlank(bizCode)) {
            return new ErrorCode(Integer.valueOf(bizCode),  bizError);

        }

        return null;
    }


    private static String findPatternError(String escape , String content) {

//        escape = ReUtil.escape(escape);
        Pattern pattern = Pattern.compile(escape, Pattern.DOTALL);
        return  ReUtil.get(pattern, content, 1);
    }


    public static void main(String[] args) {


        String error = "RPCError{PSM:[ocean.cloud.plugin] Method:[DoAction] ErrType:[RPC_STATUS_CODE_NOT_ZERO] OriginalErr:[<nil>] BizStatusCode:[702093204] BizStatusMessage:[run workflow failed err=RPCError{PSM:[ocean.cloud.workflow] Method:[RunFlow] ErrType:[RPC_STATUS_CODE_NOT_ZERO] OriginalErr:[<nil>] BizStatusCode:[702090900] BizStatusMessage:[Execute Fail: {\\\"SessionID\\\":0,\\\"ErrorCode\\\":\\\"\\\",\\\"ErrorMessage\\\":\\\"\\\",\\\"OutData\\\":\\\"\\\",\\\"Type\\\":\\\"\\\",\\\"BaseResp\\\":{\\\"StatusMessage\\\":\\\"blocked by record, block_type=UserLimitation\\\",\\\"StatusCode\\\":0,\\\"extra\\\":{\\\"is_system_error\\\":\\\"false\\\",\\\"rpc_kerror\\\":\\\"{\\\\\\\"error_code\\\\\\\":\\\\\\\"702112103\\\\\\\",\\\\\\\"code_id\\\\\\\":0,\\\\\\\"code_alias\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"no_err_meta\\\\\\\":true,\\\\\\\"cause_chain\\\\\\\":[\\\\\\\"@flow.flowservice.flow_gateway@RunFlowByApi: BError: check in upstream\\\\\\\"],\\\\\\\"user_message\\\\\\\":\\\\\\\"blocked by record, block_type=UserLimitation\\\\\\\",\\\\\\\"with_user_msg\\\\\\\":true}\\\",\\\"stack\\\":\\\"\\\\nkerror.KRpcResponseCheck\\\\n    /opt/tiger/compile_path/pkg/mod/code.byted.org/apaas/kcommon@v1.3.1/specifications/kerror/rpc_error.go:160\\\\ngateway.RunFlowByAPISync\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/integration/gateway/gateway_client.go:56\\\\nservice.RuntimeServiceImpl.RunFlowSync\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/app/fdl_runtime/service/runtime_service.go:100\\\\nmain.(*FDLRuntimeServiceImpl).RunFlowSync\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/app/fdl_runtime/handler.go:22\\\\nfdlruntimeservice.runFlowSyncHandler\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/app/fdl_runtime/kitex_gen/lark/apaas/fdl_runtime/fdlruntimeservice/fdlruntimeservice.go:48\\\\nserver.(*server).invokeHandleEndpoint.func1\\\\n    /opt/tiger/compile_path/pkg/mod/github.com/cloudwego/kitex@v0.9.1/server/server.go:332\\\\nserver.newErrorHandleMW.func1.1\\\\n    /opt/tiger/compile_path/pkg/mod/github.com/cloudwego/kitex@v0.9.1/server/server.go:125\\\\nmetrics.KitexAPIMetricsWithTenantAndNamespace.func1.1\\\\n    /opt/tiger/compile_path/pkg/mod/code.byted.org/apaas/automation_dx_sdk@v1.0.7-0.20240122072740-01945d11ea8c/utils/metrics/method_middleware.go:31\\\\nuserlog.KitexAPICollectorMiddleware.func1.1\\\\n    /opt/tiger/compile_path/pkg/mod/code.byted.org/apaas/automation_dx_sdk@v1.0.7-0.20240122072740-01945d11ea8c/userlog/middleware.go:53\\\\nmiddleware.PrintRequestAndResponseMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:54\\\\nmiddleware.RemoteServiceCallLogMW.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:145\\\\nmiddleware.ContextStressKeyMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:71\\\\nmiddleware.KCtxMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:100\\\\nmiddleware.PrintPanicStackMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:128\\\\nmiddleware.MultiBranchMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:191\\\\nmiddleware.InitMetadataSdkCacheVersion.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:182\\\"},\\\"KStatusCode\\\":\\\"702112103\\\"}} ]}]}";
        //String error = "biz error: code=702093204, msg=Workflow execute failed";

        CozeErrorCode errorCode =  new CozeErrorCode(error);


        System.out.println(errorCode.getMsg());

        System.out.println(errorCode.getCode());


    }

}
