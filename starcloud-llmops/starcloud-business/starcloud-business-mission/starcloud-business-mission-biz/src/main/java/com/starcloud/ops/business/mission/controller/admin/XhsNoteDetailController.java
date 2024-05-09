package com.starcloud.ops.business.mission.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.NoteDetailQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
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
    public CommonResult<XhsNoteDetailRespVO> detailById(@PathVariable("noteId") String noteId) {
        return CommonResult.success(detailService.selectByNoteId(noteId));
    }

    @PostMapping("/detail")
    @Operation(summary = "小红书笔记内容")
    public CommonResult<AbstractCreativeMaterialDTO> detailByUrl(@Valid @RequestBody NoteDetailQueryReqVO reqVO) {
        return CommonResult.success(detailService.mapMaterialDetail(reqVO.getNoteUrl(), reqVO.getMaterialType()));
    }

}
