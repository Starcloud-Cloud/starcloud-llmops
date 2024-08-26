package com.starcloud.ops.business.app.exception.plugins;

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

    private String originalMessage = "";

    public CozeErrorCode(String message) {

        super(760100012, "coze工作流输出参数应该是json对象或者集合对象");

        this.setOriginalMessage(message);
    }


    @Override
    public Integer getCode() {
        String BizStatusCode = "BizStatusCode:\\[(\\d+)\\]";
        String findBizStatusCode = findPatternError(BizStatusCode, this.getOriginalMessage());

        if (StrUtil.isNotBlank(findBizStatusCode)) {
            return Integer.valueOf(findBizStatusCode);
        }

        return this.getCode();
    }

    @Override
    public String getMsg() {

        try {

            String ErrType = "ErrType:\\[(\\w+)\\]";
            String findErrType = findPatternError(ErrType, this.getOriginalMessage());

            List<String> messageList = new ArrayList();
            messageList.add("["+findErrType+"]");

            if (this.isUserLimitation()) {
                messageList.add("coze工作流执行限流");
            } else {
                //@todo
                messageList.add(this.getBlockType());
            }

            return StrUtil.join(" ", messageList);

        } catch (Exception e) {

            log.error("CozeErrorCode getMsg is fail", e);
        }

        return super.getMsg();
    }


    private Boolean isUserLimitation() {

       return ReUtil.contains("block_type=UserLimitation", this.getOriginalMessage());
    }


    private String getBlockType() {
        return findPatternError("block_type=(\\w+)", this.getOriginalMessage());
    }


    private static String findPatternError(String escape , String content) {

//        escape = ReUtil.escape(escape);
        Pattern pattern = Pattern.compile(escape, Pattern.DOTALL);
        return  ReUtil.get(pattern, content, 1);
    }


    public static void main(String[] args) {


        String error = "RPCError{PSM:[ocean.cloud.plugin] Method:[DoAction] ErrType:[RPC_STATUS_CODE_NOT_ZERO] OriginalErr:[<nil>] BizStatusCode:[702093204] BizStatusMessage:[run workflow failed err=RPCError{PSM:[ocean.cloud.workflow] Method:[RunFlow] ErrType:[RPC_STATUS_CODE_NOT_ZERO] OriginalErr:[<nil>] BizStatusCode:[702090900] BizStatusMessage:[Execute Fail: {\\\"SessionID\\\":0,\\\"ErrorCode\\\":\\\"\\\",\\\"ErrorMessage\\\":\\\"\\\",\\\"OutData\\\":\\\"\\\",\\\"Type\\\":\\\"\\\",\\\"BaseResp\\\":{\\\"StatusMessage\\\":\\\"blocked by record, block_type=UserLimitation\\\",\\\"StatusCode\\\":0,\\\"extra\\\":{\\\"is_system_error\\\":\\\"false\\\",\\\"rpc_kerror\\\":\\\"{\\\\\\\"error_code\\\\\\\":\\\\\\\"702112103\\\\\\\",\\\\\\\"code_id\\\\\\\":0,\\\\\\\"code_alias\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"no_err_meta\\\\\\\":true,\\\\\\\"cause_chain\\\\\\\":[\\\\\\\"@flow.flowservice.flow_gateway@RunFlowByApi: BError: check in upstream\\\\\\\"],\\\\\\\"user_message\\\\\\\":\\\\\\\"blocked by record, block_type=UserLimitation\\\\\\\",\\\\\\\"with_user_msg\\\\\\\":true}\\\",\\\"stack\\\":\\\"\\\\nkerror.KRpcResponseCheck\\\\n    /opt/tiger/compile_path/pkg/mod/code.byted.org/apaas/kcommon@v1.3.1/specifications/kerror/rpc_error.go:160\\\\ngateway.RunFlowByAPISync\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/integration/gateway/gateway_client.go:56\\\\nservice.RuntimeServiceImpl.RunFlowSync\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/app/fdl_runtime/service/runtime_service.go:100\\\\nmain.(*FDLRuntimeServiceImpl).RunFlowSync\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/app/fdl_runtime/handler.go:22\\\\nfdlruntimeservice.runFlowSyncHandler\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/app/fdl_runtime/kitex_gen/lark/apaas/fdl_runtime/fdlruntimeservice/fdlruntimeservice.go:48\\\\nserver.(*server).invokeHandleEndpoint.func1\\\\n    /opt/tiger/compile_path/pkg/mod/github.com/cloudwego/kitex@v0.9.1/server/server.go:332\\\\nserver.newErrorHandleMW.func1.1\\\\n    /opt/tiger/compile_path/pkg/mod/github.com/cloudwego/kitex@v0.9.1/server/server.go:125\\\\nmetrics.KitexAPIMetricsWithTenantAndNamespace.func1.1\\\\n    /opt/tiger/compile_path/pkg/mod/code.byted.org/apaas/automation_dx_sdk@v1.0.7-0.20240122072740-01945d11ea8c/utils/metrics/method_middleware.go:31\\\\nuserlog.KitexAPICollectorMiddleware.func1.1\\\\n    /opt/tiger/compile_path/pkg/mod/code.byted.org/apaas/automation_dx_sdk@v1.0.7-0.20240122072740-01945d11ea8c/userlog/middleware.go:53\\\\nmiddleware.PrintRequestAndResponseMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:54\\\\nmiddleware.RemoteServiceCallLogMW.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:145\\\\nmiddleware.ContextStressKeyMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:71\\\\nmiddleware.KCtxMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:100\\\\nmiddleware.PrintPanicStackMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:128\\\\nmiddleware.MultiBranchMiddleware.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:191\\\\nmiddleware.InitMetadataSdkCacheVersion.func1\\\\n    /opt/tiger/compile_path/src/code.byted.org/apaas/fdl/infra/middleware/middleware.go:182\\\"},\\\"KStatusCode\\\":\\\"702112103\\\"}} ]}]}";

        CozeErrorCode errorCode =  new CozeErrorCode(error);

        errorCode.getMsg();


    }

}
