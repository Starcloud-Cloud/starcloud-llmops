package com.starcloud.ops.business.app.controller.admin.xhs;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.NoteDetailQueryReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.app.service.xhs.XhsNoteDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/llm/xhs/note")
@Tag(name = "星河云海-小红书笔记", description = "小红书笔记")
public class XhsNoteDetailController {

    @Resource
    private XhsNoteDetailService detailService;

    @GetMapping("/detail/{noteId}")
    @Operation(summary = "小红书笔记内容")
    public CommonResult<XhsNoteDetailRespVO> detailById(@PathVariable("noteId")String noteId) {
        return CommonResult.success(detailService.selectByNoteId(noteId));
    }

    @PostMapping("/detail")
    @Operation(summary = "小红书笔记内容")
    public CommonResult<XhsNoteDetailRespVO> detailByUrl(@Valid @RequestBody NoteDetailQueryReqVO reqVO) {
        return CommonResult.success(detailService.selectByNoteUrl(reqVO.getNoteUrl()));
    }

}
