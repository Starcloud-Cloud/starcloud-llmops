package com.starcloud.ops.business.mission.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_xhs_note_detail")
public class XhsNoteDetailDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * xhs笔记id
     */
    private String noteId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String description;

    /**
     * 点赞数
     */
    private Integer likedCount;

    /**
     * 收藏数
     */
    private Integer collectedCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 分享数
     */
    private Integer shareCount;

    /**
     * 图片
     */
    private String imageList;

    /**
     * 视频
     */
    private String video;

    /**
     * 预结算时的价格明细
     */
    private String unitPrice;

    /**
     * 预结算金额
     */
    private BigDecimal amount;
}
