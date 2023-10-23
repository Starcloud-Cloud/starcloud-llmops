package com.starcloud.ops.business.listing.service.sellersprite.DTO.repose;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filename:     starcloud-llmops
 * Description:  com.starcloud.ops.business.listing.service.sellersprite.DTO.repose
 * Company:      mdc.ai Inc.
 *
 * @Author: djl
 * @version: 1.0
 * Create at:    2023/10/18  16:11
 * Modification History:
 * Date          Author      Version     Description
 * ------------------------------------------------------------------
 * 2023/10/18   AlanCusack    1.0         1.0 Version
 */
@NoArgsConstructor
@Data
public class KeywordMinerReposeDTO {


    private Integer page;
    private Integer size;
    private Integer total;
    private List<ItemsDTO> items;
    private Integer took;
}
