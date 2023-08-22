package com.starcloud.ops.business.app.domain.entity.chat.Interactive;

import com.starcloud.ops.business.dataset.pojo.dto.RecordDTO;
import com.starcloud.ops.business.dataset.pojo.response.MatchQueryVO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class InteractiveInfo {

    private String id;

    private Boolean success;

    /**
     * url,pic,text
     */
    private String showType;

    private String tips;

    private String title;

    private String subTitle;

    private String picUrl;

    private String url;

    /**
     * 反馈状态，0 开始，1完成，3，失败？
     */
    private int status;

    private Date time;

    private Object data;

    private String errorCode;

    private String errorMsg;


    /**
     * url 卡片，前端获取URL内的内容取渲染
     *
     * @param url
     * @return
     */
    public static InteractiveInfo buildUrlCard(String url) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("url");
        interactiveInfo.setSuccess(true);
        interactiveInfo.setUrl(url);

        return interactiveInfo;
    }


    /**
     * 多字段+图片卡片渲染
     *
     * @return
     */
    public static InteractiveInfo buildPicCard(String title, String subTitle, String picUrl, String url) {
        InteractiveInfo interactiveInfo = new InteractiveInfo();

        interactiveInfo.setShowType("pic");
        interactiveInfo.setSuccess(true);
        interactiveInfo.setTitle(title);
        interactiveInfo.setSubTitle(subTitle);
        interactiveInfo.setPicUrl(picUrl);
        interactiveInfo.setUrl(url);

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
        interactiveInfo.setData(text);

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
        interactiveInfo.setData(Optional.ofNullable(matchQueryVO.getRecords()).orElse(new ArrayList<>()).stream().map((recordDTO) -> {

            RecordDTO recordDTO1 = new RecordDTO();

            recordDTO1.setId(recordDTO.getId());
            recordDTO1.setScore(recordDTO.getScore());
            recordDTO1.setDatasetId(recordDTO.getDatasetId());
            recordDTO1.setDocumentId(recordDTO.getDocumentId());
            recordDTO1.setPosition(recordDTO.getPosition());
            recordDTO1.setStatus(recordDTO.getStatus());
            recordDTO1.setContent(recordDTO.getContent());
            recordDTO1.setUpdateTime(recordDTO.getUpdateTime());

            return recordDTO1;
        }).collect(Collectors.toList()));

        return interactiveInfo;
    }

}
