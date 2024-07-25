package com.starcloud.ops.business.app.api.app.handler.ImageOcr;

import com.starcloud.ops.business.app.api.ocr.OcrResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HandlerResponse implements Serializable {

    private List<OcrResult> list;

}