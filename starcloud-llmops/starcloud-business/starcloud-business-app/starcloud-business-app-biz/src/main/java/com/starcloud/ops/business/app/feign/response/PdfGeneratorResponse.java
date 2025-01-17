package com.starcloud.ops.business.app.feign.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class PdfGeneratorResponse implements Serializable {

    private static final long serialVersionUID = -7317991058511614021L;

    private String uid;

    private String url;

    private String type;
}
