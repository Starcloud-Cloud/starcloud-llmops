package com.starcloud.ops.business.app.domain.entity.chat.Interactive;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InteractiveInfo {

    private String id;

    private Boolean success;

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

    private Object data;


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

}
