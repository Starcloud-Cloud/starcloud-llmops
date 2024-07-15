package com.starcloud.ops.business.app.model.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "拓展内容")
public class CreativeContentExecuteExtend implements java.io.Serializable {

    private static final long serialVersionUID = 3901716136527628939L;

    /**
     * 拓展内容
     *
     * @return 扩展内容
     */
    public static CreativeContentExecuteExtend of() {
        return new CreativeContentExecuteExtend();
    }
}
