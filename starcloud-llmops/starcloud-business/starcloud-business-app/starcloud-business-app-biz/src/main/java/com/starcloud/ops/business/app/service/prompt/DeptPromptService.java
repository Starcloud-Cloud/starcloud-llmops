package com.starcloud.ops.business.app.service.prompt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.PromptBaseVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.DeptPromptModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.req.PromptPageReqVO;
import com.starcloud.ops.business.app.controller.admin.prompt.vo.resp.PromptRespVO;

import javax.validation.Valid;

public interface DeptPromptService {

    /**
     * 新增提示词
     * @param promptBaseVO
     * @return
     */
    PromptRespVO create(PromptBaseVO promptBaseVO);

    /**
     * 修改提示词
     * @param reqVO
     * @return
     */
    PromptRespVO modify(@Valid DeptPromptModifyReqVO reqVO);

    /**
     * 删除提示词
     * @param uid
     */
    void delete(String uid);

    /**
     * 分页查询提示词
     * @param reqVO
     * @return
     */
    PageResult<PromptRespVO> page(@Valid PromptPageReqVO reqVO);

    /**
     * 分页查询系统提示词
     * @param reqVO
     * @return
     */
    PageResult<PromptRespVO> sysPage(@Valid PromptPageReqVO reqVO);
}
