package com.starcloud.ops.business.listing.dto;

import com.starcloud.ops.business.listing.enums.KeywordBindTypeEnum;
import lombok.Data;

@Data
public class KeywordBindDTO {

    private Long dictId;

    private Long draftId;

    /**
     * {@link KeywordBindTypeEnum}
     */
    private String type;
}
