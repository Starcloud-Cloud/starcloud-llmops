package com.starcloud.ops.business.app.controller.admin.comment;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsPageReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsRespVO;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.service.comment.MediaCommentsActionService;
import com.starcloud.ops.business.app.service.comment.MediaCommentsService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;


@Tag(name = "管理后台 - 媒体评论")
@RestController
@RequestMapping("/marketing/media-comments")
@Validated
public class MediaCommentsController {

    @Resource
    private MediaCommentsService mediaCommentsService;

    @PostMapping("/create")
    @Operation(summary = "上传/创建媒体评论")
    public CommonResult<Long> createMediaComments(@Valid @RequestBody MediaCommentsSaveReqVO createReqVO) {
        return success(mediaCommentsService.createMediaComments(getLoginUserId(),createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体评论")
    public CommonResult<Boolean> updateMediaComments(@Valid @RequestBody MediaCommentsSaveReqVO updateReqVO) {
        mediaCommentsService.updateMediaComments(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体评论")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMediaComments(@RequestParam("id") Long id) {
        mediaCommentsService.deleteMediaComments(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得媒体评论")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MediaCommentsRespVO> getMediaComments(@RequestParam("id") Long id) {
        MediaCommentsDO mediaComments = mediaCommentsService.getMediaComments(id);
        MediaCommentsRespVO bean = BeanUtils.toBean(mediaComments, MediaCommentsRespVO.class);
        List<MediaCommentsActionDO> actionDOS = mediaCommentsService.getMediaCommentsActionListByCommentsId(bean.getId());
        bean.setActions(BeanUtils.toBean(actionDOS, MediaCommentsPageReqVO.Action.class));
        return success(bean);
    }

    @GetMapping("/page")
    @Operation(summary = "获得媒体评论分页")
    public CommonResult<PageResult<MediaCommentsPageReqVO>> getMediaCommentsPage(@Valid MediaCommentsPageReqVO pageReqVO) {
        PageResult<MediaCommentsDO> pageResult = mediaCommentsService.getMediaCommentsPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        PageResult<MediaCommentsPageReqVO> bean = BeanUtils.toBean(pageResult, MediaCommentsPageReqVO.class);

        bean.getList().stream().forEach(comment -> {
            List<MediaCommentsActionDO> actionDOS = mediaCommentsService.getMediaCommentsActionListByCommentsId(comment.getId());
            comment.setActions(BeanUtils.toBean(actionDOS, MediaCommentsPageReqVO.Action.class));
        });

        return success(bean);
    }

    // ==================== 子表（媒体评论操作） ====================

    @GetMapping("/media-comments-action/list-by-comments-id")
    @Operation(summary = "获得媒体评论操作列表")
    @Parameter(name = "commentsId", description = "评论编号")
    @PreAuthorize("@ss.hasPermission('marketing:media-comments:query')")
    public CommonResult<List<MediaCommentsActionDO>> getMediaCommentsActionListByCommentsId(@RequestParam("commentsId") Long commentsId) {
        return success(mediaCommentsService.getMediaCommentsActionListByCommentsId(commentsId));
    }

}