package com.starcloud.ops.business.app.controller.admin.comment;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.comment.vo.comment.*;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.service.comment.MediaCommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
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
        return success(mediaCommentsService.createMediaComments(getLoginUserId(), createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体评论")
    public CommonResult<Boolean> updateMediaComments(@Valid @RequestBody MediaCommentsUpdateReqVO updateReqVO) {
        mediaCommentsService.updateMediaComments(getLoginUserId(), updateReqVO);
        return success(true);
    }


    @PostMapping("/get-match-success-list")
    @Operation(summary = "获取待评论列表")
    public CommonResult<List<MediaCommentsListRespVO>> getMatchSuccessList(@Valid @RequestBody MediaCommentsListReqVO reqVO) {
        // 获取匹配成功列表
        List<MediaCommentsDO> mediaCommentsDOS = mediaCommentsService.getMatchSuccessList(getLoginUserId(), reqVO);
        if (mediaCommentsDOS.isEmpty()) {
            return success(new ArrayList<MediaCommentsListRespVO>());
        }

        List<MediaCommentsListRespVO> bean = BeanUtils.toBean(mediaCommentsDOS, MediaCommentsListRespVO.class);

        bean.stream().forEach(comment -> {
            List<MediaCommentsActionDO> actionDOS = mediaCommentsService.getMediaCommentsActionListByCommentsId(comment.getId());
            comment.setActions(BeanUtils.toBean(actionDOS, MediaCommentsListRespVO.Action.class));
        });
        return success(bean);
    }

    @PostMapping("/update-action-execute-time")
    @Operation(summary = "更新预计执行时间")
    public CommonResult<Boolean> updateActionExecuteTime(@Valid @RequestBody MediaCommentsActionReqVO reqVO) {
        // 获取匹配成功列表
        mediaCommentsService.updateActionSendStatus(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/update-action-status")
    @Operation(summary = "更新发送状态")
    public CommonResult<Boolean> updateActionSendStatus(@Valid @RequestBody MediaCommentsActionReqVO reqVO) {
        // 获取匹配成功列表
        mediaCommentsService.updateActionSendStatus(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/manual-response")
    @Operation(summary = "手动回复媒体评论")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> manualMediaComments(@Valid @RequestBody MediaCommentsManualResponseReqVO responseReqVO) {

        mediaCommentsService.manualResponseMediaComments(getLoginUserId(), responseReqVO.getId(), responseReqVO.getResponseContent());
        return success(true);
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体评论")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMediaComments(@RequestParam("id") Long id) {
        mediaCommentsService.deleteMediaComments(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得媒体评论")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MediaCommentsRespVO> getMediaComments(@RequestParam("id") Long id) {
        MediaCommentsDO mediaComments = mediaCommentsService.getMediaComments(getLoginUserId(), id);
        MediaCommentsRespVO bean = BeanUtils.toBean(mediaComments, MediaCommentsRespVO.class);
        List<MediaCommentsActionDO> actionDOS = mediaCommentsService.getMediaCommentsActionListByCommentsId(bean.getId());
        bean.setActions(BeanUtils.toBean(actionDOS, MediaCommentsPageReqVO.Action.class));
        return success(bean);
    }

    @GetMapping("/page")
    @Operation(summary = "获得媒体评论分页")
    public CommonResult<PageResult<MediaCommentsPageReqVO>> getMediaCommentsPage(@Valid MediaCommentsPageReqVO pageReqVO) {
        PageResult<MediaCommentsDO> pageResult = mediaCommentsService.getMediaCommentsPage(getLoginUserId(), pageReqVO);
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