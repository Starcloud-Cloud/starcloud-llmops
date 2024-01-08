package com.starcloud.ops.business.app.convert.xhs.scheme;

import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.AssembleSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.ContentSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.ParagraphSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.AssembleSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.BaseSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.ContentSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.ParagraphSchemeStepEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.PosterSchemeStepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Mapper
public interface CreativeSchemeStepConvert {

    CreativeSchemeStepConvert INSTANCE = Mappers.getMapper(CreativeSchemeStepConvert.class);

    /**
     * 转换方案步骤DTO
     *
     * @param schemeStep 方案步骤实体
     * @return 转换方案步骤DTO
     */
    default BaseSchemeStepDTO convert(BaseSchemeStepEntity schemeStep) {
        if (schemeStep instanceof ContentSchemeStepEntity) {
            return convertContent((ContentSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof ParagraphSchemeStepEntity) {
            return convertParagraph((ParagraphSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof AssembleSchemeStepEntity) {
            return convertAssemble((AssembleSchemeStepEntity) schemeStep);
        }
        if (schemeStep instanceof PosterSchemeStepEntity) {
            return convertPoster((PosterSchemeStepEntity) schemeStep);
        }
        throw new RuntimeException("不支持的方案步骤");
    }

    /**
     * 内容方案实体转换为内容方案DTO
     *
     * @param schemeStep 内容方案实体
     * @return 内容方案DTO
     */
    ContentSchemeStepDTO convertContent(ContentSchemeStepEntity schemeStep);

    /**
     * 段落方案实体转换为段落方案DTO
     *
     * @param schemeStep 段落方案实体
     * @return 段落方案DTO
     */
    ParagraphSchemeStepDTO convertParagraph(ParagraphSchemeStepEntity schemeStep);

    /**
     * 拼接方案实体转换为拼接方案DTO
     *
     * @param schemeStep 拼接方案实体
     * @return 拼接方案DTO
     */
    AssembleSchemeStepDTO convertAssemble(AssembleSchemeStepEntity schemeStep);

    /**
     * 图片方案实体转换为图片方案DTO
     *
     * @param schemeStep 图片方案实体
     * @return 图片方案DTO
     */
    PosterSchemeStepDTO convertPoster(PosterSchemeStepEntity schemeStep);

    /**
     * 转换方案步骤DTO
     *
     * @param schemeStep 方案步骤DTO
     * @return 方案步骤实体
     */
    default BaseSchemeStepEntity convert(BaseSchemeStepDTO schemeStep) {
        if (schemeStep instanceof ContentSchemeStepDTO) {
            return convertContent((ContentSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof ParagraphSchemeStepDTO) {
            return convertParagraph((ParagraphSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof AssembleSchemeStepDTO) {
            return convertAssemble((AssembleSchemeStepDTO) schemeStep);
        }
        if (schemeStep instanceof PosterSchemeStepDTO) {
            return convertPoster((PosterSchemeStepDTO) schemeStep);
        }
        throw new RuntimeException("不支持的方案步骤");
    }

    /**
     * 内容方案DTO转换为内容方案实体
     *
     * @param schemeStep 内容方案DTO
     * @return 内容方案实体
     */
    ContentSchemeStepEntity convertContent(ContentSchemeStepDTO schemeStep);

    /**
     * 段落方案DTO转换为段落方案实体
     *
     * @param schemeStep 段落方案DTO
     * @return 段落方案实体
     */
    ParagraphSchemeStepEntity convertParagraph(ParagraphSchemeStepDTO schemeStep);

    /**
     * 拼接方案DTO转换为拼接方案实体
     *
     * @param schemeStep schemeStep
     * @return 拼接方案实体
     */
    AssembleSchemeStepEntity convertAssemble(AssembleSchemeStepDTO schemeStep);

    /**
     * 图片方案DTO转换为图片方案实体
     *
     * @param schemeStep 图片方案DTO
     * @return 图片方案实体
     */
    PosterSchemeStepEntity convertPoster(PosterSchemeStepDTO schemeStep);

}
