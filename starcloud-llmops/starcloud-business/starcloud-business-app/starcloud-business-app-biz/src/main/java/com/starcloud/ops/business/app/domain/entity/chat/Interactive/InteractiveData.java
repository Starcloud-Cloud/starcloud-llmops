package com.starcloud.ops.business.app.domain.entity.chat.Interactive;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InteractiveData implements Serializable {

    /**
     * url,pic,text
     */
    private String showType;


    private String title;

    private String subTitle;

    private String imageUrl;

    private String url;

    private String content;

    private String time;
}
