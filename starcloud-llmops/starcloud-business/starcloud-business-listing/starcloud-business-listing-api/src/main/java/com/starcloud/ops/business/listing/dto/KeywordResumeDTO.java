package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Data
@Schema(description = "关键词摘要")
public class KeywordResumeDTO {


    @Schema(description = "关键词")
    private String keyword;

    public KeywordResumeDTO(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 去重使用 只用keyword
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeywordResumeDTO that = (KeywordResumeDTO) o;
        return Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword);
    }
}
