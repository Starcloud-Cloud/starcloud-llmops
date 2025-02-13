package com.starcloud.ops.business.app.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class TemplateErrorData {

    private String templateCode;

    private String demoId;
}
