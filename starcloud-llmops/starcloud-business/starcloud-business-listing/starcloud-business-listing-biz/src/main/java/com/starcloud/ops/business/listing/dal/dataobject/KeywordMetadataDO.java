package com.starcloud.ops.business.listing.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@TableName("llm_keyword_metadata")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class KeywordMetadataDO  extends BaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 站点
     */
    private String market;
    /**
     * 站点
     */
    private Long marketId;

    /**
     * 数据网址
     */
    private String website;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 关键词-中文
     */
    private String keywordCn;
    /**
     * 关键词-日文
     */
    private String keywordJp;

    /**
     * 分类
     */
    private String departments;

    private String trends;

    /**
     * 数据时间
     */
    private String month;

    /**
     * 月搜索量 注意 日平均前端计算
     */
    private Long searches;

    /**
     * 月购买量
     */
    private Long purchases;

    /**
     * 月购买量比列
     */
    private Double purchaseRate;

    /**
     * 具体的点击和转化数据
     */
    private String monopolyAsinDtos;

    /**
     * 点击集中度
     */
    private Double monopolyClickRate;

    /**
     * 前 10 产品信息
     */
    private String gkDatas;

    /**
     * 商品数
     */
    private Long products;


    /**
     * 广告竞品数
     */
    private Long adProducts;

    /**
     * 供需比
     */
    private Double supplyDemandRatio;

    /**
     * 预估 价格分布 判断哪个价格区间可能还有机会(价格差异化)，以及哪个价格区间竞争最为激烈
     */
    private Double avgPrice;

    /**
     * 评分数分布：说明该市场打造新品的难度，如果中低评分数区间占比较大，说明新品进入壁垒不高
     */
    private Long avgReviews;

    /**
     * 评分值分布：说明该市场的成熟度，如果4.5以上的商品数很多，说明该市场很成熟，通过商品差异性建立竞争壁垒难度较大；如果3.5分商品很多，可能存在改进空间
     */
    private Double avgRating;

    /**
     * PPC 竞价
     */
    private Double bid;
    /**
     * PPC 竞价 范围最小值
     */
    private Double bidMin;

    /**
     * PPC 竞价 范围最大值
     */
    private Double bidMax;

    /**
     * '关键词单词数'
     */
    private Integer wordCount;

    /**
     * 标题密度
     */
    private Long titleDensity;


    /**
     * SPR
     */
    private Long spr;

    /**
     * ABA 周排名
     */
    private Long searchRank;

    /**
     * '可以在亚马逊搜索'
     */
    private Boolean amazonChoice;


    /**
     * '每周搜索量'
     */
    private Long searchWeeklyRank;

//    /**
//     * ABA 排名的数据时间
//     */
//    private Long searchRankTimeFrom;
//    private Long searchRankTimeTo;

    /**
     * 数据更新时间
     */
    private Long dataUpdatedTime;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 状态
     */
    private Integer status;


}
