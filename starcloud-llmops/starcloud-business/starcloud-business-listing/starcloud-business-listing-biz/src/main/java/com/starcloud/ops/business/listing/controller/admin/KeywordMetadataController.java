package com.starcloud.ops.business.listing.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.business.listing.controller.admin.vo.request.QueryKeywordMetadataPageReqVO;
import com.starcloud.ops.business.listing.controller.admin.vo.request.SellerSpriteListingVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.ExtendAsinReposeExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataBasicRespVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.KeywordMetadataRespVO;
import com.starcloud.ops.business.listing.convert.KeywordMetadataConvert;
import com.starcloud.ops.business.listing.service.KeywordMetadataService;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.ExtendAsinReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.repose.PrepareReposeDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.ExtendAsinRequestDTO;
import com.starcloud.ops.business.listing.service.sellersprite.DTO.request.PrepareRequestDTO;
import com.starcloud.ops.business.user.enums.LevelRightsLimitEnums;
import com.starcloud.ops.business.user.framework.rights.core.rightslimit.RightsLimit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/listing/KeywordMetadata")
@Tag(name = "关键词原数据", description = "关键词原数据")
public class KeywordMetadataController {

    @Resource
    private KeywordMetadataService keywordMetadataService;

    @Resource
    private SmsSendApi smsSendApi;


    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;

    @PutMapping("/add")
    @Operation(summary = "新增关键词", description = "新增关键词")
    public CommonResult<Boolean> create(@RequestParam() List<String> keywords,String marketName) {
        return CommonResult.success(keywordMetadataService.addMetaData(keywords,marketName));
    }


    @GetMapping("/basic")
    @Operation(summary = "获取关键词列表", description = "获取关键词列表")
    public CommonResult<List<KeywordMetadataBasicRespVO>> getKeywordsBasic(@RequestParam() List<String> keywords, String marketName) {
        return CommonResult.success(keywordMetadataService.getKeywordsBasic(keywords,marketName));
    }


    @GetMapping("/page")
    @Operation(summary = "分页查询关键词列表", description = "分页查询关键词列表")
    public CommonResult<PageResult<KeywordMetadataRespVO>> page(@Validated @RequestBody QueryKeywordMetadataPageReqVO pageReqVO) {
//        sendMessage(1000);
        pageReqVO.setMarketName("US");
        return CommonResult.success(keywordMetadataService.queryMetaData(pageReqVO));
    }

    @GetMapping("/Listing")
    @Operation(summary = "根据 ASIN 获取 Listing 数据", description = "根据 ASIN 获取 Listing 数据")
    public CommonResult<SellerSpriteListingVO> Listing(@RequestParam("asin") String asin, @RequestParam("marketName") String marketName) {
        return CommonResult.success(keywordMetadataService.getListingByAsin(asin, marketName));
    }



    @GetMapping("/extendPrepare")

    @Operation(summary = " 根据 ASIN 获取变体", description = "根据 ASIN 获取变体")
    public CommonResult<PrepareReposeDTO> extendPrepare(@Validated PrepareRequestDTO prepareRequestDTO) {
        return CommonResult.success(keywordMetadataService.extendPrepare(prepareRequestDTO));
    }



    @GetMapping("/extendAsin")
    @Operation(summary = "根据asin 获取拓展词数据", description = "根据asin 获取拓展词数据")
    public CommonResult<ExtendAsinReposeDTO> extendAsin(@Validated ExtendAsinRequestDTO extendAsinRequestDTO) {
        return CommonResult.success(keywordMetadataService.extendAsin(extendAsinRequestDTO));
    }


    @GetMapping("/exportExtendAsin")
    @Operation(summary = "根据asin 导出拓展词数据", description = "根据asin 导出拓展词数据")
    public CommonResult<ExtendAsinReposeDTO> exportExtendAsin(HttpServletResponse response, @Validated ExtendAsinRequestDTO extendAsinRequestDTO) throws IOException {

        ExtendAsinReposeDTO extendAsinReposeDTO = keywordMetadataService.extendAsin(extendAsinRequestDTO);

        // 导出 Excel
        List<ExtendAsinReposeExcelVO> datas = KeywordMetadataConvert.INSTANCE.convertExcelVOList(extendAsinReposeDTO.getItems());
        ExcelUtils.write(response, "Asin扩展结果.xls", "数据", ExtendAsinReposeExcelVO.class, datas);
        return CommonResult.success(keywordMetadataService.extendAsin(extendAsinRequestDTO));
    }

}
