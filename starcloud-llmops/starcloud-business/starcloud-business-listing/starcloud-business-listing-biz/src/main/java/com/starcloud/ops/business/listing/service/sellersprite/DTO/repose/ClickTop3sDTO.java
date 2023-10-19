package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ClickTop3sDTO {
    private String asin;
    private String imageUrl;
    private Double clickRate;
    private Double conversionRate;
    private Object productTitle;
}