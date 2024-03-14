package com.starcloud.ops.business.app.service.xhs.huitun.dto;

import lombok.Data;

/**
 * 灰豚数据 小红书搜索 DTO
 */
@Data
public class NoteSearchDTO  {
    /**
     * 当前时间戳
     */
    private Long _t;

    /**
     * 页码
     */
    private Long page;

    /**
     * 每页大小
     */
    private Long pageSize;


    /**
     * 搜索范围
     * 1.笔记标题
     * 2.笔记内容
     * 3.笔记标签
     * 4.笔记图片
     * 5.作者评论
     * 6.笔记热评
     */
    private Long rangeList;


    // 笔记分类


    private Long sort;


    /**
     * 开始时间
     */
    private String dateStart;
    /**
     * 结束时间
     */
    private String dateEnd;











}
