package com.starcloud.ops.business.app.feign.dto.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "VideoMergeConfig", description = "视频合并配置类")
public class VideoMergeConfig {
    private String id;

    @NotNull(message = "待合并的视频配置不可以为空")
    private List<Videos> videos;


    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static
    class Videos {
        private String url;
        private String type;

    }

}
