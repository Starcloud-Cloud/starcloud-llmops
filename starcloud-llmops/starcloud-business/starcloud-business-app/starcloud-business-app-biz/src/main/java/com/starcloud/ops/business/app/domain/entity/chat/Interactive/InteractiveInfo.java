package com.starcloud.ops.business.app.domain.entity.chat.Interactive;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataBasicInfoVO;
import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
public class InteractiveInfo implements Serializable {

    private static DatasetSourceDataService datasetSourceDataService = SpringUtil.getBean(DatasetSourceDataService.class);

    private String id;

    private Boolean success;

    /**
     * url,pic,text
     */
    private String showType;

    private String tips;

    /**
     * 反馈状态，0 开始，1完成，3，失败？
     */
    private int status;

    private Date time;

    private Object input;

    private List<?> data;

    @JsonIgnore
    @JSONField(serialize = false)
    private BaseToolHandler toolHandler;

    private Integer errorCode;

    private String errorMsg;

    public String tool() {
        return this.toolHandler != null ? this.toolHandler.getUserName() : null;
    }

    public String toolDesc() {
        return this.toolHandler != null ? this.toolHandler.getUserDescription() : null;
    }

    /**
     * 文本内容卡片渲染
     *
     * @return
     */
    public static InteractiveInfo buildTips(String tips) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("tips");
        interactiveInfo.setSuccess(true);
        interactiveInfo.setTips(tips);

        return interactiveInfo;
    }

    /**
     * url 卡片，前端获取URL内的内容取渲染
     *
     * @param tips
     * @return
     */
    public static InteractiveInfo buildUrlCard(String tips) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("url");
        interactiveInfo.setSuccess(true);
        interactiveInfo.setTips(tips);

        return interactiveInfo;
    }


    /**
     * 图片卡片渲染
     *
     * @return
     */
    public static InteractiveInfo buildImgCard(String tips) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("img");
        interactiveInfo.setSuccess(true);
        interactiveInfo.setTips(tips);

        return interactiveInfo;
    }

    /**
     * 多字段+图片卡片渲染
     *
     * @return
     */
    public static InteractiveInfo buildPicCard(String tips) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("pic");
        interactiveInfo.setSuccess(true);

        return interactiveInfo;
    }


    /**
     * 文本内容卡片渲染
     *
     * @param text
     * @return
     */
    public static InteractiveInfo buildText(String text) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("text");
        interactiveInfo.setSuccess(true);

        return interactiveInfo;
    }


    /**
     * 文档内容，传到前端，做文档列表渲染
     *
     * @param matchQueryVO
     * @return
     */
    public static InteractiveInfo buildDocs(MatchQueryVO matchQueryVO) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("docs");
        interactiveInfo.setSuccess(true);

        List<Long> docIds = Optional.ofNullable(matchQueryVO).map(MatchQueryVO::getRecords).orElse(new ArrayList<>()).stream().map((recordDTO) -> {
            return Long.valueOf(recordDTO.getDocumentId());
        }).collect(Collectors.toList());

        //查出具体文档信息
        List<DatasetSourceDataBasicInfoVO> docs = datasetSourceDataService.getSourceDataListData(docIds);

        interactiveInfo.setData(Optional.ofNullable(matchQueryVO).map(MatchQueryVO::getRecords).orElse(new ArrayList<>()).stream().map((recordDTO) -> {

            DatasetSourceDataBasicInfoVO source = Optional.ofNullable(docs).orElse(new ArrayList<>()).stream().filter((dataBasicInfoVO) -> {
                return recordDTO.getDocumentId().equals(String.valueOf(dataBasicInfoVO.getId()));
            }).findFirst().orElse(null);

            InteractiveDoc docInteractiveInfo = new InteractiveDoc();

            if (source != null) {

                docInteractiveInfo.setId(source.getId());
                docInteractiveInfo.setPosition(recordDTO.getPosition());
                docInteractiveInfo.setScore(recordDTO.getScore());
                docInteractiveInfo.setDatasetId(recordDTO.getDatasetId());
                docInteractiveInfo.setName(source.getName());
                docInteractiveInfo.setType(source.getDataType());
                docInteractiveInfo.setUrl(source.getAddress());
                docInteractiveInfo.setDesc(source.getDescription());
                docInteractiveInfo.setUpdateTime(source.getUpdateTime());
            }

            return docInteractiveInfo;

        }).collect(Collectors.toList()));

        return interactiveInfo;
    }

}
