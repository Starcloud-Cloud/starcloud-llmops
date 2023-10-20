package com.starcloud.ops.business.listing.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictCreateReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictModifyReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.DictPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DictRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.service.DictService;
import com.starcloud.ops.business.listing.service.KeywordMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/listing/KeywordMetadata")
@Tag(name = "关键词原数据", description = "关键词原数据")
public class KeywordMetadataController {

    @Resource
    private KeywordMetadataService keywordMetadataService;

    @PutMapping("/add")
    @Operation(summary = "新增关键词", description = "新增关键词")
    public CommonResult<Boolean> create(@RequestParam() List<String> keywords,String marketName) {
        return CommonResult.success(keywordMetadataService.addMetaData(keywords,marketName));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询关键词列表", description = "分页查询关键词列表")
    public CommonResult<PageResult<KeywordMetadataRespVO>> page(@Valid QueryKeywordMetadataPageReqVO pageReqVO) {
        return CommonResult.success(keywordMetadataService.queryMetaData(pageReqVO));
    }

}
