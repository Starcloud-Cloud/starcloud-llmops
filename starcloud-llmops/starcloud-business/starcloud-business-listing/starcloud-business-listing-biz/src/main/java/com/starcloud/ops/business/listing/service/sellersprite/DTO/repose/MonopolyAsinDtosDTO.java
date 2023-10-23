package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MonopolyAsinDtosDTO {
    private String asin;
    private Object title;
    private String imageUrl;
    private Double clickRate;
    private Double conversionShareRate;
}