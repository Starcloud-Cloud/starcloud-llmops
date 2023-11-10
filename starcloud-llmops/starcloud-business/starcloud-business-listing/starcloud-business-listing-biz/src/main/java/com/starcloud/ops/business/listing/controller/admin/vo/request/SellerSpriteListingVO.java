package com.starcloud.ops.business.listing.controller.admin.vo.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 卖家精灵 Listing VO
 */
@NoArgsConstructor
@Data
public class SellerSpriteListingVO {

    private String market;
    private String asin;
    private List<String> imgUrls;
    private String title;
    private List<String> features;
    private String description;
}
