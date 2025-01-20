package com.starcloud.ops.business.app.feign.request.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImagePdfRequest implements Serializable {

    private static final long serialVersionUID = -7063720976257890507L;

    private List<String> imageUrlList;

    private Boolean isAddVideoQrCode;

    private Boolean isAddAudioQrCode;

    private String videoQrCode;

    private String audioQrCode;

    private String qrCodeLocation;

    private Integer qrCodePadding;

}
