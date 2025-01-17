package com.starcloud.ops.business.app.feign.request.video;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class WordbookPdfRequest implements Serializable {

    private static final long serialVersionUID = 3687999754376432422L;

    private List<String> wordbookImageUrlList;

}
