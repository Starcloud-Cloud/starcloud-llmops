package com.starcloud.ops.business.app.dal.databoject.xhs.content;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.app.enums.xhs.content.XhsCreativeContentStatusEnums;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_creative_content")
public class CreativeContentDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("uid")
    private String uid;

    /**
     * 计划uid
     */
    private String planUid;

    /**
     * 创作方案UID
     */
    private String schemeUid;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 业务uid
     */
    private String businessUid;


    /**
     * 使用的图片 文案模板Uid
     */
    private String tempUid;

    /**
     * 使用的图片列表 List<String>
     */
    private String usePicture;

    /**
     * 执行参数 json
     */
    private String executeParams;

    /**
     * 执行状态 {@link XhsCreativeContentStatusEnums}
     */
    private String status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行耗时
     */
    private Long executeTime;

    /**
     * 文案标题
     */
    private String copyWritingTitle;

    /**
     * 文案内容
     */
    private String copyWritingContent;

    /**
     * 文案字数
     */
    private Integer copyWritingCount;

    /**
     * 文案生成结果
     */
    private String copyWritingResult;

    /**
     * 生成图片数量
     */
    private Integer pictureNum;

    /**
     * 生成图片结果  List<XhsCreativePictureContentDTO>
     */
    private String pictureContent;

    /**
     * 失败信息
     */
    private String errorMsg;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 拓展信息  执行结果  json
     */
    private String extend;

    /**
     * 是否绑定
     */
    private Boolean claim;

    /**
     * 是否喜欢
     */
    private Boolean liked;

}
