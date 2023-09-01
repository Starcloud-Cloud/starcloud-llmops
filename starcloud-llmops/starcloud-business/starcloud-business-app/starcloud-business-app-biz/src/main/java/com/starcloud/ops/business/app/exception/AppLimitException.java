package com.starcloud.ops.business.app.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-01
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class AppLimitException extends RuntimeException {

    private static final long serialVersionUID = 666583625940820152L;


}
