package com.starcloud.ops.business.app.service.template;

import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.TemplateRecordRespVO;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TemplateRecordService {
    /**
     * 新增记录
     */
    void addRecord(List<PosterStyleDTO> posterStyleDTOList);

    /**
     * 当前用户的所有使用记录
     */
    List<TemplateRecordRespVO> listRecord();

    /**
     * 校验模板数量
     */
    void checkRecordNum(List<PosterStyleDTO> posterStyleDTOList);

}
